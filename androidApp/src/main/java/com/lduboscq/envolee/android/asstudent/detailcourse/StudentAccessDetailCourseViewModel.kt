package com.lduboscq.envolee.android.asstudent.detailcourse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lduboscq.envolee.model.Control
import com.lduboscq.envolee.remote.AuthenticatedApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentAccessDetailCourseViewModel(
    private val authenticatedApiInstance: AuthenticatedApi,
    courseId: Long
) : ViewModel() {

    data class State(
        val controls: List<Control> = emptyList(),
        val loading: Boolean = false
    )

    private val currentState: State
        get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = currentState.copy(loading = true)
            val controls = authenticatedApiInstance.getControls(courseId)
            _uiState.value = currentState.copy(loading = false, controls = controls)
        }
    }
}
