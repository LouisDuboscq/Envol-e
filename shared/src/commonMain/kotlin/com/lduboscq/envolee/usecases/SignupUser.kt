package com.lduboscq.envolee.usecases

import com.lduboscq.envolee.remote.Api

class SignupUser(private val api: Api) {
    suspend operator fun invoke(
        teacherEmail: String,
        displayedName: String,
        courseSubject: String,
        password: String,
        studentUsername: String,
        userRole: String
    ) {
        api.signup(
            teacherEmail = teacherEmail,
            displayedName = displayedName,
            courseSubject = courseSubject,
            password = password,
            studentUsername = studentUsername,
            userRole = userRole
        )
    }
}