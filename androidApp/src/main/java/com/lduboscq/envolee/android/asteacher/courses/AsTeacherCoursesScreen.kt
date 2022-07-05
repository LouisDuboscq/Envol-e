package com.lduboscq.envolee.android.asteacher.courses

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
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
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
import com.lduboscq.envolee.model.Course
import com.lduboscq.envolee.presentation.Localizable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun AsTeacherCoursesScreen(navController: NavHostController) {
    val asTeacherCoursesVM: AsTeacherCoursesViewModel = getViewModel()
    val state by asTeacherCoursesVM.uiState.collectAsStateWithLifecycle()

    CoursesScreen(
        navigateToClass = { navController.navigate("courses/${it.id}") },
        navigateBack = { navController.navigateUp() },
        state = state,
        effects = asTeacherCoursesVM.effect,
        addCourse = { asTeacherCoursesVM.addCourse(it) }
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun CoursesScreen(
    navigateToClass: (Course) -> Unit,
    navigateBack: () -> Unit,
    state: AsTeacherCoursesViewModel.State,
    effects: Flow<AsTeacherCoursesViewModel.Effect>,
    addCourse: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    var courseName by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        effects.onEach {
            when (it) {
                AsTeacherCoursesViewModel.Effect.CourseAdded -> {
                    courseName = ""
                    bottomState.hide()
                }
            }
        }.collect()
    }

    BackHandler {
        if (bottomState.isVisible) {
            scope.launch { bottomState.hide() }
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
                        text = Localizable.enterClassName().toString(context),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.headlineLarge
                    )

                    TextField(
                        value = courseName,
                        label = {
                            Text(
                                Localizable.enterClassName().toString(context),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        onValueChange = { courseName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                addCourse(courseName)
                            }
                        )
                    )

                    Button(
                        onClick = { addCourse(courseName) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        enabled = courseName.isNotEmpty()
                    ) {
                        Text(Localizable.addCourse().toString(context))
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = { LargeTopAppBar(title = { Text(Localizable.classes().toString(context)) }) },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text(Localizable.addCourse().toString(context)) },
                    onClick = {
                        scope.launch {
                            bottomState.show()
                        }
                    },
                    icon = { Icon(Icons.Outlined.Add, null) }
                )
            },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            LazyColumn(modifier = Modifier.padding(it)) {
                items(state.courses) {
                    CardTextNavigateNext(it.name, onClick = { navigateToClass(it) })
                }
            }
        }
    }
}
