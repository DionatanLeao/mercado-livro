package com.mercadolivro.controller.request

import com.fasterxml.jackson.annotation.JsonAlias
import java.math.BigDecimal
import javax.validation.constraints.NotEmpty

class PostBookRequest(
    @field:NotEmpty(message = "Name must be informed")
    var name: String,

    @field:NotEmpty(message = "Price must be valid")
    var price: BigDecimal,

    @JsonAlias("customer_id")
    val customerId: Int
)