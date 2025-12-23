package com.woocurlee.bookview.service

import com.woocurlee.bookview.domain.Book
import com.woocurlee.bookview.repository.BookRepository
import org.springframework.stereotype.Service

@Service
class BookService(
    private val bookRepository: BookRepository,
) {
    fun searchBooks(query: String): List<Book> =
        bookRepository.findByTitleContaining(query) +
            bookRepository.findByAuthorContaining(query)

    fun getBookById(id: String): Book? = bookRepository.findById(id).orElse(null)

    fun createBook(book: Book): Book = bookRepository.save(book)

    fun getAllBooks(): List<Book> = bookRepository.findAll()
}
