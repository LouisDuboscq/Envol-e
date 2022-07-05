package com.lduboscq.envolee.android.asteacher.controls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lduboscq.envolee.model.Control
import com.lduboscq.envolee.remote.AuthenticatedApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AsTeacherControlsViewModel(
    private val authenticatedApiInstance: AuthenticatedApi,
    private val courseId: Long
) : ViewModel() {

    data class State(
        val loading: Boolean = false,
        val controls: List<Control> = emptyList()
    )

    sealed class Effect {
        object ControlAdded : Effect()
    }

    private val currentState: State
        get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch { refreshControls() }
    }

    private suspend fun refreshControls() {
        _uiState.value = currentState.copy(loading = true)
        val controls = authenticatedApiInstance.getControls(courseId)
        _uiState.value = currentState.copy(loading = false, controls = controls)
    }

    fun addControl(controlName: String) {
        viewModelScope.launch {
            _uiState.value = currentState.copy(loading = true)
            val created = authenticatedApiInstance.addControl(
                courseId,
                controlName
            )
            if (created) {
                refreshControls()
                _effect.send(Effect.ControlAdded)
            }
        }
    }
}
