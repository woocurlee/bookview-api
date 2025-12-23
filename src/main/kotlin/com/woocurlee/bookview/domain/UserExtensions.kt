package com.woocurlee.bookview.domain

import com.woocurlee.bookview.dto.UserResponse

fun User.toResponse(): UserResponse =
    UserResponse(
        id = this.id ?: "",
        nickname = this.nickname,
        profileImageUrl = this.profileImageUrl,
        email = this.email,
    )
