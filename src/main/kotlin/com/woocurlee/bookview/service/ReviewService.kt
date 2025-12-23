package com.woocurlee.bookview.service

import com.woocurlee.bookview.domain.Review
import com.woocurlee.bookview.repository.ReviewRepository
import java.time.LocalDateTime
import org.springframework.stereotype.Service

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
) {
    fun getReviewsByBookId(bookId: String): List<Review> = reviewRepository.findByBookId(bookId)

    fun createReview(review: Review): Review = reviewRepository.save(review)

    fun updateReview(
        id: String,
        content: String,
        rating: Int,
    ): Review? {
        val review = reviewRepository.findById(id).orElse(null) ?: return null
        val updated =
            review.copy(
                content = content,
                rating = rating,
                updatedAt = LocalDateTime.now(),
            )
        return reviewRepository.save(updated)
    }

    fun deleteReview(id: String) {
        reviewRepository.deleteById(id)
    }
}
