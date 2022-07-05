package com.lduboscq.envolee.android.common.signup

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.lduboscq.envolee.android.subtitleColor
import com.lduboscq.envolee.presentation.Localizable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun SignupAsStudentScreen(navController: NavHostController) {
    val viewModel: SignupViewModel = getViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    SignupAsStudentScreen(
        onLoginSucceed = { navController.navigate("student-access-courses") },
        effect = viewModel.effect,
        signup = { username: String, password: String -> viewModel.signupAsStudent(username, password) },
        state = state
    )
}

@Composable
fun SignupAsStudentScreen(
    onLoginSucceed: () -> Unit,
    effect: Flow<SignupViewModel.Effect>,
    signup: (String, String) -> Unit,
    state: SignupViewModel.State
) {
    val context = LocalContext.current
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var username by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        effect.onEach {
            when (it) {
                is SignupViewModel.Effect.NoConnexion -> {
                    Toast.makeText(
                        context,
                        "no connexion",
                        Toast.LENGTH_LONG
                    ).show()
                }
                is SignupViewModel.Effect.LoginSucceed -> {
                    onLoginSucceed()
                }

                is SignupViewModel.Effect.UserAlreadyExists -> {
                    Toast.makeText(
                        context,
                        Localizable.anUserAlreadyExistsWithThisUsernameOrEmail().toString(context),
                        Toast.LENGTH_LONG
                    ).show()
                }
                is SignupViewModel.Effect.UserUnknown -> {
                    Toast.makeText(
                        context,
                        Localizable.unknownUser().toString(context),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.collect()
    }

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
                "Email or username",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(Modifier.height(4.dp))
            TextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Username") })
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
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    Text(
                        "Au moins 6 caractères",
                        style = LocalTextStyle.current.copy(color = subtitleColor)
                    )
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible) {
                        Icons.Filled.Visibility
                    } else {
                        Icons.Filled.VisibilityOff
                    }
                    val description = if (passwordVisible) "Hide password" else "Show password"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                }
            )
        }

        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            onClick = { signup(username, password) },
            enabled = !state.loading
        ) {
            Text("Créer un compte", modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}