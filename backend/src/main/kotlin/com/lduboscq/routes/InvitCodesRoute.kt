package com.lduboscq.routes

import com.lduboscq.authentication.UserStudentDto
import com.lduboscq.authentication.UserTeacherDto
import com.lduboscq.envolee.mobileshared.CodeForStudent
import com.lduboscq.envolee.mobileshared.RegisterCourseResponse
import com.lduboscq.generateRandomInvitationCode
import com.lduboscq.model.Course
import com.lduboscq.model.StudentInCourse
import com.lduboscq.repositories.InvitationCodeRepository
import com.lduboscq.repositories.StudentRepository
import com.lduboscq.repositories.UserRepository
import com.lduboscq.repositories.memory.CourseRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.createInvitationCodeForCourseRoute(
    invitationCodeRepository: InvitationCodeRepository,
    courseRepository: CourseRepository,
    studentRepository: StudentRepository
) {

    get("/create-invit-code-for-course/{courseId}") {
        val loggedInTeacher: UserTeacherDto = call.principal()!!

        val courseId = call.parameters["courseId"]?.toLongOrNull() ?: run {
            call.respond(HttpStatusCode.BadRequest, "courseId not an id")
            return@get
        }

        val course = courseRepository.getCourseById(courseId)
        if (course == null) {
            call.respond(HttpStatusCode.BadRequest, "course $courseId not found")
            return@get
        }

        val studentsInCourse = studentRepository.getAllStudentsForCourse(course)

        val codesForStudents: List<CodeForStudent> = studentsInCourse.map { student ->
            val existingCode = invitationCodeRepository.getCodeForStudent(student.id)

            if (existingCode == null) {
                val codeForStudent = CodeForStudent(
                    studentId = student.id,
                    invitationCode = generateRandomInvitationCode(),
                    courseId = courseId,
                    teacherId = loggedInTeacher.userId,
                    studentName = student.nameGivenByTeacher
                )
                invitationCodeRepository.addCode(codeForStudent)
                codeForStudent
            } else {
                CodeForStudent(
                    studentId = student.id,
                    invitationCode = existingCode.invitationCode,
                    courseId = courseId,
                    teacherId = loggedInTeacher.userId,
                    studentName = student.nameGivenByTeacher
                )
            }
        }
        call.respond(codesForStudents)
    }
}

fun Route.doesInvitationCodeExistsRoute(
    userRepository: UserRepository,
    invitationCodeRepository: InvitationCodeRepository,
    courseRepository: CourseRepository,
    studentRepository: StudentRepository
) {
    get("/invitation-code-exist/{code}") {
        val code = call.parameters["code"] ?: run {
            call.respond(HttpStatusCode.BadRequest, "code not an id")
            return@get
        }

        val cfs: CodeForStudent? = invitationCodeRepository.getCodeForStudentByIdCode(code)
        if (cfs == null) {
            call.respond(HttpStatusCode.NotFound, "invitation code does not exist")
            return@get
        }

        val student: StudentInCourse? = studentRepository.get(cfs.studentId)
        if (student == null) {
            call.respond(
                HttpStatusCode.InternalServerError,
                "invitation code exists but student for this code no longer exists"
            )
            return@get
        }

        val course: Course? = courseRepository.getCourseById(cfs.courseId)
        if (course == null) {
            call.respond(
                HttpStatusCode.InternalServerError,
                "invitation code exists but course for this code no longer exists"
            )
            return@get
        }

        val teacher: UserTeacherDto? = userRepository.getTeacher(cfs.teacherId)
        if (teacher == null) {
            call.respond(
                HttpStatusCode.InternalServerError,
                "invitation code exists but teacher for this code no longer exists"
            )
            return@get
        }

        if (invitationCodeRepository.isCodeExists(code)) {
            call.respond(
                RegisterCourseResponse(
                    studentName = student.nameGivenByTeacher,
                    courseName = course.name,
                    teacherName = teacher.displayedName,
                    //   materialName = teacher.materialName
                )
            )
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}

fun Route.registerCourseViaCodeRoute(
    userRepository: UserRepository,
    courseRepository: CourseRepository,
    invitationCodeRepository: InvitationCodeRepository,
    studentRepository: StudentRepository
) {
    get("/register-course-via-code/{code}") {

        val loggedInUser = call.principal<UserStudentDto>()

        if (loggedInUser == null) {
            call.respond(HttpStatusCode.Forbidden, "user should be logged in as student")
            return@get
        }

        val code: String = call.parameters["code"] ?: run {
            call.respond(HttpStatusCode.BadRequest, "code not an id")
            return@get
        }

        val cfs: CodeForStudent? = invitationCodeRepository.getCodeForStudentByIdCode(code)
        if (cfs == null) {
            call.respond(HttpStatusCode.NotFound, "invitation code does not exist")
            return@get
        }

        val student: StudentInCourse? = studentRepository.get(cfs.studentId)
        if (student == null) {
            call.respond(
                HttpStatusCode.InternalServerError,
                "invitation code exists but student for this code no longer exists"
            )
            return@get
        }

        val course: Course? = courseRepository.getCourseById(cfs.courseId)
        if (course == null) {
            call.respond(
                HttpStatusCode.InternalServerError,
                "invitation code exists but course for this code no longer exists"
            )
            return@get
        }

        val teacher: UserTeacherDto? = userRepository.getTeacher(cfs.teacherId)
        if (teacher == null) {
            call.respond(
                HttpStatusCode.InternalServerError,
                "invitation code exists but teacher for this code no longer exists"
            )
            return@get
        }
        // --start-- business logic for registering a student to a course
        courseRepository.registerStudentToCourse(loggedInUser, course)
        studentRepository.attachLoggedUser(student, loggedInUser)
        // --end--

        call.respond(HttpStatusCode.Accepted)
    }
}
