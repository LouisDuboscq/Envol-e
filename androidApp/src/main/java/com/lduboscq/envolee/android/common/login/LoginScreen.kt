package com.lduboscq.envolee.android.common.login

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.lduboscq.envolee.android.TopRightButton
import com.lduboscq.envolee.android.subtitleColor
import com.lduboscq.envolee.model.UserRole
import com.lduboscq.envolee.presentation.Localizable
import com.lduboscq.envolee.presentation.Localizable.getLoginString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    val viewModel: LoginViewModel = getViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LoginScreen(
        state = state,
        effects = viewModel.effect,
        doSignupInstead = { navController.navigate("choose-student-or-teacher") },
        validate = { email, password -> viewModel.validate(email, password) },
        navController = navController
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    state: LoginViewModel.State,
    effects: Flow<LoginViewModel.Effect>,
    doSignupInstead: () -> Unit,
    validate: (String, String) -> Unit,
    navController: NavHostController
) {
    val context = LocalContext.current
    var email: String by rememberSaveable { mutableStateOf(state.existingEmail ?: "") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val (passwordFocusRequester) = FocusRequester.createRefs()

    LaunchedEffect(Unit) {
        effects.onEach {
            when (it) {
                is LoginViewModel.Effect.LoginSucceed -> {
                    when (it.role) {
                        UserRole.Teacher -> {
                            navController.navigate("courses")
                        }
                        UserRole.Student -> {
                            navController.navigate("student-access-courses")
                        }
                        else -> throw IllegalAccessException()
                    }
                }
                LoginViewModel.Effect.NoConnexion -> {
                    Toast.makeText(context, "no connexion", Toast.LENGTH_LONG).show()
                }
                LoginViewModel.Effect.UserUnknown -> {
                    Toast.makeText(
                        context,
                        Localizable.unknownUser().toString(context),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.collect()
    }

    Column(modifier = Modifier) {
        TopRightButton(
            Localizable.createAnAccount().toString(context),
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp),
            doSignupInstead
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            if (state.loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(modifier = Modifier.align(Alignment.Center)) {
                Text(
                    Localizable.emailOrUsername().toString(context),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(Modifier.height(4.dp))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "monadresse@gmail.com",
                            style = LocalTextStyle.current.copy(color = subtitleColor)
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Companion.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { passwordFocusRequester.requestFocus() }
                    )
                )
                Spacer(Modifier.height(32.dp))
                Text(
                    "Password",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(Modifier.height(4.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(passwordFocusRequester),
                    singleLine = true,
                    placeholder = { Text(Localizable.atLeastSixCaracters().toString(context)) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Companion.Done
                    ),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        val description = if (passwordVisible) "Hide password" else "Show password"

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, description)
                        }
                    },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            validate(email, password)
                        }
                    )
                )
            }

            Button(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                onClick = { validate(email, password) },
                shape = RoundedCornerShape(20.dp),
                enabled = !state.loading
            ) {
                Text(
                    getLoginString().toString(context),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}
