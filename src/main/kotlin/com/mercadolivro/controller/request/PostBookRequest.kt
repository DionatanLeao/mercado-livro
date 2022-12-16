package com.mercadolivro.controller.request

import com.fasterxml.jackson.annotation.JsonAlias
import java.math.BigDecimal

class PostBookRequest(
    var name: String,
    var price: BigDecimal,
    @JsonAlias("customer_id")
    val customerId: Int
)