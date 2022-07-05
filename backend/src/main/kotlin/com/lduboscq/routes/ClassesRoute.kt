package com.lduboscq.routes

import com.lduboscq.envolee.mobileshared.CourseGetDto
import com.lduboscq.envolee.mobileshared.CoursePostDto
import com.lduboscq.authentication.UserSession
import com.lduboscq.authentication.UserStudentDto
import com.lduboscq.authentication.UserTeacherDto
import com.lduboscq.repositories.memory.CourseRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.postCourseRoute(courseRepository: CourseRepository) {
    post("/courses") {
        val courseDto = call.receive<CoursePostDto>()
        val principal = call.principal<UserTeacherDto>()
        if (principal == null) {
            call.respond(HttpStatusCode.Forbidden, "user should be logged in as teacher")
            return@post
        }
        val course = courseRepository.addForTeacher(courseDto.name, principal)
        call.respond(status = HttpStatusCode.Created, "course ${course.id} created")
    }
}

fun Route.getCoursesRoute(courseRepository: CourseRepository) {
    get("/courses") {
        val courses = when (val user = call.principal<UserSession>()) {
            is UserTeacherDto -> {
                courseRepository.getAllCoursesForTeacher(user)
                    .map {
                        CourseGetDto(it.id, it.name)//, it.invitationCode)
                    }
            }
            is UserStudentDto -> {
                courseRepository.getAllCoursesForStudent(user)
                    .map {
                        CourseGetDto(it.id, it.name)//, it.invitationCode)
                    }
            }
            else -> {
                call.respond(HttpStatusCode.Forbidden, "user should be logged in as teacher or student")
                return@get
            }
        }
        call.respond(courses)
    }
}


