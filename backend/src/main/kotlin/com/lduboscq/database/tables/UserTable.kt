package com.lduboscq.database.tables

import com.lduboscq.authentication.UserSession
import com.lduboscq.authentication.UserStudentDto
import com.lduboscq.authentication.UserTeacherDto
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object UsersTable : Table() {
    val id = long("id").autoIncrement().uniqueIndex()
    val usernameOrEmail = varchar("usernameOrEmail", length = 50).uniqueIndex()
    val password = varchar("password", length = 50)
    val material = varchar("name", length = 50)
    val displayedName = varchar("displayedName", length = 50)
    val role = varchar("role", length = 50)

    fun toDomain(row: ResultRow): UserSession {
        return if (row[role] == "teacher") {
            UserTeacherDto(
                userId = row[id],
                email = row[usernameOrEmail],
                //password = row[password],
                materialName = row[material],
                displayedName = row[displayedName],
            )
        } else if (row[role] == "student") {
            UserStudentDto(
                userId = row[id],
                username = row[usernameOrEmail]
            )
        } else {
            throw IllegalAccessError()
        }
    }
}
