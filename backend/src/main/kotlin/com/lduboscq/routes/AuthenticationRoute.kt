package com.lduboscq.routes

import com.lduboscq.authentication.JwtConfig
import com.lduboscq.StudentAlreadyExistsWithThisUsername
import com.lduboscq.TeacherAlreadyExistsWithThisEmail
import com.lduboscq.authentication.LoginDto
import com.lduboscq.authentication.UserStudentDto
import com.lduboscq.authentication.UserTeacherDto
import com.lduboscq.envolee.mobileshared.LoginResponse
import com.lduboscq.envolee.mobileshared.SignupDto
import com.lduboscq.repositories.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.loginRoute(userRepository: UserRepository) {
    post("/login") {
        val credentials = call.receive<LoginDto>()
        val user = userRepository.findUserByCredentials(credentials)
        user?.let { it1 ->
            val token = JwtConfig.makeToken(it1)
            val response = LoginResponse(
                token = token,
                userRole = when (user) {
                    is UserTeacherDto -> "teacher"
                    is UserStudentDto -> "student"
                    else -> throw IllegalAccessError()
                }
            )
            call.respond(response)
        } ?: run {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}

fun Routing.signupRoute(userRepository: UserRepository) {
    post("/signup") {
        val body = call.receive<SignupDto>()
        when (body.role) {
            "teacher" -> {
                val user: UserTeacherDto? = try {
                    userRepository.signupTeacher(
                        email = body.email, displayedName = body.displayedName,
                        materials = body.courseSubject
                    )
                } catch (e: TeacherAlreadyExistsWithThisEmail) {
                    null
                }
                if (user == null) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        "a teacher already exists with this email"
                    )
                } else {
                    call.respond(HttpStatusCode.Created, "user ${user.userId} created")
                }
            }
            "student" -> {
                val user: UserStudentDto? = try {
                    userRepository.signupStudent(username = body.username)
                } catch (e: StudentAlreadyExistsWithThisUsername) {
                    null
                }

                if (user == null) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        "a student already exists with this username"
                    )
                } else {
                    call.respond(HttpStatusCode.Created, "user ${user.userId} created")
                }
            }
            else -> {
                throw IllegalAccessError()
            }
        }
    }
}

fun Routing.getAllStudentsRoute(userRepository: UserRepository) {
    get("/all-students") {
        val users = userRepository.getAllUsers().filterIsInstance<UserStudentDto>()
        call.respond(users)
    }
}

fun Routing.getAllTeachersRoute(userRepository: UserRepository) {
    get("/all-teachers") {
        val users = userRepository.getAllUsers().filterIsInstance<UserTeacherDto>()
        call.respond(users)
    }
}
