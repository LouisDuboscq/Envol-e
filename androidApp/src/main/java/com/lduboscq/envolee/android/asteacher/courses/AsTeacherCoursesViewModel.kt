package com.lduboscq.envolee.android.asteacher.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lduboscq.envolee.model.Course
import com.lduboscq.envolee.remote.AuthenticatedApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AsTeacherCoursesViewModel(
    private val authenticatedApiInstance: AuthenticatedApi
) : ViewModel() {

    data class State(
        val loading: Boolean = false,
        val courses: List<Course> = emptyList()
    )

    sealed class Effect {
        object CourseAdded : Effect()
    }

    private val currentState: State
        get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    private suspend fun refreshCourses() {
        _uiState.value = currentState.copy(loading = true)
        val courses = authenticatedApiInstance.getCourses()
        _uiState.value = currentState.copy(loading = false, courses = courses)
    }

    fun addCourse(name: String) {
        viewModelScope.launch {
            val created = authenticatedApiInstance.addCourse(name)
            if (created) {
                refreshCourses()
                _effect.send(Effect.CourseAdded)
            }
        }
    }
}
