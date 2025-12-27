package com.woocurlee.bookview.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class KakaoBookSearchService(
    private val kakaoWebClient: WebClient,
) {
    @Value("\${kakao.api.key}")
    private lateinit var kakaoApiKey: String

    fun searchBooks(
        query: String,
        page: Int = 1,
        size: Int = 10,
    ): KakaoBookSearchResponse? {
        val log = org.slf4j.LoggerFactory.getLogger(javaClass)
        log.info("=== 카카오 책 검색 API 호출 ===")
        log.info("API Key: ${if (kakaoApiKey.isNotEmpty()) "${kakaoApiKey.take(10)}..." else "설정 안됨"}")
        log.info("검색어: $query")
        log.info("Authorization 헤더: KakaoAK ${kakaoApiKey.take(10)}...")

        return try {
            val result =
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
                    .onStatus({ it.is4xxClientError }) { response ->
                        response.bodyToMono<String>().map { body ->
                            log.error("4xx 에러 발생: ${response.statusCode()}")
                            log.error("응답 본문: $body")
                            RuntimeException("카카오 API 에러: ${response.statusCode()}")
                        }
                    }.bodyToMono<KakaoBookSearchResponse>()
                    .block()

            log.info("검색 결과: ${result?.documents?.size}건")
            result
        } catch (e: Exception) {
            log.error("카카오 API 호출 실패", e)
            null
        }
    }
}

data class KakaoBookSearchResponse(
    val meta: Meta,
    val documents: List<BookDocument>,
)

data class Meta(
    val totalCount: Int? = null,
    val pageableCount: Int? = null,
    val isEnd: Boolean? = null,
)

data class BookDocument(
    val title: String = "",
    val contents: String = "",
    val url: String = "",
    val isbn: String = "",
    val datetime: String = "",
    val authors: List<String> = emptyList(),
    val publisher: String = "",
    val translators: List<String> = emptyList(),
    val price: Int? = null,
    val salePrice: Int? = null,
    val thumbnail: String = "",
    val status: String = "",
)
