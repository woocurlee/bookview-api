package com.woocurlee.bookview.service

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

/**
 * 로컬 개발용 목 책 검색.
 * `kakao.mock.enabled: true`일 때 KakaoBookSearchService 대신 주입되어
 * 카카오 API 없이도 책 검색이 동작하게 한다.
 */
@Service
@ConditionalOnProperty(name = ["kakao.mock.enabled"], havingValue = "true")
class MockBookSearchService : BookSearchService {
    override fun searchBooks(
        query: String,
        page: Int,
        size: Int,
    ): KakaoBookSearchResponse {
        val keyword = query.trim()
        val filtered =
            if (keyword.isEmpty()) {
                MOCK_BOOKS
            } else {
                MOCK_BOOKS.filter { book ->
                    book.title.contains(keyword, ignoreCase = true) ||
                        book.authors.any { it.contains(keyword, ignoreCase = true) } ||
                        book.publisher.contains(keyword, ignoreCase = true)
                }
            }

        val from = ((page - 1).coerceAtLeast(0)) * size
        val pageItems = filtered.drop(from).take(size)
        val isEnd = from + size >= filtered.size

        return KakaoBookSearchResponse(
            meta = Meta(totalCount = filtered.size, pageableCount = filtered.size, isEnd = isEnd),
            documents = pageItems,
        )
    }

    companion object {
        private fun cover(id: Long): String =
            "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=" +
                "http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F$id"

        private val MOCK_BOOKS =
            listOf(
                BookDocument(
                    title = "데미안",
                    contents = "새는 알에서 나오려고 투쟁한다. 알은 세계다.",
                    url = "https://search.daum.net/search?w=bookpage&bookId=1467038",
                    isbn = "8937460440 9788937460449",
                    datetime = "2000-12-01T00:00:00.000+09:00",
                    authors = listOf("헤르만 헤세"),
                    publisher = "민음사",
                    translators = listOf("전영애"),
                    price = 10000,
                    salePrice = 9000,
                    thumbnail = cover(1467038),
                    status = "정상판매",
                ),
                BookDocument(
                    title = "1984",
                    contents = "빅 브라더가 당신을 지켜보고 있다.",
                    url = "https://search.daum.net/search?w=bookpage&bookId=1006446",
                    isbn = "8937460777 9788937460777",
                    datetime = "2003-06-25T00:00:00.000+09:00",
                    authors = listOf("조지 오웰"),
                    publisher = "민음사",
                    translators = listOf("정회성"),
                    price = 12000,
                    salePrice = 10800,
                    thumbnail = cover(1006446),
                    status = "정상판매",
                ),
                BookDocument(
                    title = "토지 1",
                    contents = "1897년 한가위, 평사리의 만석꾼 최참판댁 이야기.",
                    url = "https://search.daum.net/search?w=bookpage&bookId=1815908",
                    isbn = "8960535028 9788960535015",
                    datetime = "2012-08-15T00:00:00.000+09:00",
                    authors = listOf("박경리"),
                    publisher = "마로니에북스",
                    translators = emptyList(),
                    price = 14000,
                    salePrice = 12600,
                    thumbnail = cover(1815908),
                    status = "정상판매",
                ),
                BookDocument(
                    title = "어린 왕자",
                    contents = "가장 중요한 것은 눈에 보이지 않아.",
                    url = "https://search.daum.net/search?w=bookpage&bookId=1923641",
                    isbn = "895460126X 9788954601269",
                    datetime = "2015-10-05T00:00:00.000+09:00",
                    authors = listOf("앙투안 드 생텍쥐페리"),
                    publisher = "열린책들",
                    translators = listOf("황현산"),
                    price = 9800,
                    salePrice = 8820,
                    thumbnail = cover(1923641),
                    status = "정상판매",
                ),
                BookDocument(
                    title = "코스모스",
                    contents = "우리는 별의 먼지로 만들어졌다.",
                    url = "https://search.daum.net/search?w=bookpage&bookId=1467512",
                    isbn = "8983711892 9788983711892",
                    datetime = "2006-12-20T00:00:00.000+09:00",
                    authors = listOf("칼 세이건"),
                    publisher = "사이언스북스",
                    translators = listOf("홍승수"),
                    price = 22000,
                    salePrice = 19800,
                    thumbnail = cover(1467512),
                    status = "정상판매",
                ),
                BookDocument(
                    title = "사피엔스",
                    contents = "인지 혁명에서 과학 혁명까지, 인류의 역사.",
                    url = "https://search.daum.net/search?w=bookpage&bookId=2135797",
                    isbn = "8934972467 9788934972464",
                    datetime = "2015-11-24T00:00:00.000+09:00",
                    authors = listOf("유발 하라리"),
                    publisher = "김영사",
                    translators = listOf("조현욱"),
                    price = 22000,
                    salePrice = 19800,
                    thumbnail = cover(2135797),
                    status = "정상판매",
                ),
                BookDocument(
                    title = "노르웨이의 숲",
                    contents = "죽음은 삶의 대극이 아니라 그 일부로 존재한다.",
                    url = "https://search.daum.net/search?w=bookpage&bookId=1901234",
                    isbn = "8937473135 9788937473135",
                    datetime = "2017-09-01T00:00:00.000+09:00",
                    authors = listOf("무라카미 하루키"),
                    publisher = "민음사",
                    translators = listOf("양억관"),
                    price = 14000,
                    salePrice = 12600,
                    thumbnail = cover(1901234),
                    status = "정상판매",
                ),
                BookDocument(
                    title = "총, 균, 쇠",
                    contents = "왜 어떤 문명은 다른 문명을 정복했는가.",
                    url = "https://search.daum.net/search?w=bookpage&bookId=1467999",
                    isbn = "8970125248 9788970125244",
                    datetime = "2005-12-19T00:00:00.000+09:00",
                    authors = listOf("재레드 다이아몬드"),
                    publisher = "문학사상",
                    translators = listOf("김진준"),
                    price = 25000,
                    salePrice = 22500,
                    thumbnail = cover(1467999),
                    status = "정상판매",
                ),
                BookDocument(
                    title = "죄와 벌 1",
                    contents = "한 청년이 저지른 살인과 그 후의 심리.",
                    url = "https://search.daum.net/search?w=bookpage&bookId=1467621",
                    isbn = "8937462630 9788937462634",
                    datetime = "2012-03-30T00:00:00.000+09:00",
                    authors = listOf("표도르 도스토옙스키"),
                    publisher = "민음사",
                    translators = listOf("김연경"),
                    price = 13000,
                    salePrice = 11700,
                    thumbnail = cover(1467621),
                    status = "정상판매",
                ),
                BookDocument(
                    title = "이기적 유전자",
                    contents = "우리는 유전자의 생존 기계다.",
                    url = "https://search.daum.net/search?w=bookpage&bookId=1789456",
                    isbn = "8932917248 9788932917245",
                    datetime = "2018-10-15T00:00:00.000+09:00",
                    authors = listOf("리처드 도킨스"),
                    publisher = "을유문화사",
                    translators = listOf("홍영남", "이상임"),
                    price = 20000,
                    salePrice = 18000,
                    thumbnail = cover(1789456),
                    status = "정상판매",
                ),
            )
    }
}
