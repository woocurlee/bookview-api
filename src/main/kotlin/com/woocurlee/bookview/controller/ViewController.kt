package com.woocurlee.bookview.controller

import com.woocurlee.bookview.repository.UserRepository
import com.woocurlee.bookview.service.ReviewService
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ViewController(
    private val userRepository: UserRepository,
    private val reviewService: ReviewService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/")
    fun index(
        model: Model,
        @AuthenticationPrincipal principal: Any?,
    ): String {
        // 최근 리뷰 10개 추가 (페이징)
        val pageable =
            org.springframework.data.domain.PageRequest.of(
                0,
                10,
                org.springframework.data.domain.Sort
                    .by("createdAt")
                    .descending(),
            )
        val reviewsPage = reviewService.getReviews(pageable)
        model.addAttribute("reviews", reviewsPage.content)
        model.addAttribute("hasMoreReviews", reviewsPage.hasNext())

        if (principal != null) {
            val attributes = principal as? Map<*, *>
            val googleId = attributes?.get("sub")?.toString()
            if (googleId != null) {
                val user = userRepository.findByGoogleId(googleId)
                model.addAttribute("user", user)
            }
        }

        return "index"
    }

    @GetMapping("/write-review")
    fun writeReview(): String = "write-review"
}
