package com.lduboscq.database

import com.lduboscq.model.Audio
import com.lduboscq.repositories.AudioRepository
import com.lduboscq.database.tables.AudioTable
import com.lduboscq.database.tables.InvitationCodeTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseAudioRepository : AudioRepository {
    init {
        transaction {
            SchemaUtils.create(AudioTable)
        }
    }

    override fun addAudio(audio: Audio) {
        transaction {
            AudioTable.insert { row ->
                row[studentId] = audio.studentId
                row[controlId] = controlId
                row[base64] = base64
                row[classId] = classId
            }
        }
    }

    override fun removeAudio(audio: Audio) {
        /* transaction {
             AudioTable.deleteWhere {
                 AudioTable.classId eq audio.classId
                         && AudioTable.studentId eq audio.studentId
             }
         }*/
    }

    override fun getAllAudios(): List<Audio> {
        return transaction {
            InvitationCodeTable.selectAll()
                .map {
                    AudioTable.toDomain(it)
                }
        }
    }

    override fun getAudio(classId: Long, studentId: Long, controlId: Long): Audio? {
        return transaction {
            InvitationCodeTable.selectAll()
                .map {
                    AudioTable.toDomain(it)
                }.firstOrNull {
                    it.classId == classId && it.studentId == studentId && it.controlId == controlId
                }
        }
    }
}