package com.lduboscq.envolee.mobileshared

import kotlinx.serialization.Serializable

@Serializable
data class RegisterCourseResponse(
    val studentName: String,
    val courseName: String,
    val teacherName: String
)

@Serializable
data class CodeForStudent(
    val studentId: Long,
    val invitationCode: String,
    val courseId: Long,
    val teacherId: Long,
    val studentName: String
) {
    init {
        require(invitationCode.length == 6) {
            "invitation code : $invitationCode"
        }
    }
}

@Serializable
data class AudioPostDto(
    val courseId: Long,
    val studentId: Long,
    val controlId: Long,
    val base64: String
)

@Serializable
data class LoginPostDto(val email: String, val password: String)

@Serializable
data class SignupDto(
    val email: String,
    val password: String,
    val username: String,
    val displayedName: String,
    val courseSubject: String,
    val role: String
)

@Serializable
data class ControlGetDto(val id: Long, val name: String)

@Serializable
data class ControlPostDto(val name: String)

@Serializable
data class StudentPostDto(val name: String)

@Serializable
data class CoursePostDto(val name: String)

@Serializable
data class StudentGetDto(val id: Long, val name: String)

@Serializable
data class LoginResponse(val token: String, val userRole: String)

@Serializable
data class GetMeResponse(val userId: Long, val idsForCourses: List<StudentIdForCourse>)

@Serializable
data class StudentIdForCourse(val studentId: Long, val courseId: Long)

@Serializable
data class CourseGetDto(val id: Long, val name: String)
