package com.lduboscq.envolee.remote

import com.lduboscq.envolee.mobileshared.AudioPostDto
import com.lduboscq.envolee.mobileshared.CodeForStudent
import com.lduboscq.envolee.mobileshared.ControlGetDto
import com.lduboscq.envolee.mobileshared.ControlPostDto
import com.lduboscq.envolee.mobileshared.CourseGetDto
import com.lduboscq.envolee.mobileshared.CoursePostDto
import com.lduboscq.envolee.mobileshared.GetMeResponse
import com.lduboscq.envolee.mobileshared.RegisterCourseResponse
import com.lduboscq.envolee.mobileshared.StudentGetDto
import com.lduboscq.envolee.mobileshared.StudentPostDto
import com.lduboscq.envolee.model.Control
import com.lduboscq.envolee.model.Course
import com.lduboscq.envolee.model.Student
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class AuthenticatedApi(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val getToken: () -> String?
) {

    suspend fun getCourses(): List<Course> =
        httpClient.get("$baseUrl/courses") {
            header("Authorization", "Bearer ${getToken()}")
        }.body<List<CourseGetDto>>().map {
            Course(id = it.id, name = it.name)
        }

    suspend fun getControls(courseId: Long): List<Control> =
        httpClient.get("$baseUrl/courses/$courseId/controls") {
            header("Authorization", "Bearer ${getToken()}")
        }.body<List<ControlGetDto>>().map {
            Control(id = it.id, name = it.name)
        }

    suspend fun addControl(courseId: Long, name: String): Boolean =
        httpClient.post("$baseUrl/courses/$courseId/controls") {
            header("Authorization", "Bearer ${getToken()}")
            setBody(ControlPostDto(name))
            contentType(ContentType.Application.Json)
        }.status == HttpStatusCode.Created

    suspend fun addCourse(name: String): Boolean =
        httpClient.post("$baseUrl/courses") {
            header("Authorization", "Bearer ${getToken()}")
            setBody(CoursePostDto(name))
            contentType(ContentType.Application.Json)
        }.status == HttpStatusCode.Created

    suspend fun addStudentList(courseId: Long, names: List<String>): Boolean =
        httpClient.post("$baseUrl/courses/$courseId/students-list") {
            header("Authorization", "Bearer ${getToken()}")
            setBody(names.map { StudentPostDto(it) })
            contentType(ContentType.Application.Json)
        }.status == HttpStatusCode.Created

    suspend fun getStudents(courseId: Long): List<Student> =
        httpClient.get("$baseUrl/courses/$courseId/students") {
            header("Authorization", "Bearer ${getToken()}")
        }.body<List<StudentGetDto>>().map {
            Student(id = it.id, name = it.name)
        }

    suspend fun postAudio(courseId: Long, studentId: Long, controlId: Long, base64: String) {
        val audioPostDto = AudioPostDto(courseId, studentId, controlId, base64)
        val response = httpClient.post("$baseUrl/post-audio") {
            header("Authorization", "Bearer ${getToken()}")
            setBody(audioPostDto)
            contentType(ContentType.Application.Json)
        }
        val httpStatusCode: HttpStatusCode = response.status
        if (httpStatusCode.value != 201) {
            throw ErrorWhileCreated()
        }
    }

    suspend fun getAudio(courseId: Long, studentId: Long, controlId: Long): String {
        val response = try {
            httpClient.get(
                "$baseUrl/audio/courses/$courseId/students/$studentId/controls/$controlId"
            ) {
                header("Authorization", "Bearer ${getToken()}")
            }
        } catch (e: ClientRequestException) {
            when (e.response.status.value) {
                404 -> throw ResourceNotFound()
                400 -> throw BadBodyParams()
                else -> throw e
            }
        }
        val base64 = response.bodyAsText()
        return base64
    }

    suspend fun createInvitationCodesForCourse(courseId: Long): List<CodeForStudent> =
        httpClient.get(
            "$baseUrl/create-invit-code-for-course/$courseId"
        ) {
            header("Authorization", "Bearer ${getToken()}")
        }.body<List<CodeForStudent>>()

    suspend fun isCodeExists(code: String): RegisterCourseResponse? {
        val httpResponse = try {
            httpClient.get(
                "$baseUrl/invitation-code-exist/$code"
            ) {
                header("Authorization", "Bearer ${getToken()}")
            }
        } catch (e: ClientRequestException) {
            null
        }
        return when (httpResponse?.status) {
            HttpStatusCode.NotFound,
            HttpStatusCode.BadRequest,
            HttpStatusCode.InternalServerError -> null
            else -> {
                val response: RegisterCourseResponse? = httpResponse?.body()
                response
            }
        }
    }

    suspend fun registerUserToCourseViaCode(code: String): Boolean = httpClient.get(
        "$baseUrl/register-course-via-code/$code"
    ) {
        header("Authorization", "Bearer ${getToken()}")
    }.status == HttpStatusCode.Accepted

    suspend fun me(): GetMeResponse =
        httpClient.get("$baseUrl/me") {
            header("Authorization", "Bearer ${getToken()}")
        }.body()
}
 