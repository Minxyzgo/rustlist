package io.minxyzgo

import kotlinx.serialization.Serializable
import java.security.SecureRandom
import java.util.Base64

const val TOKEN_LENGTH = 32

@Serializable
data class Config(
    val port: Int = 8080,
    val accessToken: String = run {
        val random = SecureRandom()
        val tokenBytes = ByteArray(TOKEN_LENGTH)
        random.nextBytes(tokenBytes)
        Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes)
    },
    val staticRoomList: List<RoomDescription> = listOf(),
    val updatePeriod: Long = 2000,
    val roomTick: Int = 5
)