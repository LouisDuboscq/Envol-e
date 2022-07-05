package com.lduboscq.envolee.android.asteacher.detailclass

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.lduboscq.envolee.android.asteacher.controls.AsTeacherControlsScreen
import com.lduboscq.envolee.android.asteacher.students.AsTeacherStudentsScreen
import com.lduboscq.envolee.presentation.Localizable
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun AsTeacherDetailClassScreen(navController: NavHostController, courseId: Long) {
    val asTeacherDetailClassViewModel: AsTeacherDetailClassViewModel =
        getViewModel { parametersOf(courseId) }
    val state by asTeacherDetailClassViewModel.uiState.collectAsStateWithLifecycle()

    DetailClassScreen(
        state = state,
        courseId = courseId,
        navController = navController
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailClassScreen(
    state: AsTeacherDetailClassViewModel.State,
    navController: NavHostController,
    courseId: Long
) {
    var currentScreen by rememberSaveable { mutableStateOf(0) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Column {
                SmallTopAppBar(title = {
                    state.course?.name?.let { Text(it) }
                })
                TabRow(
                    selectedTabIndex = currentScreen,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Tab(currentScreen == 0, { currentScreen = 0 }) {
                        Column(
                            Modifier
                                .padding(10.dp)
                                .height(50.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {

                            Icon(
                                Icons.Outlined.Book,
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                            )
                            Text(
                                text = Localizable.controls().toString(context),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }

                    Tab(currentScreen == 1, { currentScreen = 1 }) {
                        Column(
                            Modifier
                                .padding(10.dp)
                                .height(50.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                Icons.Outlined.School,
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                            )
                            Text(
                                text = Localizable.students().toString(context),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Box(modifier = Modifier.padding(it)) {
            if (state.loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Column {
                Spacer(Modifier.height(32.dp))
                if (currentScreen == 0) {
                   AsTeacherControlsScreen(navController, courseId)
                } else if (currentScreen == 1) {
                    AsTeacherStudentsScreen(navController, courseId)
                }
            }
        }
    }
}
