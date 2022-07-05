package com.lduboscq.envolee.presentation

import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import envolee.resources.MR.strings.add_control
import envolee.resources.MR.strings.add_course
import envolee.resources.MR.strings.add_students
import envolee.resources.MR.strings.at_least_six_caracters
import envolee.resources.MR.strings.choose_role
import envolee.resources.MR.strings.controls
import envolee.resources.MR.strings.course_found
import envolee.resources.MR.strings.courses
import envolee.resources.MR.strings.create_account
import envolee.resources.MR.strings.displayed_name
import envolee.resources.MR.strings.email
import envolee.resources.MR.strings.email_or_username
import envolee.resources.MR.strings.enter_class_code
import envolee.resources.MR.strings.enter_class_name
import envolee.resources.MR.strings.enter_control_name
import envolee.resources.MR.strings.enter_student_name_or_list
import envolee.resources.MR.strings.get_feedbacks
import envolee.resources.MR.strings.get_started
import envolee.resources.MR.strings.give_feedbacks
import envolee.resources.MR.strings.i_am_a_student
import envolee.resources.MR.strings.i_am_a_teacher
import envolee.resources.MR.strings.join_course
import envolee.resources.MR.strings.login
import envolee.resources.MR.strings.material_name
import envolee.resources.MR.strings.password
import envolee.resources.MR.strings.students
import envolee.resources.MR.strings.teacher_must_generate_code
import envolee.resources.MR.strings.unknown_user
import envolee.resources.MR.strings.user_already_exists
import envolee.resources.MR.strings.username
import envolee.resources.MR.strings.classes

object Localizable {
    fun getLoginString(): StringDesc = StringDesc.Resource(login)
    fun iAmATeacher(): StringDesc = StringDesc.Resource(i_am_a_teacher)
    fun iAmAStudent(): StringDesc = StringDesc.Resource(i_am_a_student)
    fun username(): StringDesc = StringDesc.Resource(username)
    fun password(): StringDesc = StringDesc.Resource(password)
    fun displayedName(): StringDesc = StringDesc.Resource(displayed_name)
    fun materialName(): StringDesc = StringDesc.Resource(material_name)
    fun unknownUser(): StringDesc = StringDesc.Resource(unknown_user)
    fun anUserAlreadyExistsWithThisUsernameOrEmail(): StringDesc =
        StringDesc.Resource(user_already_exists)

    fun atLeastSixCaracters(): StringDesc = StringDesc.Resource(at_least_six_caracters)
    fun courseFound(vararg inputs: String): StringDesc =
        StringDesc.ResourceFormatted(course_found, *inputs)

    fun teacherMustGenerateCode(): StringDesc = StringDesc.Resource(teacher_must_generate_code)
    fun getStarted(): StringDesc = StringDesc.Resource(get_started)
    fun chooseRole(): StringDesc = StringDesc.Resource(choose_role)
    fun getFeedbacks(): StringDesc = StringDesc.Resource(get_feedbacks)
    fun giveFeedbacks(): StringDesc = StringDesc.Resource(give_feedbacks)
    fun createAnAccount(): StringDesc = StringDesc.Resource(create_account)
    fun emailOrUsername(): StringDesc = StringDesc.Resource(email_or_username)
    fun joinCourse(): StringDesc = StringDesc.Resource(join_course)
    fun addCourse(): StringDesc = StringDesc.Resource(add_course)
    fun enterClassCode(): StringDesc = StringDesc.Resource(enter_class_code)
    fun courses(): StringDesc = StringDesc.Resource(courses)
    fun controls(): StringDesc = StringDesc.Resource(controls)
    fun email(): StringDesc = StringDesc.Resource(email)
    fun addStudents(): StringDesc = StringDesc.Resource(add_students)
    fun students(): StringDesc = StringDesc.Resource(students)
    fun enterStudentNameOrList(): StringDesc = StringDesc.Resource(enter_student_name_or_list)
    fun addControl(): StringDesc = StringDesc.Resource(add_control)
    fun enterControlName(): StringDesc = StringDesc.Resource(enter_control_name)
    fun classes(): StringDesc = StringDesc.Resource(classes)
    fun enterClassName(): StringDesc = StringDesc.Resource(enter_class_name)
}