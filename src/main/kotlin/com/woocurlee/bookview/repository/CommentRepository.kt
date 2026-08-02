package com.woocurlee.bookview.repository

import com.woocurlee.bookview.domain.Comment
import com.woocurlee.bookview.domain.Status
import com.woocurlee.bookview.dto.ReviewCommentCount
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository

interface CommentRepository : MongoRepository<Comment, String> {
    /** 여러 리뷰의 댓글 수를 한 번의 집계로 조회 (댓글 없는 리뷰는 결과에서 제외됨) */
    @Aggregation(
        pipeline = [
            "{ '\$match': { 'reviewId': { '\$in': ?0 }, 'status': ?1 } }",
            "{ '\$group': { '_id': '\$reviewId', 'count': { '\$sum': 1 } } }",
            "{ '\$project': { '_id': 0, 'reviewId': '\$_id', 'count': 1 } }",
        ],
    )
    fun countByReviewIds(
        reviewIds: List<String>,
        status: Status,
    ): List<ReviewCommentCount>

    fun findByReviewIdAndStatus(
        reviewId: String,
        status: Status,
    ): List<Comment>

    fun findByReviewIdAndStatusIn(
        reviewId: String,
        statuses: List<Status>,
    ): List<Comment>

    fun findByParentIdAndStatus(
        parentId: String,
        status: Status,
    ): List<Comment>

    fun findByUserIdAndStatus(
        userId: String,
        status: Status,
    ): List<Comment>

    fun countByReviewIdAndStatus(
        reviewId: String,
        status: Status,
    ): Long
}
