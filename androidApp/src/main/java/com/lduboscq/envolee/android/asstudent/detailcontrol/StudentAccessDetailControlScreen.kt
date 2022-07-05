package com.lduboscq.envolee.android.asstudent.detailcontrol

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lduboscq.envolee.android.startPlaying
import com.lduboscq.envolee.android.subtitleColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun StudentAccessDetailControlScreen(courseId: Long, controlId: Long) {
    val studentDetailControlVM: StudentAccessDetailControlViewModel =
        getViewModel { parametersOf(courseId, controlId) }
    val state by studentDetailControlVM.uiState.collectAsStateWithLifecycle()
    StudentAccessDetailControlScreen(state)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentAccessDetailControlScreen(
    state: StudentAccessDetailControlViewModel.State
) {
    var playing by rememberSaveable { mutableStateOf(false) }
    var timeViewText by rememberSaveable { mutableStateOf("") }
    var seekBarProgress by rememberSaveable { mutableStateOf(0) }
    val mediaPlayer = MediaPlayer()
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.stop()
        }
    }

    Scaffold(
        topBar = {
            Column {
                MediumTopAppBar(title = { state.course?.name?.let { Text(text = it) } })
                SmallTopAppBar(title = { state.control?.name?.let { Text(text = it) } })
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) {

        if (state.loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (state.remoteAudio != null && state.remoteAudio.isNotEmpty()) {
            Column(modifier = Modifier.padding(it)) {
                LargeFloatingActionButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    onClick = {
                        if (playing) {
                            mediaPlayer.stop()
                            playing = false
                        } else {
                            scope.launch {
                                mediaPlayer.startPlaying(state.remoteAudio)
                                playing = true
                                val currentPositionInSeconds = mediaPlayer.currentPosition / 1000
                                val durationInSeconds = mediaPlayer.duration / 1000

                                while (mediaPlayer.isPlaying) {
                                    seekBarProgress = mediaPlayer.currentPosition
                                    timeViewText = "${mediaPlayer.currentPosition.milliseconds}"
                                    delay(100)
                                    mediaPlayer.release()
                                }
                                playing = false
                            }
                        }
                    }) {
                    val icon = if (playing) {
                        Icons.Filled.Stop
                    } else {
                        Icons.Outlined.PlayArrow
                    }
                    Icon(icon, contentDescription = null)
                }

                Text(
                    "currentPosition $seekBarProgress",
                    style = MaterialTheme.typography.bodyLarge
                        .copy(color = subtitleColor)
                )
                Text(
                    "timeViewText $timeViewText",
                    style = MaterialTheme.typography.bodyLarge
                        .copy(color = subtitleColor)
                )
                if (mediaPlayer.currentPosition != 0 && mediaPlayer.isPlaying) {
                    LinearProgressIndicator(progress = (seekBarProgress.milliseconds / mediaPlayer.duration.milliseconds).toFloat())
                }
            }
        } else {
            Text(
                "Vous n'avez pas de retour vocal",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
