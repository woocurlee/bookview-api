package com.woocurlee.bookview.dto

/** 리뷰별 댓글 수 집계 결과 (aggregation projection) */
data class ReviewCommentCount(
    val reviewId: String,
    val count: Long,
)
