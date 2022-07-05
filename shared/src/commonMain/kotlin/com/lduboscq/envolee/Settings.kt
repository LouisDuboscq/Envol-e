package com.lduboscq.envolee

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class Settings constructor(private val stgs: Settings = Settings()) {

    fun getToken(): String? = stgs["token"]

    fun saveToken(token: String) {
        stgs["token"] = token
    }

    private val themeFlow: MutableStateFlow<String> = MutableStateFlow("dark") // default is dark

    fun observeAppTheme(): Flow<String> = themeFlow
    fun getAppTheme(): String? = stgs["app-theme"]

    fun saveAppTheme(theme: String) {
        stgs["app-theme"] = theme
        themeFlow.value = theme
    }

    fun getUsernameInCache(): String? = stgs["username"]

    fun saveUsernameInCache(username: String) {
        stgs["username"] = username
    }
}

class ThemeSettings {
    private val stgs = com.lduboscq.envolee.Settings()

    val appSettings = object : AppSettings {
        override fun getAppTheme(): AppTheme {
            return stgs.getAppTheme().run {
                when {
                    this == null -> AppTheme.LetSystemChoose
                    this == "light" -> AppTheme.LightSelectedByUser
                    this == "dark" -> AppTheme.DarkSelectedByUser
                    this == "system" -> AppTheme.LetSystemChoose
                    else -> throw Exception()
                }
            }
        }

        override fun observeAppTheme(): Flow<AppTheme> {
            return stgs.observeAppTheme().map {
                when (it) {
                    "light" -> AppTheme.LightSelectedByUser
                    "dark" -> AppTheme.DarkSelectedByUser
                    "system" -> AppTheme.LetSystemChoose
                    else -> throw Exception("observeAppTheme : $it")
                }
            }
        }

        override fun saveTheme(theme: AppTheme) {
            val themeStr = when (theme) {
                AppTheme.DarkSelectedByUser -> "dark"
                AppTheme.LightSelectedByUser -> "light"
                AppTheme.LetSystemChoose -> "system"
            }
            stgs.saveAppTheme(themeStr)
        }
    }
}


interface AppSettings {
    fun getAppTheme(): AppTheme
    fun observeAppTheme(): Flow<AppTheme>
    fun saveTheme(letSystemChoose: AppTheme)
}

sealed class AppTheme {
    object LightSelectedByUser : AppTheme()
    object DarkSelectedByUser : AppTheme()
    object LetSystemChoose : AppTheme()

    fun isDarkTheme(systemDarkMode: Boolean): Boolean {
        return when (this) {
            LightSelectedByUser -> false
            DarkSelectedByUser -> true
            LetSystemChoose -> systemDarkMode
        }
    }
}