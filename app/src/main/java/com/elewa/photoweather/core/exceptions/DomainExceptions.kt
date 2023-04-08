package com.elewa.photoweather.core.exceptions

sealed class DomainExceptions:Throwable() {

    object UnknownException:DomainExceptions()

}