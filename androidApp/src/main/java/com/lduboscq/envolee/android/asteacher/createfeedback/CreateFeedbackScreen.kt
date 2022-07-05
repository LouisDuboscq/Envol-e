package com.lduboscq.envolee.android.asteacher.createfeedback

import android.Manifest
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.Animatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.Hearing
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.lduboscq.envolee.android.PrepareRecordException
import com.lduboscq.envolee.android.startPlaying
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun CreateFeedbackScreen(
    courseId: Long,
    controlId: Long,
    studentId: Long
) {
    val createFeedbackViewModel: CreateFeedbackViewModel = getViewModel {
        parametersOf(
            courseId,
            controlId,
            studentId
        )
    }
    val state by createFeedbackViewModel.uiState.collectAsStateWithLifecycle()

    CreateFeedbackScreen(
        state = state,
        sendToServer = { createFeedbackViewModel.sendToServer() },
        startPlaying = { createFeedbackViewModel.startPlaying() },
        startRecording = { createFeedbackViewModel.startRecording() },
        stopPlaying = { createFeedbackViewModel.stopPlaying() },
        stopRecording = { createFeedbackViewModel.stopRecording() }
    )
}

@OptIn(
    ExperimentalPermissionsApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun CreateFeedbackScreen(
    state: CreateFeedbackViewModel.State,
    sendToServer: () -> Unit,
    startPlaying: () -> Unit,
    startRecording: () -> Unit,
    stopPlaying: () -> Unit,
    stopRecording: () -> Unit
) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose {
            stopPlaying()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            stopRecording()
        }
    }

    Scaffold(
        topBar = { LargeTopAppBar(title = { Text("Feedback") }) },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(it), verticalArrangement = Arrangement.SpaceBetween) {
            Text("${state.student?.name}", style = MaterialTheme.typography.headlineMedium)
            Text("${state.control?.name}", style = MaterialTheme.typography.headlineMedium)

            if (state.remoteAudio != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Vous avez déjà commencé à enregistrer un feedback",
                        modifier = Modifier.width(150.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    IconButton(onClick = { MediaPlayer().startPlaying(state.remoteAudio) }) {
                        Icon(Icons.Outlined.Hearing, null)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.clickable { startPlaying() },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ecouter l'enregistrement",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Icon(Icons.Outlined.PlayArrow, null)
                }
                Column(
                    modifier = Modifier.clickable { stopPlaying() },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Arrêter d'écouter", style = MaterialTheme.typography.labelSmall)
                    Icon(Icons.Outlined.Stop, null)
                }
            }

            val primaryColor = MaterialTheme.colorScheme.primary
            val controlsColor = remember { Animatable(primaryColor) }

            /*MusicSlider(
                sliderColor = controlsColor,
                state = viewModel.uiState.value.musicSliderState
            )*/


            LargeFloatingActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally),
                onClick = {
                    if (state.recording) {
                        stopRecording()
                    } else {
                        if (cameraPermissionState.status != PermissionStatus.Granted) {
                            cameraPermissionState.launchPermissionRequest()
                        } else {
                            try {
                                startRecording()
                            } catch (e: PrepareRecordException) {
                                Toast.makeText(
                                    context,
                                    "Error while starting recording",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }) {
                val icon = if (state.recording) {
                    Icons.Filled.Stop
                } else {
                    Icons.Outlined.Mic
                }
                Icon(icon, contentDescription = null)
            }

            if (state.recording) {
                Text(text = "RECORDING", style = MaterialTheme.typography.headlineMedium)
            }

            TextButton(onClick = { scope.launch { sendToServer() } }) {
                Text("Send audio to server")
            }
        }
    }
}


