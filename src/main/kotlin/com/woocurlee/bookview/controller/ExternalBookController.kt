package com.woocurlee.bookview.controller

import com.woocurlee.bookview.service.BookSearchService
import com.woocurlee.bookview.service.KakaoBookSearchResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/external/books")
class ExternalBookController(
    private val bookSearchService: BookSearchService,
) {
    @GetMapping("/search")
    fun searchExternalBooks(
        @RequestParam query: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<KakaoBookSearchResponse> {
        val result =
            bookSearchService.searchBooks(query, page, size)
                ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(result)
    }
}
