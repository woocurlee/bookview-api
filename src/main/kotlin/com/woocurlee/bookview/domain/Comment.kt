package com.woocurlee.bookview.domain

import com.woocurlee.bookview.common.MongoCollections
import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = MongoCollections.COMMENTS)
@CompoundIndex(name = "comment_review_status_idx", def = "{'reviewId': 1, 'status': 1}")
@CompoundIndex(name = "comment_parent_status_idx", def = "{'parentId': 1, 'status': 1}")
data class Comment(
    @Id val id: String? = null,
    val commentNo: Long? = null,
    val reviewId: String,
    val userId: String,
    val content: String,
    val parentId: String? = null,
    val status: Status = Status.ACTIVE,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)
