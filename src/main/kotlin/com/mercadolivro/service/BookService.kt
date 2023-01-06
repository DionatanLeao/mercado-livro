package com.mercadolivro.service

import com.mercadolivro.enums.BookStatus
import com.mercadolivro.exception.NotFoundException
import com.mercadolivro.model.BookModel
import com.mercadolivro.model.CustomerModel
import com.mercadolivro.repository.BookRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class BookService(
    val repository: BookRepository
) {

    fun create(book: BookModel) {
        repository.save(book)
    }

    fun findAll(pageable: Pageable): Page<BookModel> {
       return repository.findAll(pageable)
    }

    fun findActives(pageable: Pageable): Page<BookModel> {
        return repository.findByStatus(BookStatus.ATIVO, pageable)
    }

    fun findById(id: Int): BookModel {
        return repository.findById(id).orElseThrow{ NotFoundException("Book [${id}] not exists", "ML-0002") }
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