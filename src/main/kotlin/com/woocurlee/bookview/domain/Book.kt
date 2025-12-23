package com.woocurlee.bookview.domain

import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "books")
data class Book(
    @Id
    val id: String? = null,
    val title: String,
    val author: String,
    val isbn: String,
    val publishedYear: Int,
    val description: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
