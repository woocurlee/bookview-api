package com.woocurlee.bookview.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token = extractTokenFromCookie(request)

        if (token != null && jwtUtil.validateToken(token)) {
            val googleId = jwtUtil.getGoogleIdFromToken(token)
            val email = jwtUtil.getEmailFromToken(token)
            val nickname = jwtUtil.getNicknameFromToken(token)

            // 간단한 Authentication 객체 생성
            val authentication =
                UsernamePasswordAuthenticationToken(
                    mapOf(
                        "sub" to googleId,
                        "email" to email,
                        "nickname" to nickname,
                    ),
                    null,
                    emptyList(),
                )
            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }

    private fun extractTokenFromCookie(request: HttpServletRequest): String? =
        request.cookies
            ?.find { it.name == "jwt" }
            ?.value
}
