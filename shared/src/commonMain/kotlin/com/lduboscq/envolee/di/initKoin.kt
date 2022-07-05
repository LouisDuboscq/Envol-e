package com.lduboscq.envolee.di

import com.lduboscq.envolee.Settings
import com.lduboscq.envolee.platformModule
import com.lduboscq.envolee.remote.Api
import com.lduboscq.envolee.remote.AuthenticatedApi
import com.lduboscq.envolee.remote.NoConnexionException
import com.lduboscq.envolee.usecases.LoginAndStoreToken
import com.lduboscq.envolee.usecases.SignupUser
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(baseUrl: String, appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(commonModule(baseUrl), platformModule())
    }

fun commonModule(baseUrl: String) = module {
    single { createHttpClient() }
    single {
        AuthenticatedApi(
            httpClient = get(),
            getToken = { get<Settings>().getToken() },
            baseUrl = baseUrl,
        )
    }
    single { Api(baseUrl = baseUrl, httpClient = get()) }
    single { SignupUser(api = get()) }
    single { LoginAndStoreToken(api = get(), settings = get()) }
    single { Settings() }
}

private fun createHttpClient() =
    HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
        HttpResponseValidator {
            handleResponseExceptionWithRequest { cause: Throwable, request: HttpRequest ->
                throw NoConnexionException()
            }
        }
    }
