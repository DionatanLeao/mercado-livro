package com.mercadolivro.service

import com.mercadolivro.enums.CustomerStatus
import com.mercadolivro.enums.Errors
import com.mercadolivro.enums.Role
import com.mercadolivro.exception.NotFoundException
import com.mercadolivro.helper.buildCustomer
import com.mercadolivro.model.CustomerModel
import com.mercadolivro.repository.CustomerRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*
import kotlin.random.Random

@ExtendWith(MockKExtension::class)
class CustomerServiceTest {

    @MockK
    private lateinit var repository: CustomerRepository

    @MockK
    private lateinit var bookService: BookService

    @MockK
    private lateinit var bCrypt: BCryptPasswordEncoder

    @InjectMockKs
    @SpyK
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

    @Test
    fun `should create customer and encrypt password`() {
        val initialPassword = Math.random().toString()
        val fakeCustomer = buildCustomer(password = initialPassword)
        val fakePassword = UUID.randomUUID().toString()
        val fakeCustomerEncrypted = fakeCustomer.copy(password = fakePassword)

        every { repository.save(fakeCustomerEncrypted) } returns fakeCustomer
        every { bCrypt.encode(initialPassword) } returns fakePassword

        
        customerService.create(fakeCustomer)

        verify(exactly = 1) { repository.save(any()) }
        verify(exactly = 1) { bCrypt.encode(any()) }
    }

    @Test
    fun `should return customer by id`() {
        val id = Random.nextInt()
        val fakeCustomer = buildCustomer(id = id)

        every { repository.findById(id) } returns Optional.of(fakeCustomer)

        val customer = customerService.findById(id)

        assertEquals(fakeCustomer, customer)
        verify(exactly = 1) { repository.findById(id) }
    }

    @Test
    fun `should throw not found when find by id`() {
        val id = Random.nextInt()

        every { repository.findById(id) } returns Optional.empty()

        val error = assertThrows<NotFoundException>{
            customerService.findById(id)
        }

        assertEquals("Customer [${id}] not exists", error.message)
        assertEquals("ML-101", error.errorCode)
        verify(exactly = 1) { repository.findById(id) }
    }

    @Test
    fun `should update customer`() {
        val id = Random.nextInt()
        val fakeCustomer = buildCustomer(id)

        every { repository.existsById(id) } returns true
        every { repository.save(fakeCustomer) } returns fakeCustomer

        customerService.update(fakeCustomer)

        verify(exactly = 1) { repository.existsById(id) }
        verify(exactly = 1) { repository.save(fakeCustomer) }
    }

    @Test
    fun `should throw not found exception when update customer`() {
        val id = Random.nextInt()
        val fakeCustomer = buildCustomer(id)

        every { repository.existsById(id) } returns false
        every { repository.save(fakeCustomer) } returns fakeCustomer

        val error = assertThrows<NotFoundException>{
            customerService.update(fakeCustomer)
        }

        assertEquals("Customer [${id}] not exists", error.message)
        assertEquals("ML-101", error.errorCode)

        verify(exactly = 1) { repository.existsById(id) }
        verify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `should delete customer`() {
        val id = Random.nextInt()
        val fakeCustomer = buildCustomer(id)
        val expectedCustomer = fakeCustomer.copy(status = CustomerStatus.INATIVO)

        every { customerService.findById(id) } returns fakeCustomer
        every { repository.save(expectedCustomer) } returns expectedCustomer
        every { bookService.deleteByCustomer(fakeCustomer) } just runs

        customerService.delete(id)

        verify(exactly = 1) { customerService.findById(any()) }
        verify(exactly = 1) { bookService.deleteByCustomer(fakeCustomer) }
        verify(exactly = 1) { repository.save(expectedCustomer) }
    }

    @Test
    fun `should throw not found exception when delete customer`() {
        val id = Random.nextInt()

        every { customerService.findById(id) } throws NotFoundException(Errors.ML101.message.format(id), Errors.ML101.code)


        val error = assertThrows<NotFoundException>{
            customerService.delete(id)
        }

        assertEquals("Customer [${id}] not exists", error.message)
        assertEquals("ML-101", error.errorCode)

        verify(exactly = 1) { customerService.findById(any()) }
        verify(exactly = 0) { bookService.deleteByCustomer(any()) }
        verify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `should return true when email available`() {
        val email = "${Random.nextInt()}@email.com"

        every { repository.existsByEmail(email) } returns false

        val emailAvailable = customerService.emailAvailable(email)

        assertTrue(emailAvailable)
        verify(exactly = 1) { repository.existsByEmail(email) }
    }

    @Test
    fun `should return false when email unavailable`() {
        val email = "${Random.nextInt()}@email.com"

        every { repository.existsByEmail(email) } returns true

        val emailAvailable = customerService.emailAvailable(email)

        assertFalse(emailAvailable)
        verify(exactly = 1) { repository.existsByEmail(email) }
    }

}