package com.woocurlee.bookview.controller

import com.woocurlee.bookview.domain.Review
import com.woocurlee.bookview.service.ReviewService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/reviews")
class ReviewController(
    private val reviewService: ReviewService,
) {
    @GetMapping("/book/{bookId}")
    fun getReviewsByBookId(
        @PathVariable bookId: String,
    ): ResponseEntity<List<Review>> = ResponseEntity.ok(reviewService.getReviewsByBookId(bookId))

    @PostMapping
    fun createReview(
        @RequestBody review: Review,
    ): ResponseEntity<Review> = ResponseEntity.ok(reviewService.createReview(review))

    @PutMapping("/{id}")
    fun updateReview(
        @PathVariable id: String,
        @RequestBody request: UpdateReviewRequest,
    ): ResponseEntity<Review> {
        val updated =
            reviewService.updateReview(id, request.content, request.rating)
                ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun deleteReview(
        @PathVariable id: String,
    ): ResponseEntity<Void> {
        reviewService.deleteReview(id)
        return ResponseEntity.noContent().build()
    }
}

data class UpdateReviewRequest(
    val content: String,
    val rating: Int,
)
