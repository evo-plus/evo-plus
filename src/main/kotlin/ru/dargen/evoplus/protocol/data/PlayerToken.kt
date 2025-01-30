package ru.dargen.evoplus.protocol.data

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import pro.diamondworld.protocol.packet.VerificationToken
import java.security.KeyFactory
import java.security.interfaces.RSAKey
import java.security.spec.X509EncodedKeySpec
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.util.*

interface PlayerToken {

    val token: String
    val player: String
    val expiresAt: Instant

    val isExpired: Boolean
    val isWorking: Boolean

    data class Parsed(
        override val token: String,
        override val player: String, override val expiresAt: Instant,
    ) : PlayerToken {

        override val isExpired get() = Instant.now().isAfter(expiresAt)
        override val isWorking get() = !isExpired

        val expireIn get() = Duration.between(Instant.now(), expiresAt)

        override fun toString(): String {
            return "PlayerToken(name=$player, ${if (isExpired) "expired=true" else "expireIn=${expireIn.seconds}"})"
        }

    }

    data class Invalid(
        private val tokenOptional: String? = null, private val optionalPlayer: String? = null,
    ) : PlayerToken {

        override val token: String
            get() = tokenOptional ?: throw UnsupportedOperationException("Invalid token")
        override val player: String
            get() = optionalPlayer ?: throw UnsupportedOperationException("Invalid token")
        override val expiresAt: Instant
            get() = throw UnsupportedOperationException("Invalid token")

        override val isExpired = true
        override val isWorking = false

        override fun toString(): String {
            return "InvalidToken(player=${optionalPlayer})"
        }

    }

    companion object {

        private val Decoder: JWTVerifier

        init {
            val keyBytes = Base64.getDecoder().decode(PlayerToken::class.java.getResource("/token.pub")?.readText())
            val key = KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(keyBytes)) as RSAKey
            val algo = com.auth0.jwt.algorithms.Algorithm.RSA256(key)
            Decoder = JWT.require(algo).ignoreIssuedAt().build()
        }

        fun VerificationToken.parse(): PlayerToken {
            if (token == null) {
                return Invalid()
            }

            return runCatching {
                val decoded = Decoder.verify(token)
                Parsed(token, decoded.subject, decoded.expiresAtAsInstant.atZone(ZoneId.systemDefault()).toInstant())
            }.apply { exceptionOrNull()?.printStackTrace() }.getOrNull() ?: runCatching {
                val decoded = JWT.decode(token)
                Invalid(token, decoded.subject)
            }.getOrElse { Invalid() }
        }
    }

}