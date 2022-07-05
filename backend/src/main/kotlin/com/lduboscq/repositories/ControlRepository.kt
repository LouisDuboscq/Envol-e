package com.lduboscq.repositories

import com.lduboscq.model.Course
import com.lduboscq.model.Control
import kotlin.random.Random

interface ControlRepository {
    fun getAllControlsForCourse(course: Course): List<Control>
    fun addForCourse(controlName: String, course: Course): Long
}

