package com.lduboscq.routes

import com.lduboscq.envolee.mobileshared.AudioPostDto
import com.lduboscq.model.Audio
import com.lduboscq.repositories.AudioRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.postAudioRoute(
    audioRepository: AudioRepository
) {
    post("/post-audio") {
        val body = call.receive<AudioPostDto>()
        audioRepository.addAudio(
            Audio(
                body.courseId,
                body.studentId,
                body.controlId,
                body.base64,
            )
        )
        call.respond(HttpStatusCode.Created)
    }

    get("/audio/courses/{courseId}/students/{studentId}/controls/{controlId}") {
        val courseId = call.parameters["courseId"]?.toLongOrNull() ?: run {
            call.respond(HttpStatusCode.BadRequest, "courseId not an id")
            return@get
        }
        val studentId = call.parameters["studentId"]?.toLongOrNull() ?: run {
            call.respond(HttpStatusCode.BadRequest, "studentId not an id")
            return@get
        }
        val controlId = call.parameters["controlId"]?.toLongOrNull() ?: run {
            call.respond(HttpStatusCode.BadRequest, "controlId not an id")
            return@get
        }
        val base64: String? = audioRepository.getAudio(courseId, studentId, controlId)?.base64
        if (base64 != null) {
            call.respond(base64)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}
