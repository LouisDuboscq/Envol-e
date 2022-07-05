package com.lduboscq.envolee.android.di

import com.lduboscq.envolee.android.asteacher.courses.AsTeacherCoursesViewModel
import com.lduboscq.envolee.android.asteacher.detailclass.AsTeacherDetailClassViewModel
import com.lduboscq.envolee.android.asteacher.detailcontrol.AsTeacherDetailControlViewModel
import com.lduboscq.envolee.android.asteacher.students.AsTeacherStudentsViewModel
import com.lduboscq.envolee.android.asstudent.courses.StudentAccessCoursesViewModel
import com.lduboscq.envolee.android.asstudent.detailcontrol.StudentAccessDetailControlViewModel
import com.lduboscq.envolee.android.asstudent.detailcourse.StudentAccessDetailCourseViewModel
import com.lduboscq.envolee.android.asteacher.createfeedback.CreateFeedbackViewModel
import com.lduboscq.envolee.android.asteacher.history.AsTeacherHistoryForAStudentViewModel
import com.lduboscq.envolee.android.common.login.LoginViewModel
import com.lduboscq.envolee.android.common.signup.SignupViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { AsTeacherCoursesViewModel(authenticatedApiInstance = get()) }
    viewModel { (courseId: Long) ->
        AsTeacherDetailClassViewModel(
            authenticatedApiInstance = get(),
            courseId = courseId
        )
    }
    viewModel { (controlId: Long, courseId: Long) ->
        AsTeacherDetailControlViewModel(
            authenticatedApiInstance = get(),
            controlId = controlId,
            courseId = courseId
        )
    }
    viewModel { (courseId: Long) ->
        AsTeacherStudentsViewModel(
            authenticatedApiInstance = get(),
            courseId = courseId,
            settings = get()
        )
    }
    viewModel { StudentAccessCoursesViewModel(authenticatedApiInstance = get()) }
    viewModel { (courseId: Long, controlId: Long) ->
        StudentAccessDetailControlViewModel(
            authenticatedApiInstance = get(),
            courseId = courseId,
            controlId = controlId
        )
    }
    viewModel { (courseId: Long) ->
        StudentAccessDetailCourseViewModel(
            authenticatedApiInstance = get(),
            courseId = courseId
        )
    }
    viewModel { (courseId: Long, controlId: Long, studentId: Long) ->
        CreateFeedbackViewModel(
            controlId = controlId,
            authenticatedApiInstance = get(),
            context = get(),
            courseId = courseId,
            studentId = studentId
        )
    }
    viewModel { (courseId: Long, studentId: Long) ->
        AsTeacherHistoryForAStudentViewModel(courseId, studentId, authenticatedApi = get())
    }
    viewModel {
        SignupViewModel(
            loginAndStoreToken = get(),
            signupUser = get()
        )
    }
    viewModel {
        LoginViewModel(loginAndStoreToken = get())
    }
}
