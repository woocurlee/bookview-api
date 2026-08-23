package com.woocurlee.bookview.service

import com.woocurlee.bookview.domain.Status
import com.woocurlee.bookview.domain.User
import com.woocurlee.bookview.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class UserServiceTest {
    @Mock lateinit var userRepository: UserRepository

    private lateinit var userService: UserService

    private val existingUser =
        User(
            googleId = "google-1",
            nickname = "old_nickname",
            isNicknameSet = true,
        )

    @BeforeEach
    fun setUp() {
        userService = UserService(userRepository)
        given(userRepository.findByGoogleIdAndStatus("google-1", Status.ACTIVE)).willReturn(existingUser)
    }

    @Test
    fun `정상 닉네임이면 변경되어 저장된다`() {
        given(userRepository.existsByNicknameAndStatus("new_nickname", Status.ACTIVE)).willReturn(false)
        given(userRepository.save(any())).willAnswer { it.arguments[0] as User }

        val updated = userService.updateNickname("google-1", "new_nickname")

        assertThat(updated?.nickname).isEqualTo("new_nickname")
    }

    @ParameterizedTest
    @ValueSource(strings = ["admin", "root", "test", "official", "bookview"])
    fun `예약어와 정확히 일치하면 예외가 발생하고 메시지에 해당 단어가 포함된다`(reserved: String) {
        assertThatThrownBy { userService.updateNickname("google-1", reserved) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining(reserved)
    }

    @ParameterizedTest
    @ValueSource(strings = ["admin_official", "admin.official", "official_admin"])
    fun `예약어가 마침표나 밑줄로 구분된 토큰으로 포함되면 예외가 발생한다`(nickname: String) {
        assertThatThrownBy { userService.updateNickname("google-1", nickname) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @ParameterizedTest
    @ValueSource(strings = ["admin123", "testimonial", "attestation"])
    fun `예약어를 부분 문자열로만 포함하고 토큰 전체와는 다르면 통과한다`(nickname: String) {
        given(userRepository.existsByNicknameAndStatus(nickname, Status.ACTIVE)).willReturn(false)
        given(userRepository.save(any())).willAnswer { it.arguments[0] as User }

        val updated = userService.updateNickname("google-1", nickname)

        assertThat(updated?.nickname).isEqualTo(nickname)
    }

    @Test
    fun `이미 사용 중인 닉네임이면 예외가 발생한다`() {
        given(userRepository.existsByNicknameAndStatus("taken_nickname", Status.ACTIVE)).willReturn(true)

        assertThatThrownBy { userService.updateNickname("google-1", "taken_nickname") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("이미 사용 중인 닉네임")
    }

    @Test
    fun `허용되지 않는 문자가 포함되면 예외가 발생한다`() {
        assertThatThrownBy { userService.updateNickname("google-1", "Invalid Name!") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}
