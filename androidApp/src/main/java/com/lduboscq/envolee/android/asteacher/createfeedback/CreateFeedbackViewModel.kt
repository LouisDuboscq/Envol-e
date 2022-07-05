package com.lduboscq.envolee.android.asteacher.createfeedback

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lduboscq.envolee.android.PrepareRecordException
import com.lduboscq.envolee.android.convertImageFileToBase64
import com.lduboscq.envolee.model.Control
import com.lduboscq.envolee.model.Student
import com.lduboscq.envolee.remote.AuthenticatedApi
import com.lduboscq.envolee.remote.ResourceNotFound
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class CreateFeedbackViewModel(
    private val authenticatedApiInstance: AuthenticatedApi,
    private val context: Context,
    private val courseId: Long,
    private val studentId: Long,
    private val controlId: Long
) : ViewModel() {

    data class State(
        val loading: Boolean = false,
        val student: Student? = null,
        val control: Control? = null,
        val remoteAudio: String? = null,
        val recording: Boolean = false
    )

    private val currentState: State
        get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    private var fileName: String? = null
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    init {
        this.fileName =
            "${context.externalCacheDir?.absolutePath}/courses/$courseId/controls/$controlId/students/$studentId.mp4"

        viewModelScope.launch {
            _uiState.value = currentState.copy(loading = true)

            val control = authenticatedApiInstance.getControls(courseId)
                .firstOrNull { it.id == controlId }

            val student = authenticatedApiInstance.getStudents(courseId)
                .firstOrNull { it.id == studentId }

            val remoteAudio = try {
                authenticatedApiInstance.getAudio(courseId, studentId, controlId)
            } catch (e: ResourceNotFound) {
                null
            }
            _uiState.value =
                currentState.copy(control = control, student = student, remoteAudio = remoteAudio)
        }
    }

    fun sendToServer() {
        viewModelScope.launch {
            authenticatedApiInstance.postAudio(
                courseId = courseId,
                studentId = studentId,
                controlId = controlId,
                base64 = convertImageFileToBase64(File(fileName))
            )
        }
    }

    fun startRecording() {
        val file = File(fileName)
        if (file.parentFile?.exists() != true) {
            file.parentFile?.mkdirs()
        }
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            //setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            // setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            // setAudioEncodingBitRate(128000);
            // setAudioSamplingRate(96000);

            val bitDepth = 16
            val sampleRate = 44100
            val bitRate = sampleRate * bitDepth

            setAudioEncodingBitRate(bitRate)
            setAudioSamplingRate(sampleRate)

            try {
                prepare()
            } catch (e: IOException) {
                throw PrepareRecordException()
            }

            start()
            _uiState.value = currentState.copy(recording = true)
        }
    }

    fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        _uiState.value = currentState.copy(recording = false)
    }


    fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
            }
        }
    }

    fun stopPlaying() {
        player?.release()
        player = null
    }
}