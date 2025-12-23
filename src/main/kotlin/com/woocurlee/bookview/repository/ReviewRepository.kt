package com.woocurlee.bookview.repository

import com.woocurlee.bookview.domain.Review
import org.springframework.data.mongodb.repository.MongoRepository

interface ReviewRepository : MongoRepository<Review, String> {
    fun findByBookId(bookId: String): List<Review>
}
