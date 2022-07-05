package com.lduboscq.authentication

import com.auth0.jwt.*
import com.auth0.jwt.algorithms.*
import java.util.*

object JwtConfig {

    /*
    todo
    val secret = "a"//environment.config.property("jwt.secret").getString()
    val issuer = "b"//environment.config.property("jwt.issuer").getString()
    val audience = "c"//environment.config.property("jwt.audience").getString()
    val myRealm = "d"//environment.config.property("jwt.realm").getString()
     */

    private const val secret = "zAP5MBA4B4Ijz0MZaS48"
    private const val issuer = "ktor.io"
    private const val validityInMs = 36_000_00 * 10 // 10 hours
    private val algorithm = Algorithm.HMAC512(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    /**
     * Produce a token for this combination of User and Account
     */
    fun makeToken(user: UserSession): String {
        val id = when (user) {
            is UserTeacherDto -> user.userId
            is UserStudentDto -> user.userId
            else -> throw IllegalAccessError()
        }
        return JWT.create()
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withClaim("id", id)
            .withExpiresAt(getExpiration())
            .sign(algorithm)
    }

    /**
     * Calculate the expiration Date based on current time + the given validity
     */
    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)

}