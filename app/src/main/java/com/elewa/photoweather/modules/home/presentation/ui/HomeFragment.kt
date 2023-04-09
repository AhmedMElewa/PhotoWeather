package com.elewa.photoweather.modules.home.presentation.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.display.DisplayManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.core.Camera
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowMetricsCalculator
import com.elewa.photoweather.R
import com.elewa.photoweather.base.BaseFragment
import com.elewa.photoweather.databinding.FragmentHomeBinding
import com.elewa.photoweather.extentions.*
import com.elewa.photoweather.modules.home.presentation.uimodel.HomeSideEffect
import com.elewa.photoweather.modules.home.presentation.uimodel.HomeUiState
import com.elewa.photoweather.modules.home.presentation.uimodel.ImageUiModel
import com.elewa.photoweather.modules.home.presentation.uimodel.WeatherUiModel
import com.elewa.photoweather.modules.home.presentation.viewmodel.HomeViewModel
import com.elewa.photoweather.modules.home.util.Constants.Companion.CameraXTAG
import com.elewa.photoweather.modules.home.util.Constants.Companion.FILENAME
import com.elewa.photoweather.modules.home.util.Constants.Companion.PHOTO_EXTENSION
import com.elewa.photoweather.modules.home.util.Constants.Companion.RATIO_16_9_VALUE
import com.elewa.photoweather.modules.home.util.Constants.Companion.RATIO_4_3_VALUE
import com.elewa.photoweather.modules.home.util.Constants.Companion.createFile
import com.elewa.photoweather.modules.home.util.Constants.Companion.getOutputDirectory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private var currentImage: ImageUiModel? = null

    override val bindLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (hasCamera() && hasAccessAresLocation()) {
                initView()
            } else {
                errorText(getString(R.string.permission_required))
            }
        }

    //camerax
    private val displayManager by lazy {
        requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var cameraExecutor: ExecutorService
    private var displayId: Int = -1
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private lateinit var windowManager: WindowInfoTracker
    private lateinit var outputDirectory: File
    private var preview: Preview? = null
    private lateinit var photoFile: File
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK

    private var space = 0

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit
        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
            if (displayId == this@HomeFragment.displayId) {
                Log.d(CameraXTAG, "Rotation changed: ${view.display.rotation}")
                imageCapture?.targetRotation = view.display.rotation
                imageAnalyzer?.targetRotation = view.display.rotation
            }
        } ?: Unit
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermissions()
    }

    private fun initView() {
        initCamera()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.imgHistory.setOnClickListener {
            currentImage = null
            val action =
                HomeFragmentDirections.actionHomeFragmentToHistoryFragment()
            findNavController().navigate(action)
        }

        binding.swipeToRefresh.setOnRefreshListener {
            checkWhatIsNotAvailable()
        }

        initObservers()
        initEffectObservation()

    }

    private fun checkWhatIsNotAvailable() {
        binding.swipeToRefresh.isRefreshing = false
        if (hasCamera() && hasAccessAresLocation()) {
            if (requireContext().isLocationEnabled()) {
                if (requireContext().isOnline()) {
                    if (currentImage != null) {
                        binding.txtError.toGone()
                        getLocation()
                        binding.swipeToRefresh.isEnabled = false

                    }
                }
            }
        }
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is HomeUiState.Empty -> {
                            binding.progressBlue.toGone()
                            currentImage = null
                        }
                        is HomeUiState.Loading -> {
                            if (state.loading) {
                                binding.progressBlue.toVisible()
                            } else {
                                binding.progressBlue.toGone()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initEffectObservation() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiEffects.collectLatest { effect ->
                when (effect) {
                    is HomeSideEffect.Error -> {
                        binding.progressBlue.toGone()
                        Toast.makeText(
                            requireContext(),
                            getString(effect.message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is HomeSideEffect.ImageSaved -> {
                        currentImage = effect.imgState
                        getLocation()
                    }
                    is HomeSideEffect.WeatherLoaded -> {

                        try {
                            val file: File = File(currentImage?.imgPath)
                            var uri: Uri = Uri.fromFile(file)
                            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                ImageDecoder.decodeBitmap(
                                    ImageDecoder.createSource(
                                        requireContext().contentResolver,
                                        uri
                                    )
                                )
                            } else {
                                MediaStore.Images.Media.getBitmap(
                                    requireContext().contentResolver,
                                    uri
                                )
                            }
                            binding.progressBlue.toVisible()
                            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                drawWeatherData(bitmap, effect.weatherState)
                            }

                        } catch (e: java.lang.Exception) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.generic_unknown_error),
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.progressBlue.toGone()
                        }

                    }
                }
            }
        }
    }


    private fun errorText(value: String) {
        binding.txtError.text = value
        binding.txtError.toVisible()
        checkWhatIsNotAvailable()
    }


    private fun hasCamera() =
        ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun hasAccessAresLocation() =
        ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED


    private fun requestPermissions() {
        var permissionsToReques = mutableListOf<String>()
        if (!hasCamera()) {
            permissionsToReques.add(Manifest.permission.CAMERA)
        }
        if (!hasAccessAresLocation()) {
            permissionsToReques.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (permissionsToReques.isNotEmpty()) {
            requestPermission.launch(permissionsToReques.toTypedArray())
        } else {
            initView()

        }
    }

    private fun initCamera() {
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()


        // Every time the orientation of device changes, update rotation for use cases
        displayManager.registerDisplayListener(displayListener, null)

//        //Initialize WindowManager to retrieve display metrics
        windowManager = WindowInfoTracker.getOrCreate((requireContext()))

        // Determine the output directory
        outputDirectory = getOutputDirectory(requireContext())

        // Wait for the views to be properly laid out
        binding.viewFinder.post {

            // Keep track of the display in which this view is attached
            displayId = binding.viewFinder.display.displayId

            // Build UI controls
            updateCameraUi()

            // Set up the camera and its use cases
            setUpCamera()
        }
    }


    private fun updateCameraUi() {

        // Listener for button used to capture photo
        binding.imgTakePhoto.setOnClickListener {

            // Get a stable reference of the modifiable image capture use case
            imageCapture?.let { imageCapture ->

                // Create output file to hold the image
                photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)

                // Setup image capture metadata
                val metadata = ImageCapture.Metadata().apply {

                    // Mirror image when using the front camera
                    isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
                }

                // Create output options object which contains file + metadata
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
                    .setMetadata(metadata)
                    .build()

                // Setup image capture listener which is triggered after photo has been taken
                imageCapture.takePicture(
                    outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                        override fun onError(exc: ImageCaptureException) {
                            Log.e(CameraXTAG, "Photo capture failed: ${exc.message}", exc)
                        }

                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                            Log.d(CameraXTAG, "Photo capture succeeded: $savedUri")

                            // We can only change the foreground Drawable using API level 23+ API
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                // Update the gallery thumbnail with latest picture taken

                                updateView((savedUri))
                            }

                            // Implicit broadcasts will be ignored for devices running API level >= 24
                            // so if you only target API level 24+ you can remove this statement
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                requireActivity().sendBroadcast(
                                    Intent(android.hardware.Camera.ACTION_NEW_PICTURE, savedUri)
                                )
                            }

                            // If the folder selected is an external media directory, this is
                            // unnecessary but otherwise other apps will not be able to access our
                            // images unless we scan them using [MediaScannerConnection]
                            val mimeType = MimeTypeMap.getSingleton()
                                .getMimeTypeFromExtension(savedUri.toFile().extension)
                            MediaScannerConnection.scanFile(
                                context,
                                arrayOf(savedUri.toFile().absolutePath),
                                arrayOf(mimeType)
                            ) { _, uri ->
                                Log.d(CameraXTAG, "Image capture scanned into media store: $uri")
                            }
                        }
                    })

            }
        }
    }

    private fun setUpCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({

            // CameraProvider
            cameraProvider = cameraProviderFuture.get()

            // Select lensFacing depending on the available cameras
            lensFacing = when {
                cameraProvider!!.hasBackCamera() -> CameraSelector.LENS_FACING_BACK
                cameraProvider!!.hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
                else -> throw IllegalStateException("Back and front camera are unavailable")
            }

            // Build and bind the camera use cases
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun updateView(url: Uri) {
        if (url != null) {
            viewModel.saveImage(url.path.toString())
        }
    }

    /** Declare and bind preview, capture and analysis use cases */
    private fun bindCameraUseCases() {

        // Get screen metrics used to setup camera for full screen resolution
        val metrics = WindowMetricsCalculator.getOrCreate()
            .computeCurrentWindowMetrics(requireActivity()).bounds
        Log.d(CameraXTAG, "Screen metrics: ${metrics.width()} x ${metrics.height()}")

        val screenAspectRatio =
            aspectRatio(metrics.width(), metrics.height(), RATIO_4_3_VALUE, RATIO_16_9_VALUE)
        Log.d(CameraXTAG, "Preview aspect ratio: $screenAspectRatio")

        val rotation = binding.viewFinder.display.rotation

        // CameraProvider
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        // Preview
        preview = Preview.Builder()
            // We request aspect ratio but no resolution
            .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation
            .setTargetRotation(rotation)
            .build()

        // ImageCapture
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            // We request aspect ratio but no resolution to match preview config, but letting
            // CameraX optimize for whatever specific resolution best fits our use cases
            .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            .setTargetRotation(rotation)
            .build()

        // ImageAnalysis
        imageAnalyzer = ImageAnalysis.Builder()
            // We request aspect ratio but no resolution
            .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            .setTargetRotation(rotation)
            .build()
            // The analyzer can then be assigned to the instance
            .also {
                it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                    // Values returned from our analyzer are passed to the attached listener
                    // We log image analysis results here - you should do something useful
                    // instead!
                    // Log.d(TAG, "Average luminosity: $luma")
                })
            }

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture, imageAnalyzer
            )

            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)
