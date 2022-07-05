package com.lduboscq.repositories

import com.lduboscq.authentication.LoginDto
import com.lduboscq.authentication.UserSession
import com.lduboscq.authentication.UserStudentDto
import com.lduboscq.authentication.UserTeacherDto

interface UserRepository {
    fun findUserById(id: Long): UserSession?
    fun findUserByCredentials(credential: LoginDto): UserSession?
    fun signupStudent(username: String): UserStudentDto

    fun signupTeacher(
        email: String,
        displayedName: String,
        materials: String
    ): UserTeacherDto

    fun getAllUsers(): List<UserSession>
    fun getTeacher(teacherId: Long): UserTeacherDto?
    fun getById(id: Long): UserSession?
}
