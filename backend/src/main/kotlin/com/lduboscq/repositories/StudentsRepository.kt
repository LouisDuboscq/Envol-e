package com.lduboscq.repositories

import com.lduboscq.authentication.UserStudentDto
import com.lduboscq.model.Course
import com.lduboscq.model.StudentInCourse

interface StudentRepository {
    fun getAllStudentsForCourse(course: Course): List<StudentInCourse>
    fun addStudent(student: StudentInCourse)
    fun attachLoggedUser(student: StudentInCourse, user: UserStudentDto)
    fun addAllStudentsToCourse(course: Course, studentNameList: List<String>): List<Long>
    fun get(studentId: Long): StudentInCourse?
    fun getAllStudents(): List<StudentInCourse>
}