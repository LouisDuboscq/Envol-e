package com.lduboscq.envolee.android.asstudent.detailcourse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.lduboscq.envolee.android.CardTextNavigateNext
import com.lduboscq.envolee.presentation.Localizable
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun StudentAccessDetailCourseScreen(navController: NavHostController, courseId: Long) {
    val studentAccessDetailCourseVM: StudentAccessDetailCourseViewModel = getViewModel(
        parameters = { parametersOf(courseId) }
    )

    val state by studentAccessDetailCourseVM.uiState.collectAsStateWithLifecycle()

    StudentAccessDetailCourseScreen(state) {
        navController.navigate("student-access-courses/$courseId/controls/$it")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentAccessDetailCourseScreen(
    state: StudentAccessDetailCourseViewModel.State,
    navigateToDetailControl: (Long) -> Unit
) {

    val context = LocalContext.current

    Scaffold(
        topBar = {
            LargeTopAppBar(title = {
                Text(Localizable.controls().toString(context))
            })
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        LazyColumn(
            modifier = Modifier.padding(it),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.controls) {
                CardTextNavigateNext(it.name, onClick = { navigateToDetailControl(it.id) })
            }
        }
    }
}
