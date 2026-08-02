package com.woocurlee.bookview.controller

import com.woocurlee.bookview.dto.AddBookshelfEntryRequest
import com.woocurlee.bookview.dto.UpdateBookshelfEntryRequest
import com.woocurlee.bookview.service.BookshelfService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bookshelf")
class BookshelfController(
    private val bookshelfService: BookshelfService,
) {
    @PostMapping
    fun addEntry(
        @RequestBody request: AddBookshelfEntryRequest,
        @AuthenticationPrincipal principal: Any,
    ): ResponseEntity<Any> {
        val googleId = googleIdOf(principal)
        return try {
            ResponseEntity.ok(bookshelfService.addEntry(googleId, request))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("message" to (e.message ?: "잘못된 요청입니다.")))
        }
    }

    @PutMapping("/{id}")
    fun updateEntry(
        @PathVariable id: String,
        @RequestBody request: UpdateBookshelfEntryRequest,
        @AuthenticationPrincipal principal: Any,
    ): ResponseEntity<Any> {
        val googleId = googleIdOf(principal)
        return try {
            val updated =
                bookshelfService.updateEntry(id, googleId, request) ?: return ResponseEntity.notFound().build()
            ResponseEntity.ok(updated)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("message" to (e.message ?: "잘못된 요청입니다.")))
        }
    }

    @DeleteMapping("/{id}")
    fun deleteEntry(
        @PathVariable id: String,
        @AuthenticationPrincipal principal: Any,
    ): ResponseEntity<Void> {
        val googleId = googleIdOf(principal)
        return if (bookshelfService.deleteEntry(id, googleId)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    private fun googleIdOf(principal: Any): String {
        val attributes = principal as Map<*, *>
        return attributes["sub"].toString()
    }
}
