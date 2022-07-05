package com.lduboscq.envolee.model

sealed class UserRole(val name: String) {
    object Teacher : UserRole("teacher")
    object Student : UserRole("student")
}
