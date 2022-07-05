package com.lduboscq.repositories.memory

import com.lduboscq.authentication.UserStudentDto
import com.lduboscq.authentication.UserTeacherDto
import com.lduboscq.model.Course

interface CourseRepository {
    fun addForTeacher(courseName: String, teacher: UserTeacherDto): Course
    fun getAllCoursesForTeacher(teacher: UserTeacherDto): List<Course>
    fun getCourseById(classId: Long): Course?
    fun getAllCoursesForStudent(student: UserStudentDto): List<Course>
    fun registerStudentToCourse(student: UserStudentDto, course: Course)
}