package com.woocurlee.bookview.repository

import com.woocurlee.bookview.domain.Book
import org.springframework.data.mongodb.repository.MongoRepository

interface BookRepository : MongoRepository<Book, String> {
    fun findByTitleContaining(title: String): List<Book>

    fun findByAuthorContaining(author: String): List<Book>

    fun findByIsbn(isbn: String): Book?
}
