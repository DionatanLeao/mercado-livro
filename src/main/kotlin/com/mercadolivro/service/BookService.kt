package com.mercadolivro.service

import com.mercadolivro.model.BookModel
import com.mercadolivro.repository.BookRepository
import org.springframework.stereotype.Service

@Service
class BookService(
    val repository: BookRepository
) {

    fun create(book: BookModel) {
        repository.save(book)
    }
}