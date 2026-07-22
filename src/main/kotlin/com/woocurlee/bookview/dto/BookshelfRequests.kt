package com.woocurlee.bookview.dto

import com.woocurlee.bookview.domain.BookshelfEntry
import java.time.LocalDate

/** 책장에 책 추가 요청 (카카오 검색 결과 + 날짜) */
data class AddBookshelfEntryRequest(
    val bookTitle: String,
    val bookAuthor: String,
    val bookThumbnail: String?,
    val bookIsbn: String,
    val startedAt: LocalDate?,
    val finishedAt: LocalDate?,
)

/** 책장 항목의 날짜 수정 요청 */
data class UpdateBookshelfEntryRequest(
    val startedAt: LocalDate?,
    val finishedAt: LocalDate?,
)

/** 페이지 렌더링용 책장 항목 (userId·상태 등 내부 필드 제외) */
data class BookshelfEntryView(
    val id: String?,
    val bookTitle: String,
    val bookAuthor: String,
    val bookThumbnail: String?,
    val bookIsbn: String,
    val startedAt: LocalDate?,
    val finishedAt: LocalDate?,
)

fun BookshelfEntry.toView() =
    BookshelfEntryView(
        id = id,
        bookTitle = bookTitle,
        bookAuthor = bookAuthor,
        bookThumbnail = bookThumbnail,
        bookIsbn = bookIsbn,
        startedAt = startedAt,
        finishedAt = finishedAt,
    )
