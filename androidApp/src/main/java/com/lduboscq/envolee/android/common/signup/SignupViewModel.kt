package com.lduboscq.envolee.android.common.signup

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lduboscq.envolee.android.BuildConfig
import com.lduboscq.envolee.remote.NoConnexionException
import com.lduboscq.envolee.remote.UserAlreadyExists
import com.lduboscq.envolee.remote.UserUnknown
import com.lduboscq.envolee.usecases.LoginAndStoreToken
import com.lduboscq.envolee.usecases.SignupUser
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignupViewModel(
    private val loginAndStoreToken: LoginAndStoreToken,
    private val signupUser: SignupUser
) : ViewModel() {

    data class State(
        val loading: Boolean = false
    )

    interface Effect {
        object LoginSucceed : Effect
        object UserAlreadyExists : Effect
        object UserUnknown : Effect
        object NoConnexion : Effect
    }

    private val currentState: State
        get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    fun signupAsStudent(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = currentState.copy(loading = true)
            try {
                signupUser(
                    studentUsername = username,
                    password = password,
                    userRole = "student",
                    teacherEmail = "",
                    courseSubject = "",
                    displayedName = "",
                )
                login(username, password)
            } catch (e: UserAlreadyExists) {
                _effect.send(Effect.UserAlreadyExists)
            } catch (e: NoConnexionException) {
                _effect.send(Effect.NoConnexion)
            }
            _uiState.value = currentState.copy(loading = false)
        }
    }

    fun signupAsTeacher(
        email: String,
        password: String,
        courseSubject: String,
        displayedName: String
    ) {
        viewModelScope.launch {
            _uiState.value = currentState.copy(loading = true)
            try {
                signupUser(
                    teacherEmail = email,
                    password = password,
                    courseSubject = courseSubject,
                    displayedName = displayedName,
                    userRole = "teacher",
                    studentUsername = "",
                )

                login(email, password)
            } catch (e: UserAlreadyExists) {
                _effect.send(Effect.UserAlreadyExists)
            } catch (e: NoConnexionException) {
                _effect.send(Effect.NoConnexion)
            }
            _uiState.value = currentState.copy(loading = false)
        }
    }

    private suspend fun login(email: String, password: String) {
        try {
            loginAndStoreToken(
                email,
                password
            )
            _effect.send(Effect.LoginSucceed)
        } catch (e: UserUnknown) {
            _effect.send(Effect.UserUnknown)
        } catch (e: NoConnexionException) {
            _effect.send(Effect.NoConnexion)
        }
    }
}