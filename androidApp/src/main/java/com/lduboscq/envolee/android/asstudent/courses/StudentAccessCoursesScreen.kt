package com.lduboscq.envolee.android.asstudent.courses

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.lduboscq.envolee.android.CardTextNavigateNext
import com.lduboscq.envolee.presentation.Localizable
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun StudentAccessCoursesScreen(navController: NavHostController) {
    val studentAccessCoursesVM: StudentAccessCoursesViewModel = getViewModel()
    val state by studentAccessCoursesVM.uiState.collectAsStateWithLifecycle()

    StudentAccessCoursesScreen(
        navigateToDetailScreen = { navController.navigate("student-access-courses/$it/controls") },
        navigateBack = { navController.navigateUp() },
        checkingIfCodeExists = { studentAccessCoursesVM.checkIfCodeExist(it) },
        effects = studentAccessCoursesVM.effect,
        registerToCourse = { studentAccessCoursesVM.registerToCourse(it) },
        state = state
    )
}

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun StudentAccessCoursesScreen(
    state: StudentAccessCoursesViewModel.State,
    effects: Flow<StudentAccessCoursesViewModel.Effect>,
    navigateToDetailScreen: (courseId: Long) -> Unit,
    navigateBack: () -> Unit,
    registerToCourse: (String) -> Unit,
    checkingIfCodeExists: (String) -> Unit,
) {
    var code by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var checkCodeExistsJob: Job? = null
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    LaunchedEffect(Unit) {
        effects.onEach {
            when (it) {
                StudentAccessCoursesViewModel.Effect.CourseAdded -> {
                    code = ""
                    bottomState.hide()
                }
            }
        }.collect()
    }

    BackHandler {
        if (bottomState.isVisible) {
            scope.launch {
                bottomState.hide()
            }
        } else {
            navigateBack()
        }
    }

    ModalBottomSheetLayout(
        sheetState = bottomState,
        sheetContent = {
            Surface {
                Column {
                    Text(
                        text = Localizable.enterClassCode().toString(context),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Text(
                        Localizable.teacherMustGenerateCode().toString(context),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.labelSmall
                    )

                    TextField(
                        value = code,
                        label = {
                            Text(
                                Localizable.enterClassCode().toString(context),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        onValueChange = {
                            checkCodeExistsJob?.cancel()
                            checkCodeExistsJob = null

                            code = it

                            checkCodeExistsJob = scope.launch {
                                delay(200)
                                checkingIfCodeExists(it)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                registerToCourse(code)
                            }
                        )
                    )

                    if (state.codeExistResponse != null) {
                        Text(
                            String.format(
                                Localizable.courseFound(
                                    state.codeExistResponse!!.courseName,
                                    state.codeExistResponse!!.teacherName,
                                    state.codeExistResponse!!.studentName
                                ).toString(context)
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    Button(
                        onClick = { registerToCourse(code) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        enabled = state.isButtonRegisterEnabled
                    ) {
                        Text(Localizable.joinCourse().toString(context))
                    }
                }
            }
        }
    ) {
        Scaffold(
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text(Localizable.addCourse().toString(context)) },
                    onClick = {
                        scope.launch {
                            bottomState.show()
                        }
                    },
                    icon = {
                        Icon(Icons.Outlined.Add, null)
                    })
            },
            topBar = {
                LargeTopAppBar(title = {
                    Text(
                        Localizable.courses().toString(context),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                })
            },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            LazyColumn(modifier = Modifier.padding(it)) {
                items(state.courses) {
                    CardTextNavigateNext(it.name, onClick = { navigateToDetailScreen(it.id) })
                }
            }
        }
    }
}
