package com.lduboscq.envolee.android.asteacher.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lduboscq.envolee.android.BuildConfig
import com.lduboscq.envolee.model.Control
import com.lduboscq.envolee.remote.AuthenticatedApi
import com.lduboscq.envolee.remote.ResourceNotFound
import com.lduboscq.envolee.remote.authenticatedApiInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AsTeacherHistoryForAStudentViewModel(
    private val courseId: Long,
    private val studentId: Long,
    private val authenticatedApi: AuthenticatedApi
) : ViewModel() {

    data class State(
        val controls: List<Control> = emptyList(),
        val audios: List<Pair<Control, String?>> = emptyList()
    )

    private val currentState: State
        get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val controls = authenticatedApi.getControls(courseId)
            val audios = controls.map {
                val b64 = try {
                    authenticatedApiInstance(BuildConfig.base_url)
                        .getAudio(
                            courseId = courseId,
                            studentId = studentId,
                            controlId = it.id
                        )
                } catch (e: ResourceNotFound) {
                    null
                }
                Pair(it, b64)
            }

            _uiState.value = currentState.copy(controls = controls, audios = audios)
        }
    }
}
