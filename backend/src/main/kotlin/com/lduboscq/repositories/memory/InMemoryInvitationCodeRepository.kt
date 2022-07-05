package com.lduboscq.repositories.memory

import com.lduboscq.InvitationCodeAlreadyExists
import com.lduboscq.envolee.mobileshared.CodeForStudent
import com.lduboscq.repositories.InvitationCodeRepository

class InMemoryInvitationCodeRepository: InvitationCodeRepository {

    private val codes = mutableListOf<CodeForStudent>()

    override fun getCodeForStudentByIdCode(code: String): CodeForStudent? {
        return codes.firstOrNull { it.invitationCode == code }
    }

    override fun getCodeForStudent(studentId: Long): CodeForStudent? {
        return codes.firstOrNull { it.studentId == studentId }
    }

    override fun isCodeExists(code: String): Boolean {
        return codes.firstOrNull { it.invitationCode == code } != null
    }

    override fun addCode(codeForStudent: CodeForStudent) {
        if (codes.firstOrNull { it.invitationCode == codeForStudent.invitationCode } != null) {
            throw InvitationCodeAlreadyExists()
        }
        codes.add(codeForStudent)
    }

    override fun getForCourse(courseId: Long): CodeForStudent? {
        TODO("Not yet implemented")
    }
}
