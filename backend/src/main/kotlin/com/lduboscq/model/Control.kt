package com.lduboscq.model

import com.lduboscq.authentication.UserStudentDto
import com.lduboscq.authentication.UserTeacherDto

data class Control(val id: Long, val name: String, val course: Course)

data class Audio(
    val classId: Long,
    val studentId: Long,
    val controlId: Long,
    val base64: String
)

data class Course(
    val id: Long,
    val name: String,
    val teacher: UserTeacherDto,
    val students: List<UserStudentDto>,
    //  val invitationCode: String
)

data class StudentInCourse(
    val id: Long,
    val nameGivenByTeacher: String,
    val course: Course,
    val attachedUserStudent: UserStudentDto?
)
