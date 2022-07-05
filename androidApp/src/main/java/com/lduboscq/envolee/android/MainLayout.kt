package com.lduboscq.envolee.android

import androidx.compose.runtime.Composable
import com.lduboscq.envolee.Settings

@Composable
fun MainLayout(settings: Settings) {
    val darkTheme = true /*themeSettings.appSettings.observeAppTheme()
                .collectAsStateWithLifecycle(AppTheme.LightSelectedByUser)
                .value
                .isDarkTheme(sys)*/

    // TestTheme(darkTheme)

    EnvoleeTheme(darkTheme) { MainGraph(settings) }
}
