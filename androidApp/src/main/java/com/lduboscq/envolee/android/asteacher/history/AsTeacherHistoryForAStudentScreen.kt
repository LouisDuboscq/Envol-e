package com.lduboscq.envolee.android.asteacher.history

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Hearing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lduboscq.envolee.android.startPlaying
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun AsTeacherHistoryForAStudentScreen(courseId: Long, studentId: Long) {
    val viewModel: AsTeacherHistoryForAStudentViewModel = getViewModel {
        parametersOf(courseId, studentId)
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val mediaPlayer = createMediaPlayerWithLifecycle()

    Scaffold(
        topBar = { LargeTopAppBar(title = { Text("Historique") }) },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            items(state.controls) { control ->
                Column {
                    Text(text = control.name)
                    if (state.audios.firstOrNull { it.first == control }?.second != null) {
                        IconButton(onClick = { mediaPlayer.startPlaying(state.audios.firstOrNull { it.first == control }?.second!!) }) {
                            Icon(Icons.Outlined.Hearing, null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun createMediaPlayerWithLifecycle(): MediaPlayer {
    val mediaPlayer = MediaPlayer()
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.stop()
        }
    }
    return mediaPlayer
}
