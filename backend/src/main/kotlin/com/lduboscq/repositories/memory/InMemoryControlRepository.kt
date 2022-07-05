package com.lduboscq.repositories.memory

import com.lduboscq.model.Control
import com.lduboscq.model.Course
import com.lduboscq.repositories.ControlRepository
import kotlin.random.Random

class InMemoryControlRepository: ControlRepository {

    private val allControls = mutableListOf<Control>()

    override fun getAllControlsForCourse(course: Course): List<Control> {
        return allControls.filter { it.course.id == course.id }
    }

    override fun addForCourse(controlName: String, course: Course): Long {
        val controlId = Random.nextLong()
        allControls.add(
            Control(
                id = controlId,
                name = controlName,
                course = course
            )
        )
        return controlId
    }
}