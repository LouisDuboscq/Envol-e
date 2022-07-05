package com.lduboscq.routes

import com.lduboscq.envolee.mobileshared.ControlGetDto
import com.lduboscq.envolee.mobileshared.ControlPostDto
import com.lduboscq.repositories.ControlRepository
import com.lduboscq.repositories.memory.CourseRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.postControlRoute(
    controlRepository: ControlRepository,
    courseRepository: CourseRepository
) {
    post("/courses/{courseId}/controls") {
        val courseId = call.parameters["courseId"]?.toLongOrNull() ?: run {
            call.respond(HttpStatusCode.BadRequest, "courseId not an id")
            return@post
        }
        val controlDto = call.receive<ControlPostDto>()
        val course = courseRepository.getCourseById(courseId)
        if (course == null) {
            call.respond(HttpStatusCode.BadRequest, "course $courseId not found")
            return@post
        }
        val controlId = controlRepository.addForCourse(controlDto.name, course)
        call.respond(status = HttpStatusCode.Created, "Control $controlId created")
    }
}

fun Route.getControlsRoute(
    courseRepository: CourseRepository,
    controlRepository: ControlRepository
) {
    get("/courses/{courseId}/controls") {
        val courseId = call.parameters["courseId"]?.toLongOrNull() ?: run {
            call.respond(HttpStatusCode.BadRequest, "courseId not an id")
            return@get
        }
        val course = courseRepository.getCourseById(courseId)
        if (course == null) {
            call.respond(HttpStatusCode.BadRequest, "course $courseId not found")
            return@get
        }
        val controls = controlRepository.getAllControlsForCourse(course).map {
            ControlGetDto(it.id, it.name)
        }
        call.respond(controls)
    }
}