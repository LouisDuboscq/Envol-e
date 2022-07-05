package com.lduboscq.routes

import com.lduboscq.envolee.mobileshared.StudentGetDto
import com.lduboscq.envolee.mobileshared.StudentPostDto
import com.lduboscq.model.StudentInCourse
import com.lduboscq.repositories.StudentRepository
import com.lduboscq.repositories.memory.CourseRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.random.Random.Default.nextLong

fun Route.getStudentsRoute(
    studentRepository: StudentRepository,
    courseRepository: CourseRepository
) {
    get("/courses/{courseId}/students") {
        val courseId = call.parameters["courseId"]?.toLongOrNull() ?: run {
            call.respond(HttpStatusCode.BadRequest, "courseId not an id")
            return@get
        }
        val course = courseRepository.getCourseById(courseId)
        if (course == null) {
            call.respond(HttpStatusCode.BadRequest, "class $courseId not found")
            return@get
        }
        val students = studentRepository.getAllStudentsForCourse(course)
        call.respond(students.map {
            StudentGetDto(id = it.id, name = it.nameGivenByTeacher)
        })
    }
}

fun Route.postStudentRoute(
    studentRepository: StudentRepository,
    courseRepository: CourseRepository
) {
    post("/courses/{courseId}/students") {
        val courseId = call.parameters["courseId"]?.toLongOrNull() ?: run {
            call.respond(HttpStatusCode.BadRequest, "courseId not an id")
            return@post
        }
        val course = courseRepository.getCourseById(courseId)
        if (course == null) {
            call.respond(HttpStatusCode.BadRequest, "class $courseId not found")
            return@post
        }

        val studentDto = call.receive<StudentPostDto>()
        val student = StudentInCourse(
            id = nextLong(),
            nameGivenByTeacher = studentDto.name,
            course = course,
            attachedUserStudent = null
        )
        studentRepository.addStudent(student)
        call.respond(status = HttpStatusCode.Created, "student ${student.id} created")
    }
}

fun Route.postStudentListRoute(
    studentRepository: StudentRepository,
    courseRepository: CourseRepository
) {
    post("/courses/{courseId}/students-list") {
        val courseId = call.parameters["courseId"]?.toLongOrNull() ?: run {
            call.respond(HttpStatusCode.BadRequest, "courseId not an id")
            return@post
        }
        val classObj = courseRepository.getCourseById(courseId)
        if (classObj == null) {
            call.respond(HttpStatusCode.BadRequest, "class $courseId not found")
            return@post
        }

        val studentDtoList = call.receive<List<StudentPostDto>>()

        val ids =
            studentRepository.addAllStudentsToCourse(classObj, studentDtoList.map { it.name })
        call.respond(
            status = HttpStatusCode.Created,
            "${studentDtoList.size} students created : $ids"
        )
    }
}
