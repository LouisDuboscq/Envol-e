package com.lduboscq.database

import com.lduboscq.StudentAlreadyExistsWithThisUsername
import com.lduboscq.TeacherAlreadyExistsWithThisEmail
import com.lduboscq.authentication.LoginDto
import com.lduboscq.authentication.UserSession
import com.lduboscq.authentication.UserStudentDto
import com.lduboscq.authentication.UserTeacherDto
import com.lduboscq.repositories.UserRepository
import com.lduboscq.database.tables.UsersTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseUserRepository : UserRepository {

    init {
        transaction {
            SchemaUtils.create(UsersTable)
        }
    }

    override fun findUserById(id: Long): UserSession? {
        return transaction {
            UsersTable.selectAll().map {
                UsersTable.toDomain(it)
            }.firstOrNull {
                when (it) {
                    is UserTeacherDto -> it.userId == id
                    is UserStudentDto -> it.userId == id
                    else -> throw IllegalAccessError()
                }
            }
        }
    }

    override fun findUserByCredentials(credential: LoginDto): UserSession? {
        return transaction {
            UsersTable.selectAll().map {
                UsersTable.toDomain(it)
            }.firstOrNull {
                when (it) {
                    is UserTeacherDto -> it.email == credential.teacherEmailOrStudentUsername
                    is UserStudentDto -> it.username == credential.teacherEmailOrStudentUsername
                    else -> throw IllegalAccessError()
                }
            }
        }
    }

    override fun signupStudent(username: String): UserStudentDto {
        val students = transaction {
            UsersTable.selectAll().map { UsersTable.toDomain(it) }.filterIsInstance<UserStudentDto>()
        }
        val studentAlreadyExists = students.any { it.username == username }
        if (studentAlreadyExists) throw StudentAlreadyExistsWithThisUsername()

        val user = transaction {
            UsersTable.insert { row ->
                row[usernameOrEmail] = username
                row[password] = "todo"
                row[material] = ""
                row[displayedName] = ""
                row[role] = "student"
            }
        }
        return UserStudentDto(user[UsersTable.id], username)
    }

    override fun signupTeacher(
        email: String,
        displayedName: String,
        materials: String
    ): UserTeacherDto {
        val teachers = transaction {
            UsersTable.selectAll().map { UsersTable.toDomain(it) }.filterIsInstance<UserTeacherDto>()
        }
        val teacherAlreadyExists = teachers.any { it.email == email }
        if (teacherAlreadyExists) throw TeacherAlreadyExistsWithThisEmail()

        val user = transaction {
            UsersTable.insert { row ->
                row[usernameOrEmail] = email
                row[password] = "todo"
                row[material] = materials
                row[UsersTable.displayedName] = displayedName
                row[role] = "teacher"
            }
        }
        return UserTeacherDto(user[UsersTable.id], email, displayedName, materials)
    }

    override fun getAllUsers(): List<UserSession> {
        return transaction {
            UsersTable.selectAll().map {
                UsersTable.toDomain(it)
            }
        }
    }

    override fun getTeacher(teacherId: Long): UserTeacherDto? {
        return transaction {
            UsersTable.selectAll().map {
                UsersTable.toDomain(it)
            }
                .filterIsInstance<UserTeacherDto>()
                .firstOrNull {
                    it.userId == teacherId
                }
        }
    }

    override fun getById(id: Long): UserSession? {
        return transaction {
            UsersTable.selectAll().map {
                UsersTable.toDomain(it)
            }.firstOrNull {
                when (it) {
                    is UserTeacherDto -> it.userId == id
                    is UserStudentDto -> it.userId == id
                    else -> throw IllegalAccessError()
                }
            }
        }
    }
}