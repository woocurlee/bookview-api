package com.woocurlee.bookview.service

import com.woocurlee.bookview.domain.BookshelfEntry
import com.woocurlee.bookview.domain.Status
import com.woocurlee.bookview.dto.AddBookshelfEntryRequest
import com.woocurlee.bookview.dto.UpdateBookshelfEntryRequest
import com.woocurlee.bookview.repository.BookshelfRepository
import com.woocurlee.bookview.util.HtmlSanitizer
import java.time.LocalDate
import java.time.LocalDateTime
import org.springframework.stereotype.Service

/**
 * 유저 책장 데이터.
 * @property reading 완독일이 없는 "읽는 중" 책 (시작일 최신순)
 * @property finished 완독한 책 (완독일 최신순)
 */
data class BookshelfData(
    val reading: List<BookshelfEntry>,
    val finished: List<BookshelfEntry>,
)

@Service
class BookshelfService(
    private val bookshelfRepository: BookshelfRepository,
) {
    /** 유저의 책장을 읽는 중/완독으로 분리해 반환한다. */
    fun getBookshelf(userId: String): BookshelfData {
        val entries = bookshelfRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, Status.ACTIVE)
        val (finished, reading) = entries.partition { it.finishedAt != null }
        return BookshelfData(
            reading = reading.sortedByDescending { it.startedAt ?: LocalDate.MIN },
            finished = finished.sortedByDescending { it.finishedAt },
        )
    }

    /** 책장에 책을 추가한다. */
    fun addEntry(
        userId: String,
        request: AddBookshelfEntryRequest,
    ): BookshelfEntry {
        validateDates(request.startedAt, request.finishedAt)
        val entry =
            BookshelfEntry(
                userId = userId,
                bookTitle = HtmlSanitizer.toPlainText(request.bookTitle),
                bookAuthor = HtmlSanitizer.toPlainText(request.bookAuthor),
                bookThumbnail = request.bookThumbnail,
                bookIsbn = request.bookIsbn,
                startedAt = request.startedAt,
                finishedAt = request.finishedAt,
            )
        return bookshelfRepository.save(entry)
    }

    /**
     * 책장 항목의 날짜를 수정한다.
     * @return 항목이 없거나 소유자가 아니면 null
     */
    fun updateEntry(
        id: String,
        userId: String,
        request: UpdateBookshelfEntryRequest,
    ): BookshelfEntry? {
        val entry = findOwnedActiveEntry(id, userId) ?: return null
        validateDates(request.startedAt, request.finishedAt)
        val updated =
            entry.copy(
                startedAt = request.startedAt,
                finishedAt = request.finishedAt,
                updatedAt = LocalDateTime.now(),
            )
        return bookshelfRepository.save(updated)
    }

    /**
     * 책장 항목을 소프트 삭제한다.
     * @return 항목이 없거나 소유자가 아니면 false
     */
    fun deleteEntry(
        id: String,
        userId: String,
    ): Boolean {
        val entry = findOwnedActiveEntry(id, userId) ?: return false
        bookshelfRepository.save(entry.copy(status = Status.DELETED, updatedAt = LocalDateTime.now()))
        return true
    }

    private fun findOwnedActiveEntry(
        id: String,
        userId: String,
    ): BookshelfEntry? {
        val entry = bookshelfRepository.findById(id).orElse(null) ?: return null
        if (entry.status != Status.ACTIVE || entry.userId != userId) return null
        return entry
    }

    /** 완독일은 시작일보다 앞설 수 없다. */
    private fun validateDates(
        startedAt: LocalDate?,
        finishedAt: LocalDate?,
    ) {
        if (startedAt != null && finishedAt != null && finishedAt.isBefore(startedAt)) {
            throw IllegalArgumentException("완독일은 시작일보다 앞설 수 없습니다.")
        }
    }
}
