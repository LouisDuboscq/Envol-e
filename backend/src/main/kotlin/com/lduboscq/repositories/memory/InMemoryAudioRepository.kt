package com.lduboscq.repositories.memory

import com.lduboscq.model.Audio
import com.lduboscq.repositories.AudioRepository

class InMemoryAudioRepository: AudioRepository {

    private val allAudios = mutableListOf<Audio>()

    override fun addAudio(audio: Audio) {
        val existingAudio = getAudio(audio.classId, audio.studentId, audio.controlId)
        if (existingAudio != null) {
            removeAudio(existingAudio)
        }
        allAudios.add(audio)
    }

    override fun removeAudio(audio: Audio) {
        allAudios.remove(audio)
    }

    override fun getAllAudios(): List<Audio> {
        return allAudios
    }

    override fun getAudio(classId: Long, studentId: Long, controlId: Long): Audio? {
        return getAllAudios()
            .firstOrNull { it.classId == classId && it.studentId == studentId && it.controlId == controlId }
    }
}
