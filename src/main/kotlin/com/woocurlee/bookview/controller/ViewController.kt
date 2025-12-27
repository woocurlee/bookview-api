package com.woocurlee.bookview.controller

import com.woocurlee.bookview.repository.UserRepository
import com.woocurlee.bookview.service.BookService
import com.woocurlee.bookview.service.ReviewService
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ViewController(
    private val bookService: BookService,
    private val userRepository: UserRepository,
    private val reviewService: ReviewService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/")
    fun index(
        model: Model,
        @AuthenticationPrincipal oauth2User: OAuth2User?,
    ): String {
        val books = bookService.getAllBooks()
        model.addAttribute("books", books)

        // 최근 리뷰 10개 추가
        val reviews = reviewService.getAllReviews().sortedByDescending { it.createdAt }.take(10)
        model.addAttribute("reviews", reviews)

        if (oauth2User != null) {
            val googleId = oauth2User.attributes["sub"].toString()
            val user = userRepository.findByGoogleId(googleId)
            model.addAttribute("user", user)
        }

        return "index"
    }

    @GetMapping("/books")
    fun bookList(
        model: Model,
        @RequestParam(required = false) query: String?,
    ): String {
        log.info("=== /books 엔드포인트 호출 ===")
        log.info("query 파라미터: '$query'")

        val books =
            if (query.isNullOrBlank()) {
                log.info("query가 비어있음 -> getAllBooks() 호출")
                bookService.getAllBooks()
            } else {
                log.info("query가 있음 -> searchBooks('$query') 호출")
                bookService.searchBooks(query)
            }

        model.addAttribute("books", books)
        model.addAttribute("query", query ?: "")
        return "books"
    }

    @GetMapping("/write-review")
    fun writeReview(): String = "write-review"
}
