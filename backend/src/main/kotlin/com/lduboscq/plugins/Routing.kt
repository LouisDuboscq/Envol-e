package com.lduboscq.plugins

import com.lduboscq.routes.*
import com.lduboscq.repositories.AudioRepository
import com.lduboscq.repositories.ControlRepository
import com.lduboscq.repositories.InvitationCodeRepository
import com.lduboscq.repositories.StudentRepository
import com.lduboscq.repositories.UserRepository
import com.lduboscq.repositories.memory.CourseRepository
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureRouting(
    userRepository: UserRepository,
    audioRepository: AudioRepository,
    invitationCodeRepository: InvitationCodeRepository,
    courseRepository: CourseRepository,
    studentRepository: StudentRepository,
    controlRepository: ControlRepository
) {

    routing {
        loginRoute(userRepository)
        signupRoute(userRepository)
        getAllTeachersRoute(userRepository)
        getAllStudentsRoute(userRepository)

        authenticate {
            postCourseRoute(courseRepository)
            getCoursesRoute(courseRepository)

            getStudentsRoute(studentRepository, courseRepository)
            postStudentRoute(studentRepository, courseRepository)
            postStudentListRoute(studentRepository, courseRepository)

            getControlsRoute(courseRepository, controlRepository)
            postControlRoute(controlRepository, courseRepository)

            postAudioRoute(audioRepository)

            meRoute(userRepository, studentRepository)

            createInvitationCodeForCourseRoute(
                invitationCodeRepository,
                courseRepository,
                studentRepository
            )
            doesInvitationCodeExistsRoute(
                userRepository,
                invitationCodeRepository,
                courseRepository,
                studentRepository
            )
            registerCourseViaCodeRoute(
                userRepository,
                courseRepository,
                invitationCodeRepository,
                studentRepository
            )
        }
    }
}
