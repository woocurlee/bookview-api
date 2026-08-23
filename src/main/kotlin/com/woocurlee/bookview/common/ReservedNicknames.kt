package com.woocurlee.bookview.common

/** 사칭·혼동 우려가 있어 닉네임으로 단독 사용할 수 없는 예약어 (토큰 단위로 매칭) */
object ReservedNicknames {
    val VALUES =
        setOf(
            // 운영/시스템
            "admin",
            "administrator",
            "root",
            "system",
            "staff",
            "moderator",
            "mod",
            "manager",
            "superuser",
            "master",
            // 지원/공지
            "support",
            "help",
            "notice",
            "official",
            "service",
            "security",
            // 서비스/브랜드
            "bookview",
            // 테스트/더미
            "test",
            "testuser",
            "null",
            "undefined",
            "none",
            "deleted",
            "anonymous",
            "guest",
            "unknown",
        )
}
