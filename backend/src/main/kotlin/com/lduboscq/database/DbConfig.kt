package com.lduboscq.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.h2.tools.Server
import org.jetbrains.exposed.sql.Database

object DbConfig {
    fun setup(jdbcUrl: String, username: String, password: String) {
        Server.createPgServer().start()
        val config = HikariConfig().also { config ->
            val host = System.getenv("DATABASE_HOST") ?: "0.0.0.0"
            val port = System.getenv("DATABASE_PORT") ?: 8090
            val dbName = System.getenv("DATABASE_NAME") ?: "nom_db"
            config.jdbcUrl = jdbcUrl //"jdbc:postgresql://${host}:${port}/${dbName}" //jdbcUrl
            config.username = username
            config.password = password
            //   config.driverClassName = "org.postgresql.Driver"
        }
        Database.connect(HikariDataSource(config))
    }
}
