package com.lduboscq.database

import com.lduboscq.envolee.mobileshared.CodeForStudent
import com.lduboscq.repositories.InvitationCodeRepository
import com.lduboscq.database.tables.InvitationCodeTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseInvitationCodeRepository : InvitationCodeRepository {

    init {
        transaction {
            SchemaUtils.create(InvitationCodeTable)
        }
    }

    override fun getCodeForStudentByIdCode(code: String): CodeForStudent? {
        return transaction {
            InvitationCodeTable.selectAll()
                .map {
                    InvitationCodeTable.toDomain(it)
                }
                .firstOrNull {
                    it.invitationCode == code
                }
        }
    }

    override fun getCodeForStudent(studentId: Long): CodeForStudent? {
        return transaction {
            InvitationCodeTable.selectAll()
                .map {
                    InvitationCodeTable.toDomain(it)
                }
                .firstOrNull {
                    it.studentId == studentId
                }
        }
    }

    override fun isCodeExists(code: String): Boolean {
        return transaction {
            InvitationCodeTable.selectAll()
                .map {
                    InvitationCodeTable.toDomain(it)
                }
                .firstOrNull {
                    it.invitationCode == code
                } != null
        }
    }

    override fun addCode(codeForStudent: CodeForStudent) {
        transaction {
            InvitationCodeTable.insert { row ->
                row[studentId] = codeForStudent.studentId
                row[invitationCode] = codeForStudent.invitationCode
                row[courseId] = codeForStudent.courseId
                row[teacherId] = codeForStudent.teacherId
                row[studentName] = codeForStudent.studentName
            }
        }
    }

    override fun getForCourse(courseId: Long): CodeForStudent? {
        TODO("Not yet implemented")
    }
}