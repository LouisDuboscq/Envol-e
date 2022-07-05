package com.lduboscq.envolee.android.asstudent.detailcontrol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lduboscq.envolee.model.Control
import com.lduboscq.envolee.model.Course
import com.lduboscq.envolee.remote.AuthenticatedApi
import com.lduboscq.envolee.remote.ResourceNotFound
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentAccessDetailControlViewModel(
    private val authenticatedApiInstance: AuthenticatedApi,
    courseId: Long,
    controlId: Long
) : ViewModel() {

    data class State(
        val loading: Boolean = false,
        val remoteAudio: String? = null,
        val course: Course? = null,
        val control: Control? = null
    )

    private val _uiState: MutableStateFlow<State> =
        MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    private val currentState: State
        get() = uiState.value

    init {
        viewModelScope.launch {
            _uiState.value = currentState.copy(loading = true)
            val (userId, idsForCourses) = authenticatedApiInstance.me()
            val studentIdForThisCourse =
                idsForCourses.firstOrNull { it.courseId == courseId }?.studentId
                    ?: throw Exception("student should have an id for the course")

            val remoteAudio: String? = try {
                authenticatedApiInstance.getAudio(
                    courseId = courseId,
                    studentId = studentIdForThisCourse,
                    controlId = controlId
                )
            } catch (e: ResourceNotFound) {
                null
            }

            _uiState.value = currentState.copy(remoteAudio = remoteAudio)

            val course: Course? = authenticatedApiInstance.getCourses()
                .firstOrNull { it.id == courseId }

            _uiState.value = currentState.copy(course = course)

            val control: Control? = authenticatedApiInstance.getControls(courseId)
                .firstOrNull { it.id == controlId }

            _uiState.value = currentState.copy(
                control = control,
                loading = false
            )
        }
    }
}