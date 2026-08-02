package com.woocurlee.bookview.service

/** 책 검색 추상화. 운영은 카카오 API, 로컬은 목 데이터로 교체된다. */
interface BookSearchService {
    fun searchBooks(
        query: String,
        page: Int = 1,
        size: Int = 10,
    ): KakaoBookSearchResponse?
}
