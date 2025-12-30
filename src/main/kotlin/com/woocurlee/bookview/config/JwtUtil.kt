package com.woocurlee.bookview.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.util.Date
import javax.crypto.SecretKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JwtUtil {
    @Value("\${jwt.secret}")
    private lateinit var secret: String

    @Value("\${jwt.expiration}")
    private var expiration: Long = 86400000 // 24시간 (밀리초)

    private fun getSigningKey(): SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateToken(
        googleId: String,
        email: String,
        nickname: String,
    ): String =
        Jwts
            .builder()
            .subject(googleId)
            .claim("email", email)
            .claim("nickname", nickname)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey())
            .compact()

    fun validateToken(token: String): Boolean =
        try {
            Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }

    fun getGoogleIdFromToken(token: String): String = getClaims(token).subject

    fun getEmailFromToken(token: String): String = getClaims(token)["email"] as String

    fun getNicknameFromToken(token: String): String = getClaims(token)["nickname"] as String

    private fun getClaims(token: String): Claims =
        Jwts
            .parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload
}
