package com.lduboscq.envolee.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.lduboscq.envolee.Settings
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val settings: Settings by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainLayout(settings) }
    }
}