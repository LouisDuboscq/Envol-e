package com.lduboscq.authentication

import io.ktor.server.auth.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface UserSession : Principal

@Serializable
data class LoginDto(
    @SerialName("email")
    val teacherEmailOrStudentUsername: String,
    val password: String
)

@Serializable
data class UserTeacherDto(
    val userId: Long,
    val email: String,
    val displayedName: String,
    val materialName: String
) : UserSession

@Serializable
data class UserStudentDto(
    val userId: Long,
    val username: String
) : UserSession
