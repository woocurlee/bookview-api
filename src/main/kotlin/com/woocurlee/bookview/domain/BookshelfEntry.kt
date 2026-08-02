package com.woocurlee.bookview.domain

import com.woocurlee.bookview.common.MongoCollections
import java.time.LocalDate
import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = MongoCollections.BOOKSHELF)
@CompoundIndex(name = "bookshelf_user_status_created_idx", def = "{'userId': 1, 'status': 1, 'createdAt': -1}")
@CompoundIndex(name = "bookshelf_user_isbn_idx", def = "{'userId': 1, 'bookIsbn': 1}")
data class BookshelfEntry(
    @Id
    val id: String? = null,
    val userId: String, // googleId
    val bookTitle: String,
    val bookAuthor: String,
    val bookThumbnail: String?, // 카카오 썸네일 (마이그레이션 대비 bookIsbn을 키로 사용)
    val bookIsbn: String,
    val startedAt: LocalDate?, // 읽기 시작한 날 (선택)
    val finishedAt: LocalDate?, // 완독일 — null이면 "읽는 중"
    val status: Status = Status.ACTIVE, // 소프트 삭제
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)
