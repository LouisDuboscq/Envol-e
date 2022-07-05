package com.lduboscq.envolee.android.asteacher.detailclass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lduboscq.envolee.model.Course
import com.lduboscq.envolee.remote.AuthenticatedApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AsTeacherDetailClassViewModel(
    private val authenticatedApiInstance: AuthenticatedApi,
    courseId: Long
) : ViewModel() {

    data class State(
        val loading: Boolean = false,
        val course: Course? = null
    )

    private val currentState: State
        get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = currentState.copy(loading = true)
            val course = authenticatedApiInstance.getCourses()
                .firstOrNull { it.id == courseId }
            _uiState.value = currentState.copy(loading = false, course = course)
        }
    }
}
