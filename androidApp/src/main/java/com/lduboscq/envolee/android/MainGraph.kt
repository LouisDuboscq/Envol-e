package com.lduboscq.envolee.android

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lduboscq.envolee.Settings
import com.lduboscq.envolee.android.asstudent.courses.StudentAccessCoursesScreen
import com.lduboscq.envolee.android.asstudent.detailcontrol.StudentAccessDetailControlScreen
import com.lduboscq.envolee.android.asstudent.detailcourse.StudentAccessDetailCourseScreen
import com.lduboscq.envolee.android.asteacher.courses.AsTeacherCoursesScreen
import com.lduboscq.envolee.android.asteacher.detailclass.AsTeacherDetailClassScreen
import com.lduboscq.envolee.android.asteacher.detailcontrol.AsTeacherDetailControlScreen
import com.lduboscq.envolee.android.asteacher.history.AsTeacherHistoryForAStudentScreen
import com.lduboscq.envolee.android.asteacher.createfeedback.CreateFeedbackScreen
import com.lduboscq.envolee.android.common.login.LoginScreen
import com.lduboscq.envolee.android.common.signup.SignupAsStudentScreen
import com.lduboscq.envolee.android.common.signup.SignupAsTeacherScreen

@Composable
fun MainGraph(settings: Settings) {
    val navController = rememberNavController()

    val usernameOrEmailInCache = settings.getUsernameInCache() ?: ""
    val startDestination = if (usernameOrEmailInCache.isNotEmpty()) {
        "login"
    } else {
        "choose-student-or-teacher"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("choose-student-or-teacher") { ChooseStudentOrTeacherScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("signup-student") { SignupAsStudentScreen(navController) }
        composable("signup-teacher") { SignupAsTeacherScreen(navController) }
        composable("courses") { AsTeacherCoursesScreen(navController) }

        composable(
            "courses/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getLong("id") ?: throw IllegalAccessException()
            AsTeacherDetailClassScreen(navController, courseId)
        }

        composable(
            "courses/{courseId}/controls/{controlid}",
            arguments = listOf(navArgument("controlid") { type = NavType.LongType },
                navArgument("courseId") { type = NavType.LongType })
        ) { backStackEntry ->
            val controlId = backStackEntry.arguments?.getLong("controlid")
                ?: throw IllegalAccessException()
            val courseId = backStackEntry.arguments?.getLong("courseId")
                ?: throw IllegalAccessException()
            AsTeacherDetailControlScreen(navController, controlId, courseId)
        }

        composable(
            "courses/{courseId}/control/{controlid}/for-student/{studentid}",
            arguments = listOf(
                navArgument("courseId") { type = NavType.LongType },
                navArgument("controlid") { type = NavType.LongType },
                navArgument("studentid") { type = NavType.LongType },
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getLong("courseId")
                ?: throw IllegalAccessException()
            val controlId = backStackEntry.arguments?.getLong("controlid")
                ?: throw IllegalAccessException()
            val studentId = backStackEntry.arguments?.getLong("studentid")
                ?: throw IllegalAccessException()
            CreateFeedbackScreen(courseId, controlId, studentId)
        }

        composable("student-access-courses") {
            StudentAccessCoursesScreen(navController)
        }

        composable(
            "student-access-courses/{courseId}/controls",
            arguments = listOf(
                navArgument("courseId") { type = NavType.LongType },
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getLong("courseId")
                ?: throw IllegalAccessException()
            StudentAccessDetailCourseScreen(navController, courseId)
        }

        composable(
            "student-access-courses/{courseId}/controls/{controlId}",
            arguments = listOf(
                navArgument("controlId") { type = NavType.LongType },
                navArgument("courseId") { type = NavType.LongType },
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getLong("courseId")
                ?: throw IllegalAccessException()
            val controlId = backStackEntry.arguments?.getLong("controlId")
                ?: throw IllegalAccessException()
            StudentAccessDetailControlScreen(courseId, controlId)
        }

        composable("courses/{courseId}/students/{studentId}",
            arguments = listOf(
                navArgument("courseId") { type = NavType.LongType },
                navArgument("studentId") { type = NavType.LongType }
            )) { backStackEntry ->
            val studentId =
                backStackEntry.arguments?.getLong("studentId") ?: throw IllegalAccessError()
            val courseId =
                backStackEntry.arguments?.getLong("courseId") ?: throw IllegalAccessError()
            AsTeacherHistoryForAStudentScreen(courseId, studentId)
        }
    }
}
