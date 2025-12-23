package com.woocurlee.bookview.service

import com.woocurlee.bookview.domain.User
import com.woocurlee.bookview.repository.UserRepository
import java.time.LocalDateTime
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository,
) : DefaultOAuth2UserService() {
    private val log = org.slf4j.LoggerFactory.getLogger(javaClass)

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)

        log.info("=== 구글 로그인 사용자 정보 ===")
        log.info("Google ID: ${oAuth2User.attributes["sub"]}")
        log.info("Name: ${oAuth2User.attributes["name"]}")
        log.info("Email: ${oAuth2User.attributes["email"]}")
        log.info("Picture: ${oAuth2User.attributes["picture"]}")

        val googleId = oAuth2User.attributes["sub"].toString()
        val nickname = oAuth2User.attributes["name"] as? String ?: "Unknown"
        val email = oAuth2User.attributes["email"] as? String
        val profileImageUrl = oAuth2User.attributes["picture"] as? String

        var user = userRepository.findByGoogleId(googleId)

        if (user == null) {
            log.info("신규 사용자 생성: $googleId")
            user =
                User(
                    googleId = googleId,
                    nickname = nickname,
                    email = email,
                    profileImageUrl = profileImageUrl,
                )
        } else {
            log.info("기존 사용자 업데이트: $googleId")
            user =
                user.copy(
                    nickname = nickname,
                    email = email,
                    profileImageUrl = profileImageUrl,
                    lastLoginAt = LocalDateTime.now(),
                )
        }

        val savedUser = userRepository.save(user)
        log.info("사용자 저장 완료 - DB ID: ${savedUser.id}")
        log.info("MongoDB Database: bookview, Collection: users")
        log.info("==============================")

        return oAuth2User
    }
}
