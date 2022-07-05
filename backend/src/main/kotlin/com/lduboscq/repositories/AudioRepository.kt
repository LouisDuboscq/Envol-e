package com.lduboscq.repositories

import com.lduboscq.model.Audio

interface AudioRepository {
    fun addAudio(audio: Audio)
    fun removeAudio(audio: Audio)
    fun getAllAudios(): List<Audio>
    fun getAudio(classId: Long, studentId: Long, controlId: Long): Audio?
}
