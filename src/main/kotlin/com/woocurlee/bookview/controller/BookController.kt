package com.woocurlee.bookview.controller

import com.woocurlee.bookview.domain.Book
import com.woocurlee.bookview.service.BookService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/books")
class BookController(
    private val bookService: BookService,
) {
    @GetMapping
    fun getAllBooks(): ResponseEntity<List<Book>> = ResponseEntity.ok(bookService.getAllBooks())

    @GetMapping("/{id}")
    fun getBook(
        @PathVariable id: String,
    ): ResponseEntity<Book> {
        val book = bookService.getBookById(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(book)
    }

    @GetMapping("/search")
    fun searchBooks(
        @RequestParam query: String,
    ): ResponseEntity<List<Book>> = ResponseEntity.ok(bookService.searchBooks(query))

    @PostMapping
    fun createBook(
        @RequestBody book: Book,
    ): ResponseEntity<Book> = ResponseEntity.ok(bookService.createBook(book))
}
