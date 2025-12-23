package com.woocurlee.bookview.dto

data class UserResponse(
    val id: String,
    val nickname: String,
    val profileImageUrl: String?,
    val email: String?,
)
