package com.lduboscq.envolee.android.asteacher.detailcontrol

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.lduboscq.envolee.android.CardTextNavigateNext
import com.lduboscq.envolee.model.Student
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun AsTeacherDetailControlScreen(
    navController: NavHostController,
    controlId: Long,
    courseId: Long
) {
    val asTeacherDetailControlVM: AsTeacherDetailControlViewModel = getViewModel {
        parametersOf(controlId, courseId)
    }
    val state by asTeacherDetailControlVM.uiState.collectAsStateWithLifecycle()

    DetailControlScreen(
        state = state,
        clickOnStudent = {
            navController.navigate("courses/$courseId/control/$controlId/for-student/${it.id}")
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailControlScreen(
    state: AsTeacherDetailControlViewModel.State,
    clickOnStudent: (Student) -> Unit
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(title = {
                state.control?.name?.let {
                    Text(it, modifier = Modifier.padding(start = 16.dp))
                }
            })
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        if (state.loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        LazyColumn(modifier = Modifier.padding(it)) {
            items(state.students) {
                CardTextNavigateNext(it.name) {
                    clickOnStudent(it)
                }
            }
        }
    }
}
