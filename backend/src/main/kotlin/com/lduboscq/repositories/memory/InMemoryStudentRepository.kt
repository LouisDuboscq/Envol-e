package com.lduboscq.repositories.memory

import com.lduboscq.authentication.UserStudentDto
import com.lduboscq.model.Course
import com.lduboscq.model.StudentInCourse
import com.lduboscq.repositories.StudentRepository
import kotlin.random.Random

class InMemoryStudentRepository : StudentRepository {

    private val students = mutableListOf<StudentInCourse>()

    override fun getAllStudentsForCourse(course: Course): List<StudentInCourse> {
        return students.filter { it.course.id == course.id }
    }

    override fun addStudent(student: StudentInCourse) {
        students.add(student)
    }

    override fun attachLoggedUser(student: StudentInCourse, user: UserStudentDto) {
        val index = students.indexOf(student)
        students[index] = student.copy(attachedUserStudent = user)
    }

    override fun addAllStudentsToCourse(course: Course, studentNameList: List<String>): List<Long> {
        return studentNameList.map {
            val studentId = Random.nextLong()
            students.add(
                StudentInCourse(
                    id = studentId,
                    nameGivenByTeacher = it,
                    course = course,
                    attachedUserStudent = null
                )
            )
            studentId
        }
    }

    override fun get(studentId: Long): StudentInCourse? {
        return students.firstOrNull { it.id == studentId }
    }

    override fun getAllStudents(): List<StudentInCourse> {
        return students
    }
}