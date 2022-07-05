package com.lduboscq.database.tables

import com.lduboscq.envolee.mobileshared.CodeForStudent
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object InvitationCodeTable : IntIdTable() {
    val invitationCode = varchar("invitationCode", length = 50).uniqueIndex()
    val studentId = long("studentId")
    val courseId = long("name")
    val teacherId = long("displayedName")
    val studentName = varchar("role", length = 50)

    fun toDomain(row: ResultRow): CodeForStudent {
        return CodeForStudent(
            studentId = row[studentId],
            invitationCode = row[invitationCode],
            courseId = row[courseId],
            teacherId = row[teacherId],
            studentName = row[studentName]
        )
    }
}
