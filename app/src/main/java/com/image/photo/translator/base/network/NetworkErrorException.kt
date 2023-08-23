package com.image.photo.translator.base.network

public open class NetworkErrorException (val responseMessage: String? = null): Exception() {
}