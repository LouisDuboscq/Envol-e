package com.lduboscq.envolee.android.common.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lduboscq.envolee.model.UserRole
import com.lduboscq.envolee.remote.NoConnexionException
import com.lduboscq.envolee.remote.UserUnknown
import com.lduboscq.envolee.usecases.LoginAndStoreToken
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginAndStoreToken: LoginAndStoreToken
) : ViewModel() {

    data class State(
        val loading: Boolean = false,
        val existingEmail: String? = null
    )

    sealed interface Effect {
        data class LoginSucceed(val role: UserRole) : Effect
        object UserUnknown : Effect
        object NoConnexion : Effect
    }

    private val currentState: State
        get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    fun validate(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = currentState.copy(loading = true)
            try {
                val userRole = loginAndStoreToken(email, password)
                _effect.send(Effect.LoginSucceed(userRole))
            } catch (e: UserUnknown) {
                _effect.send(Effect.UserUnknown)
            } catch (e: NoConnexionException) {
                _effect.send(Effect.NoConnexion)
            }
            _uiState.value = currentState.copy(loading = false)
        }
    }
}