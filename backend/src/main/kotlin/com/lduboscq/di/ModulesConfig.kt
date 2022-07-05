package com.lduboscq.di

import com.lduboscq.repositories.memory.InMemoryAudioRepository
import com.lduboscq.database.DatabaseAudioRepository
import com.lduboscq.database.DatabaseInvitationCodeRepository
import com.lduboscq.database.DatabaseUserRepository
import com.lduboscq.repositories.memory.InMemoryControlRepository
import com.lduboscq.repositories.memory.InMemoryCourseRepository
import com.lduboscq.repositories.memory.InMemoryInvitationCodeRepository
import com.lduboscq.repositories.memory.InMemoryStudentRepository
import com.lduboscq.repositories.memory.InMemoryUserRepository
import org.kodein.di.DI
import org.kodein.di.bindSingleton

class ModulesConfig(private val inMemory: Boolean) {
    val userModule = DI {
        bindSingleton {
            if (inMemory) {
                InMemoryUserRepository()
            } else {
                DatabaseUserRepository()
            }
        }
    }

    val invitationCodeModule = DI {
        bindSingleton {
            if (inMemory) {
                InMemoryInvitationCodeRepository()
            } else {
                DatabaseInvitationCodeRepository()
            }
        }
    }

    val audioModule = DI {
        bindSingleton {
            if (inMemory) {
                InMemoryAudioRepository()
            } else {
                DatabaseAudioRepository()
            }
        }
    }

    val courseModule = DI {
        bindSingleton {
            if (inMemory) {
                InMemoryCourseRepository()
            } else {
                TODO()
            }
        }
    }

    val controlModule = DI {
        bindSingleton {
            if (inMemory) {
                InMemoryControlRepository()
            } else {
                TODO()
            }
        }
    }

    val studentModule = DI {
        bindSingleton {
            if (inMemory) {
                InMemoryStudentRepository()
            } else {
                TODO()
            }
        }
    }
}
