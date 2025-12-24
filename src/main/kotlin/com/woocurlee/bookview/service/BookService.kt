package com.woocurlee.bookview.service

import com.woocurlee.bookview.domain.Book
import com.woocurlee.bookview.repository.BookRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BookService(
    private val bookRepository: BookRepository,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun searchBooks(query: String): List<Book> {
        log.info("=== searchBooks 호출됨 ===")
        log.info("검색어: $query")
        val titleResults = bookRepository.findByTitleContaining(query)
        val authorResults = bookRepository.findByAuthorContaining(query)
        log.info("제목 검색 결과: ${titleResults.size}건")
        log.info("저자 검색 결과: ${authorResults.size}건")
        val result = (titleResults + authorResults).distinct()
        log.info("최종 결과: ${result.size}건")
        return result
    }

    fun getBookById(id: String): Book? = bookRepository.findById(id).orElse(null)

    fun createBook(book: Book): Book = bookRepository.save(book)

    fun getAllBooks(): List<Book> {
        log.info("=== getAllBooks 호출됨 ===")
        val books = bookRepository.findAll()
        log.info("전체 책 수: ${books.size}건")
        return books
    }
}
