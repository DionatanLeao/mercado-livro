package com.mercadolivro.controller.response

class ErrorResponse(
    var httpCode: Int,
    var message: String,
    var internalCode: String,
    var errors: List<FieldErrorResponse>?
)