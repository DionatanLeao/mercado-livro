package com.mercadolivro.controller.request

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty

data class PostCustomerRequest(
    @field:NotEmpty(message = "Name must be informed")
    var name: String,
    @field:Email(message = "E-mail must be valid")
    var email: String
)