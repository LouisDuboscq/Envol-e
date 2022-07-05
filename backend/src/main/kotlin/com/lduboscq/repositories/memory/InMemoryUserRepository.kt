package com.lduboscq.repositories.memory

import com.lduboscq.StudentAlreadyExistsWithThisUsername
import com.lduboscq.TeacherAlreadyExistsWithThisEmail
import com.lduboscq.authentication.LoginDto
import com.lduboscq.authentication.UserSession
import com.lduboscq.authentication.UserStudentDto
import com.lduboscq.authentication.UserTeacherDto
import com.lduboscq.repositories.UserRepository
import kotlin.random.Random

class InMemoryUserRepository : UserRepository {

    private val users = mutableListOf<UserSession>()

    override fun findUserById(id: Long): UserSession? = users.firstOrNull {
        when (it) {
            is UserTeacherDto -> it.userId == id
            is UserStudentDto -> it.userId == id
            else -> throw IllegalAccessError()
        }
    }

    override fun findUserByCredentials(credential: LoginDto): UserSession? =
        users.firstOrNull {
            when (it) {
                is UserTeacherDto -> it.email == credential.teacherEmailOrStudentUsername
                is UserStudentDto -> it.username == credential.teacherEmailOrStudentUsername
                else -> throw IllegalAccessError()
            }
        }

    override fun signupStudent(username: String): UserStudentDto {
        val userId = Random.nextLong()
        val user = UserStudentDto(userId, username)

        val students = getAllUsers().filterIsInstance<UserStudentDto>()
        val studentAlreadyExists = students.any { it.username == username }
        if (studentAlreadyExists) throw StudentAlreadyExistsWithThisUsername()

        users.add(user)
        return user
    }

    override fun signupTeacher(
        email: String,
        displayedName: String,
        materials: String
    ): UserTeacherDto {
        val userId = Random.nextLong()
        val teachers = getAllUsers().filterIsInstance<UserTeacherDto>()
        val teacherAlreadyExists = teachers.any { it.email == email }
        if (teacherAlreadyExists) throw TeacherAlreadyExistsWithThisEmail()

        val user = UserTeacherDto(userId, email, displayedName, materials)
        users.add(user)
        return user
    }

    override fun getAllUsers(): List<UserSession> = users

    override fun getTeacher(teacherId: Long): UserTeacherDto? {
        return users.filterIsInstance<UserTeacherDto>().firstOrNull {
            it.userId == teacherId
        }
    }

    override fun getById(id: Long): UserSession? {
        return users.firstOrNull {
            when (it) {
                is UserStudentDto -> it.userId == id
                is UserTeacherDto -> it.userId == id
                else -> throw IllegalAccessError()
            }
        }
    }
}