package com.lduboscq.routes

import com.lduboscq.envolee.mobileshared.GetMeResponse
import com.lduboscq.envolee.mobileshared.StudentIdForCourse
import com.lduboscq.model.StudentInCourse
import com.lduboscq.authentication.UserSession
import com.lduboscq.authentication.UserStudentDto
import com.lduboscq.authentication.UserTeacherDto
import com.lduboscq.repositories.StudentRepository
import com.lduboscq.repositories.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.meRoute(
    userRepository: UserRepository,
    studentRepository: StudentRepository
) {
    get("/me") {
        val loggedInUser = call.principal<UserSession>()
        val loggedInUserId = when (loggedInUser) {
            is UserTeacherDto -> loggedInUser.userId
            is UserStudentDto -> loggedInUser.userId
            else -> throw IllegalAccessError()
        }

        val allStudents: List<StudentInCourse> = studentRepository.getAllStudents()
        val studentsInCourses = allStudents.filter {
            it.attachedUserStudent?.userId == loggedInUserId
        }

        val response = GetMeResponse(
            userId = loggedInUserId,
            studentsInCourses.map {
                StudentIdForCourse(it.id, it.course.id)
            }
        )
        call.respond(response)
    }

    get("/users/{userId}") {
        val user = userRepository.getById(
            call.parameters["userId"]?.toLongOrNull()
                ?: throw IllegalAccessError("id should be a long")
        )
        if (user == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            call.respond(user)
        }
    }
}
