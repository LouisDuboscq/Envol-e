package com.lduboscq.envolee.remote

import com.lduboscq.envolee.Settings
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val authenticatedApiInstance: (String) -> AuthenticatedApi = {
    AuthenticatedApi(
        httpClient = HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
/*
            install(HttpTimeout) {
                requestTimeoutMillis = 10000
                connectTimeoutMillis = 10000
                socketTimeoutMillis = 10000
            }
*/

            HttpResponseValidator {
                handleResponseExceptionWithRequest { cause: Throwable, request: HttpRequest ->
                    /*when (cause) {
                        is java.net.SocketTimeoutException,
                        is java.net.ConnectException -> throw NoConnexionException()

                    }

                     */
                    throw NoConnexionException()
                }
            }
        },
        getToken = { Settings().getToken() },
        baseUrl = it
    )
}
