package com.mercadolivro.controller

import com.mercadolivro.controller.request.PostCustomerRequest
import com.mercadolivro.controller.request.PutCustomerRequest
import com.mercadolivro.controller.response.CustomerResponse
import com.mercadolivro.extension.toCustomerModel
import com.mercadolivro.extension.toResponse
import com.mercadolivro.model.CustomerModel
import com.mercadolivro.service.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/customers")
class CustomerController(
    val service: CustomerService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody customer: PostCustomerRequest) {
        service.create(customer.toCustomerModel())
    }

    @GetMapping
    fun getAll(@RequestParam name: String?): List<CustomerResponse> {
        return service.getAll(name).map { it.toResponse() }
    }

    @GetMapping("/{id}")
    fun getCustomer(@PathVariable id: Int): CustomerResponse {
        return service.findById(id).toResponse()
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@PathVariable id: Int, @RequestBody customer: PutCustomerRequest) {
        val customerSaved = service.findById(id)
        service.update(customer.toCustomerModel(customerSaved))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Int) {
        service.delete(id)
    }

}