package com.mercadolivro.controller

import com.mercadolivro.controller.request.PostCustomerRequest
import com.mercadolivro.model.CustomerModel
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/customers")
class CustomerController {

    @GetMapping
    fun getCustomer(): CustomerModel {
        return CustomerModel(id = "1", name = "Name 1", email = "email@email.com")
    }

    @PostMapping
    fun create(@RequestBody customer: PostCustomerRequest) {
        println(customer)
    }

}