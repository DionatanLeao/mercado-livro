package com.mercadolivro.service

import com.mercadolivro.enums.CustomerStatus
import com.mercadolivro.enums.Role
import com.mercadolivro.model.CustomerModel
import com.mercadolivro.repository.CustomerRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.UUID

@ExtendWith(MockKExtension::class)
class CustomerServiceTest {

    @MockK
    private lateinit var repository: CustomerRepository

    @MockK
    private lateinit var bookService: BookService

    @MockK
    private lateinit var bCrypt: BCryptPasswordEncoder

    @InjectMockKs
    private lateinit var customerService: CustomerService

    @Test
    fun `should return all customers`() {
        val fakeCustomers = listOf(buildCustomer(), buildCustomer())

        every { repository.findAll() } returns fakeCustomers

        val customers = customerService.getAll(null)

        assertEquals(fakeCustomers, customers)
        verify(exactly = 1) { repository.findAll() }
        verify(exactly = 0) { repository.findByNameContaining(any()) }
    }

    @Test
    fun `should return all customers when name is informed`() {
        val name = UUID.randomUUID().toString()
        val fakeCustomers = listOf(buildCustomer(), buildCustomer())

        every { repository.findByNameContaining(name) } returns fakeCustomers

        val customers = customerService.getAll(name)
        assertEquals(fakeCustomers, customers)
        verify(exactly = 1) { repository.findByNameContaining(name) }
        verify(exactly = 0) { repository.findAll() }
    }

    private fun buildCustomer(
            id: Int? = null,
            name: String = "customer name",
            email: String = "${UUID.randomUUID()}@email.com",
            password: String = "password"
    ) = CustomerModel(
        id = id,
        name = name,
        email = email,
        password = password,
        status = CustomerStatus.ATIVO,
        roles = setOf(Role.CUSTOMER)
    )
}