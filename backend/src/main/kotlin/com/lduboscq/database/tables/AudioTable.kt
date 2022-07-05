package com.lduboscq.database.tables

import com.lduboscq.model.Audio
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object AudioTable : Table() {
    val studentId = long("studentId")
    val controlId = long("controlId")
    val base64 = blob("base64")
    val classId = long("classId")

    override val primaryKey = PrimaryKey(studentId, controlId, classId, name = "PK_User_ID")

    fun toDomain(row: ResultRow): Audio {
        return Audio(
            studentId = row[studentId],
            controlId = row[controlId],
            base64 = row[base64].toString(),
            classId = row[classId]
        )
    }
}
