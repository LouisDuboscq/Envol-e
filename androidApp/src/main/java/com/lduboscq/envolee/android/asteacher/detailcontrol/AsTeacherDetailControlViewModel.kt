package com.lduboscq.envolee.android.asteacher.detailcontrol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lduboscq.envolee.model.Control
import com.lduboscq.envolee.model.Student
import com.lduboscq.envolee.remote.AuthenticatedApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AsTeacherDetailControlViewModel(
    private val authenticatedApiInstance: AuthenticatedApi,
    courseId: Long,
    controlId: Long
) : ViewModel() {

    data class State(
        val loading: Boolean = false,
        val control: Control? = null,
        val students: List<Student> = emptyList()
    )

    private val currentState: State
        get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.value = currentState.copy(loading = true)
        viewModelScope.launch {
            val students = authenticatedApiInstance.getStudents(courseId)
            val control = authenticatedApiInstance.getControls(courseId)
                .firstOrNull { it.id == controlId }
            _uiState.value =
                currentState.copy(students = students, control = control, loading = false)
        }
    }
}
