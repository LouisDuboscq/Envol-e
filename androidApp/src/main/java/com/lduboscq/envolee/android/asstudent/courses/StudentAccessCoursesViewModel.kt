package com.lduboscq.envolee.android.asstudent.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lduboscq.envolee.mobileshared.RegisterCourseResponse
import com.lduboscq.envolee.model.Course
import com.lduboscq.envolee.remote.AuthenticatedApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class StudentAccessCoursesViewModel(
    private val authenticatedApiInstance: AuthenticatedApi
) : ViewModel() {

    data class State(
        val loading: Boolean = false,
        val courses: List<Course> = emptyList(),
        val isButtonRegisterEnabled: Boolean = false,
        val codeExistResponse: RegisterCourseResponse? = null
    )

    sealed class Effect {
        object CourseAdded : Effect()
    }

    private val _uiState: MutableStateFlow<State> =
        MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    private val currentState: State
        get() = uiState.value

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            refreshCourses()
        }
    }

    private suspend fun refreshCourses() {
        _uiState.value = currentState.copy(loading = true)
        val courses = authenticatedApiInstance.getCourses()
        _uiState.value = currentState.copy(loading = false, courses = courses)
    }

      fun registerToCourse(code: String) {
          viewModelScope.launch {
              authenticatedApiInstance.registerUserToCourseViaCode(code)
              refreshCourses()
              _effect.send(Effect.CourseAdded)
              _uiState.value = currentState.copy(
                  codeExistResponse = null,
                  isButtonRegisterEnabled = false
              )
          }
    }

      fun checkIfCodeExist(code: String) {
          viewModelScope.launch {
              val response = authenticatedApiInstance.isCodeExists(code)
              _uiState.value = currentState.copy(
                  codeExistResponse = response,
                  isButtonRegisterEnabled = response != null,
              )
          }
    }
}
