package com.lduboscq.envolee.android.asteacher.controls

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
import com.lduboscq.envolee.model.Control
import com.lduboscq.envolee.presentation.Localizable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun AsTeacherControlsScreen(navController: NavHostController, courseId: Long) {
    val asTeacherControlsVM: AsTeacherControlsViewModel = getViewModel { parametersOf(courseId) }
    val controlsState by asTeacherControlsVM.uiState.collectAsStateWithLifecycle()

    ControlsScreen(
        navigateToDetailControl = { navController.navigate("courses/$courseId/controls/${it.id}") },
        navigateBack = { navController.navigateUp() },
        state = controlsState,
        effects = asTeacherControlsVM.effect,
        addControl = { controlName -> asTeacherControlsVM.addControl(controlName) },
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun ControlsScreen(
    navigateToDetailControl: (Control) -> Unit,
    navigateBack: () -> Unit,
    state: AsTeacherControlsViewModel.State,
    effects: Flow<AsTeacherControlsViewModel.Effect>,
    addControl: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    var controlName by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        effects.onEach {
            when (it) {
                AsTeacherControlsViewModel.Effect.ControlAdded -> {
                    controlName = ""
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
                        text = Localizable.enterControlName().toString(context),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.headlineLarge
                    )

                    TextField(
                        value = controlName,
                        label = {
                            Text(
                                Localizable.enterControlName().toString(context),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        onValueChange = { controlName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                addControl(controlName)
                            }
                        )
                    )

                    Button(
                        onClick = { addControl(controlName) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        enabled = controlName.isNotEmpty()
                    ) {
                        Text(Localizable.addCourse().toString(context))
                    }
                }
            }
        }
    ) {
        Scaffold(
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text(Localizable.addControl().toString(context)) },
                    onClick = {
                        scope.launch {
                            bottomState.show()
                        }
                    },
                    icon = {
                        Icon(Icons.Outlined.Add, null)
                    })
            },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            LazyColumn(modifier = Modifier.padding(it)) {
                items(state.controls) {
                    CardTextNavigateNext(it.name) {
                        navigateToDetailControl(it)
                    }
                }
            }
        }
    }
}
