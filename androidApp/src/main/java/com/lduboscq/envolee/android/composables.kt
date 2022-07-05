package com.lduboscq.envolee.android

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NavigateNext
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.lduboscq.envolee.android.formatDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopRightButton(text: String, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.End),
        onClick = onClick
    ) {
        Text(
            text,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardTextNavigateNext(
    text: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 4.dp),
        onClick = { onClick() }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text,
                modifier = Modifier,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Image(
                imageVector = Icons.Outlined.NavigateNext,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
            )
        }
    }
}

data class MusicSliderState(
    val sliderValue: Float = 0f,
    val timePassed: Long = 0L,
    val timeLeft: Long = 0L,
    val onValueChange: (Float) -> Unit = {}
) {
    val timePassedFormatted
        get() = timePassed.formatDuration()

    val timeLeftFormatted
        get() = timeLeft.formatDuration()
}

@Composable
fun MusicSlider(
    sliderColor: Color,
    state: MusicSliderState
) {
    Slider(
        value = state.sliderValue,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = SliderDefaults.colors(
            activeTrackColor = sliderColor.copy(alpha = 0.7f),
            inactiveTrackColor = sliderColor.copy(alpha = 0.4f),
            thumbColor = sliderColor
        ),
        onValueChange = state.onValueChange,
    )
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = state.timePassedFormatted,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary,
        )

        Text(
            text = state.timeLeftFormatted,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary,
        )
    }
}
