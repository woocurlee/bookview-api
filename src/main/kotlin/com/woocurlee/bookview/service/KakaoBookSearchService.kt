package com.woocurlee.bookview.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class KakaoBookSearchService(
    private val kakaoWebClient: WebClient,
) {
    @Value("\${kakao.api.key:}")
    private lateinit var kakaoApiKey: String

    fun searchBooks(
        query: String,
        page: Int = 1,
        size: Int = 10,
    ): KakaoBookSearchResponse? =
        kakaoWebClient
            .get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/v3/search/book")
                    .queryParam("query", query)
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .build()
            }.header("Authorization", "KakaoAK $kakaoApiKey")
            .retrieve()
            .bodyToMono<KakaoBookSearchResponse>()
            .block()
}

data class KakaoBookSearchResponse(
    val meta: Meta,
    val documents: List<BookDocument>,
)

data class Meta(
    val totalCount: Int,
    val pageableCount: Int,
    val isEnd: Boolean,
)

data class BookDocument(
    val title: String,
    val contents: String,
    val url: String,
    val isbn: String,
    val datetime: String,
    val authors: List<String>,
    val publisher: String,
    val translators: List<String>,
    val price: Int,
    val salePrice: Int,
    val thumbnail: String,
    val status: String,
)
