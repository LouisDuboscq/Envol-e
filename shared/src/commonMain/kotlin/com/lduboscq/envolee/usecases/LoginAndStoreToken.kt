package com.lduboscq.envolee.usecases

import com.lduboscq.envolee.Settings
import com.lduboscq.envolee.model.UserRole
import com.lduboscq.envolee.remote.Api

class LoginAndStoreToken(
    private val api: Api,
    private val settings: Settings
) {
    suspend operator fun invoke(email: String, password: String): UserRole {
        val (token, userRole) = api.login(email, password)
        settings.saveUsernameInCache(email)
        settings.saveToken(token)
        return userRole
    }
}
