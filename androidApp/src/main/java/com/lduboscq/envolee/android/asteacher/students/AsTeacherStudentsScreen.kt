package com.lduboscq.envolee.android.asteacher.students

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.lduboscq.envolee.android.CardTextNavigateNext
import com.lduboscq.envolee.android.generateNoteOnSD
import com.lduboscq.envolee.android.shareFileEmail
import com.lduboscq.envolee.model.Student
import com.lduboscq.envolee.presentation.Localizable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun AsTeacherStudentsScreen(navController: NavHostController, courseId: Long) {
    val asTeacherStudentsViewModel: AsTeacherStudentsViewModel =
        getViewModel { parametersOf(courseId) }
    val studentsState by asTeacherStudentsViewModel.uiState.collectAsStateWithLifecycle()

    StudentsScreen(
        clickOnStudent = { student -> navController.navigate("courses/$courseId/students/${student.id}") },
        navigateBack = { navController.navigateUp() },
        effects = asTeacherStudentsViewModel.effect,
        state = studentsState,
        addStudentOrList = { studentNameOrNameList ->
            asTeacherStudentsViewModel.addStudentOrList(studentNameOrNameList)
        },
        generateStudentsCodes = { asTeacherStudentsViewModel.generateStudentsCodes() }
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun StudentsScreen(
    state: AsTeacherStudentsViewModel.State,
    clickOnStudent: (Student) -> Unit,
    navigateBack: () -> Unit,
    effects: Flow<AsTeacherStudentsViewModel.Effect>,
    generateStudentsCodes: () -> Unit,
    addStudentOrList: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    var studentNameOrNameList by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        effects.onEach {
            when (it) {
                AsTeacherStudentsViewModel.Effect.StudentOrStudentListAdded -> {
                    studentNameOrNameList = ""
                    bottomState.hide()
                }
                is AsTeacherStudentsViewModel.Effect.CodesGenerated -> {
                    generateNoteOnSD(
                        context,
                        "cours_${it.courseId}.txt",
                        it.codes
                    )
                    shareFileEmail(
                        context,
                        "cours_${it.courseId}.txt",
                        state.recipientEmail
                    )
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
                        text = Localizable.enterStudentNameOrList().toString(context),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.headlineLarge
                    )

                    TextField(
                        value = studentNameOrNameList,
                        onValueChange = { studentNameOrNameList = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .heightIn(20.dp, 150.dp),
                        keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Companion.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                addStudentOrList(studentNameOrNameList)
                            }
                        )
                    )

                    Button(
                        onClick = { addStudentOrList(studentNameOrNameList) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        enabled = studentNameOrNameList.isNotEmpty()
                    ) {
                        Text(Localizable.addStudents().toString(context))
                    }
                }
            }
        }
    ) {
        Scaffold(
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text(Localizable.addStudents().toString(context)) },
                    onClick = { scope.launch { bottomState.show() } },
                    icon = { Icon(Icons.Outlined.Add, null) }
                )
            },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column {
                if (state.students.isNotEmpty()) {
                    Button(
                        onClick = {
                            scope.launch {
                                generateStudentsCodes()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    ) {
                        Text("Générer les codes élèves")
                    }
                }

                LazyColumn(modifier = Modifier.padding(it)) {
                    items(state.students) {
                        CardTextNavigateNext(it.name) {
                            clickOnStudent(it)
                        }
                    }
                    item {
                        Spacer(Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}
