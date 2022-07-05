package com.lduboscq.repositories

interface CreateOrGetInvitationCodeForCourse {
    fun invoke(courseId: Long): String
}
/*
class CreateOrGetInvitationCodeForCourseImpl(
    private val invitationCodeRepository: InvitationCodeRepository
) : CreateOrGetInvitationCodeForCourse {
    override fun invoke(courseId: Long): String {
        val cfs: CodeForStudent? = invitationCodeRepository.getForCourse(courseId)
        if (cfs == null) {
            val codeForStudent = CodeForStudent(
                studentId = student.id,
                invitationCode = generateRandomInvitationCode(),
                courseId = courseId,
                teacherId = loggedInTeacher.userId,
                studentName = student.nameGivenByTeacher
            )
            invitationCodeRepository.addCode(codeForStudent)
            codeForStudent
        } else {

        }
    }
}*/