//            observeCameraState(camera?.cameraInfo!!)
        } catch (exc: Exception) {
            Log.e(CameraXTAG, "Use case binding failed", exc)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        displayManager.unregisterDisplayListener(displayListener)

    }

    private fun getLocation() {

        if ((ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            requestPermissions()
        } else {
            if (requireContext().isLocationEnabled()) {
                if (requireContext().isOnline()) {
                    mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                        val location: Location? = task.result
                        if (location != null) {
                            val geocoder = Geocoder(requireContext(), Locale.getDefault())
                            val list: MutableList<Address>? =
                                geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            var lat = "${list?.get(0)?.latitude}"
                            var lon = "${list?.get(0)?.longitude}"
                            var city = "${list?.get(0)?.countryName}"

                            viewModel.getWeather(lat, lon, city)


                        } else {
                            errorText(getString(R.string.internet_required))
                        }
                    }
                } else {
                    errorText(getString(R.string.internet_required))
                }
            } else {
                errorText(getString(R.string.location_required))
            }
        }
    }


    private fun drawWeatherData(capturedImageBitmap: Bitmap, weatherData: WeatherUiModel) {
        space = 0
        val newBitmap = capturedImageBitmap.copy(Bitmap.Config.ARGB_8888, true)
        var outputStream: FileOutputStream? = null
        val canvas = Canvas(newBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.BLUE
        paint.textSize = 120f
        var dataList = ArrayList<String>()
        dataList.add(weatherData.place.uppercase(Locale.ROOT))
        dataList.add(weatherData.temp.uppercase(Locale.ROOT))
        dataList.add(weatherData.description.uppercase(Locale.ROOT))
        for (text in dataList) canvas.drawText(
            text,
            (newBitmap.width / 8 - 100
                    ).toFloat(),
            (newBitmap.height / 8 + 130.let { space += it; space }).toFloat(),
            paint
        )
        val newModifiedCapturedImage: File = File(currentImage?.imgPath)
        if (!newModifiedCapturedImage.exists()) {
            try {
                newModifiedCapturedImage.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        try {
            outputStream = FileOutputStream(newModifiedCapturedImage)
            newBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush()
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                var file = File(currentImage?.imgPath)
                var uri = Uri.fromFile(file)
                if (currentImage != null) {
                    val action =
                        HomeFragmentDirections.actionHomeFragmentToImageViewFragment(
                            currentImage?.imgPath!!,
                            currentImage?.imgId!!
                        )
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                        binding.progressBlue.toGone()
                        findNavController().navigate(action)
                    }

                }
            }
        }
    }
}