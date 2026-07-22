package com.woocurlee.bookview.repository

import com.woocurlee.bookview.domain.BookshelfEntry
import com.woocurlee.bookview.domain.Status
import org.springframework.data.mongodb.repository.MongoRepository

interface BookshelfRepository : MongoRepository<BookshelfEntry, String> {
    fun findByUserIdAndStatusOrderByCreatedAtDesc(
        userId: String,
        status: Status,
    ): List<BookshelfEntry>
}
