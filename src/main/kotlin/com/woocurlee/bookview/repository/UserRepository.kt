package com.woocurlee.bookview.repository

import com.woocurlee.bookview.domain.User
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, String> {
    fun findByGoogleId(googleId: String): User?
}
