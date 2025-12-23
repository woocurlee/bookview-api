package com.woocurlee.bookview.domain

import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "reviews")
data class Review(
    @Id
    val id: String? = null,
    val bookId: String,
    val userId: String,
    val rating: Int, // 1-5
    val content: String,
    val reviewer: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)
