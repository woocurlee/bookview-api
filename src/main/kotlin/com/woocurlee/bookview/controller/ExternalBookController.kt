package com.woocurlee.bookview.controller

import com.woocurlee.bookview.service.KakaoBookSearchResponse
import com.woocurlee.bookview.service.KakaoBookSearchService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/external/books")
class ExternalBookController(
    private val kakaoBookSearchService: KakaoBookSearchService,
) {
    @GetMapping("/search")
    fun searchExternalBooks(
        @RequestParam query: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<KakaoBookSearchResponse> {
        val result =
            kakaoBookSearchService.searchBooks(query, page, size)
                ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(result)
    }
}
