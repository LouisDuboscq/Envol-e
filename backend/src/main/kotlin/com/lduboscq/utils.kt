package com.lduboscq

import kotlin.random.Random

fun generateRandomInvitationCode() : String {
    val STRING_LENGTH = 6
    val charPool: List<Char> = ('0'..'9').toList()  // ('a'..'z') + ('A'..'Z') +  ('0'..'9')

    return (1..STRING_LENGTH)
        .map { i -> Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")
}