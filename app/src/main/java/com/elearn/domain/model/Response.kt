package com.elearn.domain.model

data class HTTPResponse<T>(
    val message: String,
    val data: T
)