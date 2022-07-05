package com.lduboscq

import com.lduboscq.database.DbConfig
import com.lduboscq.di.ModulesConfig
import com.lduboscq.plugins.configureRouting
import com.lduboscq.plugins.configureSecurity
import com.lduboscq.plugins.configureSerialization
import com.lduboscq.repositories.AudioRepository
import com.lduboscq.repositories.ControlRepository
import com.lduboscq.repositories.InvitationCodeRepository
import com.lduboscq.repositories.StudentRepository
import com.lduboscq.repositories.UserRepository
import com.lduboscq.repositories.memory.CourseRepository
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.kodein.di.instance

fun main() {

    val inMemory = true

    val databasePassword = System.getenv("DATABASE_PASSWORD") ?: ""
    val databaseUser = System.getenv("DATABASE_USER") ?: "sa"
    val jdbcUrl = "jdbc:h2:file:./data/demo"

    if (!inMemory) {
        DbConfig.setup(jdbcUrl, databaseUser, databasePassword)
    }

    val port = System.getenv("PORT")?.toInt() ?: 8082
    embeddedServer(
        factory = Netty,
        port = port,
        host = "0.0.0.0",
        module = {
            mainModule(inMemory)
            configureSerialization()
        }
    ).start(wait = true)
}

fun Application.mainModule(inMemory: Boolean) {
    val userRepository by ModulesConfig(inMemory).userModule.instance<UserRepository>()
    val invitationCodeRepository by ModulesConfig(inMemory).invitationCodeModule.instance<InvitationCodeRepository>()
    val audioRepository by ModulesConfig(inMemory).audioModule.instance<AudioRepository>()
    val controlRepository by ModulesConfig(inMemory).controlModule.instance<ControlRepository>()
    val studentRepository by ModulesConfig(inMemory).studentModule.instance<StudentRepository>()
    val courseRepository by ModulesConfig(inMemory).courseModule.instance<CourseRepository>()

    configureRouting(
        userRepository = userRepository,
        invitationCodeRepository = invitationCodeRepository,
        audioRepository = audioRepository,
        courseRepository = courseRepository,
        studentRepository = studentRepository,
        controlRepository = controlRepository
    )
    configureSecurity(userRepository = userRepository)
}