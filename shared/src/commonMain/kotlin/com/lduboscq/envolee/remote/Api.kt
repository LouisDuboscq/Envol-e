package com.lduboscq.envolee.remote

import com.lduboscq.envolee.mobileshared.LoginPostDto
import com.lduboscq.envolee.mobileshared.LoginResponse
import com.lduboscq.envolee.mobileshared.SignupDto
import com.lduboscq.envolee.model.UserRole
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class Api(
    private val httpClient: HttpClient,
    private val baseUrl: String
) {

    suspend fun login(email: String, password: String): Pair<String, UserRole> {
        val response = httpClient.post("$baseUrl/login") {
            setBody(LoginPostDto(email, password))
            contentType(ContentType.Application.Json)
        }
        val lr = try {
            response.body<LoginResponse>()
        } catch (e: NoTransformationFoundException) {
            if (response.status.value == 401) {
                throw UserUnknown()
            }
            throw e
        }
        val token = lr.token
        val userRole: String = lr.userRole
        return Pair(
            token, when (userRole) {
                "student" -> UserRole.Student
                "teacher" -> UserRole.Teacher
                else -> throw IllegalStateException()
            }
        )
    }


    // todo see https://ktor.io/docs/request.html#upload_file
    suspend fun uploadFile(bytes: ByteArray) {
        val response: HttpResponse = httpClient.submitFormWithBinaryData(
            url = "$baseUrl/upload",
            formData = formData {
                append("description", "Ktor logo")
                append("image", bytes, Headers.build {
                    append(HttpHeaders.ContentType, "image/png")
                    append(HttpHeaders.ContentDisposition, "filename=ktor_logo.png")
                })
            }
        ) {
            onUpload { bytesSentTotal, contentLength ->
                println("Sent $bytesSentTotal bytes from $contentLength")
            }
        }
    }

    suspend fun signup(
        teacherEmail: String,
        displayedName: String,
        courseSubject: String,
        password: String,
        studentUsername: String,
        userRole: String
    ) {
        val response = httpClient.post("$baseUrl/signup") {
            setBody(
                SignupDto(
                    email = teacherEmail,
                    password = password,
                    username = studentUsername,
                    displayedName = displayedName,
                    courseSubject = courseSubject,
                    role = userRole
                )
            )
            contentType(ContentType.Application.Json)
        }
        if (response.status == HttpStatusCode.Conflict) {
            throw UserAlreadyExists()
        }
    }
}
