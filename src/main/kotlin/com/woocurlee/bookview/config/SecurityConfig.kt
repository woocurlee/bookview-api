package com.woocurlee.bookview.config

import com.woocurlee.bookview.service.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val authorizationRequestResolver: OAuth2AuthorizationRequestResolver,
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/", "/login/**", "/oauth2/**", "/css/**", "/js/**", "/images/**")
                    .permitAll()
                    .requestMatchers("/api/users", "/api/users/db-info")
                    .permitAll()
                    .requestMatchers("/api/**")
                    .authenticated()
                    .anyRequest()
                    .authenticated()
            }.oauth2Login { oauth2 ->
                oauth2
                    .authorizationEndpoint { authorization ->
                        authorization.authorizationRequestResolver(authorizationRequestResolver)
                    }.userInfoEndpoint { userInfo ->
                        userInfo.userService(customOAuth2UserService)
                    }.defaultSuccessUrl("/", true)
            }.logout { logout ->
                logout
                    .logoutSuccessUrl("/")
                    .permitAll()
            }

        return http.build()
    }
}
