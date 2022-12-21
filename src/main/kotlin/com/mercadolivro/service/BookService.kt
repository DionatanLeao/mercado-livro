package com.mercadolivro.service

import com.mercadolivro.enums.BookStatus
import com.mercadolivro.model.BookModel
import com.mercadolivro.model.CustomerModel
import com.mercadolivro.repository.BookRepository
import org.springframework.stereotype.Service

@Service
class BookService(
    val repository: BookRepository
) {

    fun create(book: BookModel) {
        repository.save(book)
    }

    fun findAll(): List<BookModel> {
       return repository.findAll().toList()
    }

    fun findActives(): List<BookModel> {
        return repository.findByStatus(BookStatus.ATIVO)
    }

    fun findById(id: Int): BookModel {
        return repository.findById(id).orElseThrow()
    }

    fun delete(id: Int) {
        val book = findById(id)
        book.status = BookStatus.CANCELADO
        update(book)
    }

    fun update(book: BookModel) {
        repository.save(book)
    }

    fun deleteByCustomer(customer: CustomerModel) {
        val books = repository.findByCustomer(customer)
        books.stream().forEach { book ->
            book.status = BookStatus.DELETADO
        }
        repository.saveAll(books)
    }
}