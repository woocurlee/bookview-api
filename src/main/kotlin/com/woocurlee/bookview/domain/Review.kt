package com.woocurlee.bookview.domain

import com.woocurlee.bookview.common.MongoCollections
import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = MongoCollections.REVIEWS)
@CompoundIndex(name = "review_status_created_idx", def = "{'status': 1, 'createdAt': -1}")
@CompoundIndex(name = "review_user_status_created_idx", def = "{'userId': 1, 'status': 1, 'createdAt': -1}")
data class Review(
    @Id
    val id: String? = null,
    @Indexed(name = "review_reviewNo_idx")
    val reviewNo: Long? = null, // URL에 노출할 순차 번호
    val userId: String,
    val title: String,
    val bookTitle: String,
    val bookAuthor: String,
    val bookIsbn: String,
    val bookThumbnail: String?,
    val rating: Int, // 1-5
    val quote: String, // 명언 (5~250자)
    val content: String,
    val likeCount: Long = 0,
    val status: Status = Status.ACTIVE, // 리뷰 상태
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)
