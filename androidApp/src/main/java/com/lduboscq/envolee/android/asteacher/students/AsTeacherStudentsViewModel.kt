package com.lduboscq.envolee.android.asteacher.students

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lduboscq.envolee.Settings
import com.lduboscq.envolee.model.Student
import com.lduboscq.envolee.remote.AuthenticatedApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AsTeacherStudentsViewModel(
    private val authenticatedApiInstance: AuthenticatedApi,
    private val courseId: Long,
    private val settings: Settings
) : ViewModel() {

    data class State(
        val loading: Boolean = false,
        val students: List<Student> = emptyList(),
        val recipientEmail: String = ""
    )

    sealed class Effect {
        object StudentOrStudentListAdded : Effect()
        data class CodesGenerated(val codes: String, val courseId: Long) : Effect()
    }

    private val currentState: State
        get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        _uiState.value = currentState.copy(recipientEmail = settings.getUsernameInCache() ?: "")
        viewModelScope.launch { refreshStudents() }
    }

    private suspend fun refreshStudents() {
        _uiState.value = currentState.copy(loading = true)
        val students = authenticatedApiInstance.getStudents(courseId)
        _uiState.value = currentState.copy(loading = false, students = students)
    }

    fun addStudentOrList(names: String) {
        viewModelScope.launch {
            val list = names.split("\n")
            val created =
                authenticatedApiInstance.addStudentList(courseId, list)
            if (created) {
                refreshStudents()
                _effect.send(Effect.StudentOrStudentListAdded)
            }
        }
    }

    fun generateStudentsCodes() {
        viewModelScope.launch {
            val listCFS = authenticatedApiInstance.createInvitationCodesForCourse(courseId)
            val codes = "\n" + listCFS.joinToString("\n\n") { cfs ->
                cfs.studentName + "    " + cfs.invitationCode
            }

            _effect.send(Effect.CodesGenerated(codes, courseId))
        }
    }
}
