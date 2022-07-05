package com.lduboscq.repositories

import com.lduboscq.envolee.mobileshared.CodeForStudent

interface InvitationCodeRepository {
    fun getCodeForStudentByIdCode(code: String): CodeForStudent?
    fun getCodeForStudent(studentId: Long): CodeForStudent?
    fun isCodeExists(code: String): Boolean
    fun addCode(codeForStudent: CodeForStudent)
    fun getForCourse(courseId: Long): CodeForStudent?
}
