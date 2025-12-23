package com.woocurlee.bookview.config

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.stereotype.Component

@Component
class OAuth2AuthorizationRequestResolver(
    clientRegistrationRepository: ClientRegistrationRepository,
) : OAuth2AuthorizationRequestResolver {
    private val log = LoggerFactory.getLogger(javaClass)
    private val defaultResolver =
        DefaultOAuth2AuthorizationRequestResolver(
            clientRegistrationRepository,
            "/oauth2/authorization",
        )

    override fun resolve(request: HttpServletRequest): OAuth2AuthorizationRequest? {
        val authorizationRequest = defaultResolver.resolve(request)

        if (authorizationRequest != null) {
            log.info("=== OAuth2 Authorization Request ===")
            log.info("Authorization URI: ${authorizationRequest.authorizationUri}")
            log.info("Client ID: ${authorizationRequest.clientId}")
            log.info("Redirect URI: ${authorizationRequest.redirectUri}")
            log.info("Scope: ${authorizationRequest.scopes}")
            log.info("State: ${authorizationRequest.state}")

            // 실제 호출될 전체 URL 생성
            val fullUrl =
                buildString {
                    append(authorizationRequest.authorizationUri)
                    append("?response_type=code")
                    append("&client_id=${authorizationRequest.clientId}")
                    append("&redirect_uri=${authorizationRequest.redirectUri}")
                    append("&scope=${authorizationRequest.scopes.joinToString(",")}")
                    append("&state=${authorizationRequest.state}")
                }
            log.info("Full Authorization URL: $fullUrl")
            log.info("===================================")
        }

        return authorizationRequest
    }

    override fun resolve(
        request: HttpServletRequest,
        clientRegistrationId: String,
    ): OAuth2AuthorizationRequest? {
        val authorizationRequest = defaultResolver.resolve(request, clientRegistrationId)

        if (authorizationRequest != null) {
            log.info("=== OAuth2 Authorization Request (with clientRegistrationId) ===")
            log.info("Client Registration ID: $clientRegistrationId")
            log.info("Authorization URI: ${authorizationRequest.authorizationUri}")
            log.info("Client ID: ${authorizationRequest.clientId}")
            log.info("Redirect URI: ${authorizationRequest.redirectUri}")
            log.info("Scope: ${authorizationRequest.scopes}")

            val fullUrl =
                buildString {
                    append(authorizationRequest.authorizationUri)
                    append("?response_type=code")
                    append("&client_id=${authorizationRequest.clientId}")
                    append("&redirect_uri=${authorizationRequest.redirectUri}")
                    append("&scope=${authorizationRequest.scopes.joinToString(",")}")
                    append("&state=${authorizationRequest.state}")
                }
            log.info("Full Authorization URL: $fullUrl")
            log.info("===============================================================")
        }

        return authorizationRequest
    }
}
