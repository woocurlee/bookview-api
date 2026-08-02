package com.woocurlee.bookview.repository

import com.woocurlee.bookview.domain.BookshelfEntry
import com.woocurlee.bookview.domain.Status
import java.time.LocalDate
import org.springframework.data.mongodb.repository.MongoRepository

interface BookshelfRepository : MongoRepository<BookshelfEntry, String> {
    fun findByUserIdAndStatusOrderByCreatedAtDesc(
        userId: String,
        status: Status,
    ): List<BookshelfEntry>

    /** 같은 유저가 같은 책(isbn)을 같은 완독일로 등록했는지 확인용 */
    fun findByUserIdAndBookIsbnAndFinishedAtAndStatus(
        userId: String,
        bookIsbn: String,
        finishedAt: LocalDate,
        status: Status,
    ): List<BookshelfEntry>

    /** 같은 유저가 같은 책(isbn)을 '읽는 중'(완독일 없음)으로 등록했는지 확인용 */
    fun findByUserIdAndBookIsbnAndFinishedAtIsNullAndStatus(
        userId: String,
        bookIsbn: String,
        status: Status,
    ): List<BookshelfEntry>
}
