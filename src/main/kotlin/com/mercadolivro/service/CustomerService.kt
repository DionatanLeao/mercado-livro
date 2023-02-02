package com.mercadolivro.service

import com.mercadolivro.enums.CustomerStatus
import com.mercadolivro.enums.Errors
import com.mercadolivro.enums.Role
import com.mercadolivro.exception.NotFoundException
import com.mercadolivro.model.CustomerModel
import com.mercadolivro.repository.CustomerRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class CustomerService(
    private val repository: CustomerRepository,
    private val bookService: BookService,
    private val bCrypt: BCryptPasswordEncoder
) {

    fun create(customer: CustomerModel) {
        val customerCopy = customer.copy(
            roles = setOf(Role.CUSTOMER),
            password = bCrypt.encode(customer.password)
        )
        repository.save(customerCopy)
    }

    fun getAll(name: String?): List<CustomerModel> {
        name?.let {
            return repository.findByNameContaining(it)
        }
        return repository.findAll().toList()
    }

    fun findById(id: Int): CustomerModel {
        return repository.findById(id).orElseThrow{ NotFoundException(Errors.ML101.message.format(id), Errors.ML101.code) }
    }

    fun update(customer: CustomerModel) {
        if (!repository.existsById(customer.id!!)) {
            throw Exception()
        }
        repository.save(customer)
    }

    fun delete(id: Int) {
        val customer = findById(id)
        bookService.deleteByCustomer(customer)
        customer.status = CustomerStatus.INATIVO
        repository.save(customer)
    }

    fun emailAvailable(email: String): Boolean {
        return !repository.existsByEmail(email)
    }
}