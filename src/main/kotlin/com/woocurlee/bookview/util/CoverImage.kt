package com.woocurlee.bookview.util

import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import org.springframework.stereotype.Component

/**
 * 도서 표지 이미지 URL 헬퍼.
 * 카카오 책 썸네일(120x174 저화질) URL의 `fname` 파라미터에는 원본 이미지 주소가 들어있어,
 * 이를 디코드하면 고화질(~458px) 커버를 얻을 수 있다. (BKVW-11)
 * 비공식 URL이므로 실패 대비 표시 측에서 onerror 폴백(원본 썸네일)을 함께 둔다.
 */
@Component("coverImage")
class CoverImage {
    /**
     * 카카오 썸네일 URL → fname 원본(고화질, https) URL로 변환.
     * 카카오 thumb URL이 아니거나 fname이 없으면 입력값을 그대로 반환한다.
     */
    fun hiRes(url: String?): String {
        val u = url ?: return ""
        if (!u.contains("kakaocdn.net/thumb/") || !u.contains("fname=")) return u
        val fname = u.substringAfter("fname=", "")
        if (fname.isBlank()) return u
        val decoded = URLDecoder.decode(fname, StandardCharsets.UTF_8)
        return when {
            decoded.startsWith("http://") -> "https://" + decoded.substring("http://".length)
            decoded.startsWith("https://") -> decoded
            else -> u
        }
    }
}
