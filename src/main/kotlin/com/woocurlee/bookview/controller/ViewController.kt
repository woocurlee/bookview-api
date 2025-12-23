package com.woocurlee.bookview.controller

import com.woocurlee.bookview.repository.UserRepository
import com.woocurlee.bookview.service.BookService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ViewController(
    private val bookService: BookService,
    private val userRepository: UserRepository,
) {
    @GetMapping("/")
    fun index(
        model: Model,
        @AuthenticationPrincipal oauth2User: OAuth2User?,
    ): String {
        val books = bookService.getAllBooks()
        model.addAttribute("books", books)

        if (oauth2User != null) {
            val googleId = oauth2User.attributes["sub"].toString()
            val user = userRepository.findByGoogleId(googleId)
            model.addAttribute("user", user)
        }

        return "index"
    }

    @GetMapping("/books")
    fun bookList(model: Model): String {
        val books = bookService.getAllBooks()
        model.addAttribute("books", books)
        return "books"
    }
}
