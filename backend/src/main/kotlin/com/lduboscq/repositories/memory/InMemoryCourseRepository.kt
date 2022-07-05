package com.lduboscq.repositories.memory

import com.lduboscq.authentication.UserStudentDto
import com.lduboscq.authentication.UserTeacherDto
import com.lduboscq.model.Course
import kotlin.random.Random

class InMemoryCourseRepository: CourseRepository {

    private val allCourses = mutableListOf<Course>()

    override  fun addForTeacher(courseName: String, teacher: UserTeacherDto): Course {
        val courseId = Random.nextLong()
        val course = Course(
            id = courseId,
            name = courseName,
            teacher = teacher,
            students = emptyList()
        )
        allCourses.add(course)
        return course
    }

    override  fun getAllCoursesForTeacher(teacher: UserTeacherDto): List<Course> {
        return allCourses.filter { it.teacher == teacher }
    }

    override fun getCourseById(classId: Long): Course? {
        return allCourses.firstOrNull { it.id == classId }
    }

    override fun getAllCoursesForStudent(student: UserStudentDto): List<Course> {
        return allCourses.filter { it.students.contains(student) }
    }

    override fun registerStudentToCourse(student: UserStudentDto, course: Course) {
        val courseToUpdate = allCourses.first { it.id == course.id }
        val newCourse = courseToUpdate.copy(students = courseToUpdate.students + student)

        allCourses.remove(courseToUpdate)
        allCourses.add(newCourse)
    }
}
