package com.molla.mollaai.auth

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence

fun createUserEntityManagerFactory(config: AppDatabaseConfig): EntityManagerFactory {
    val properties = mapOf(
        "jakarta.persistence.jdbc.url" to config.jdbcUrl,
        "jakarta.persistence.jdbc.user" to config.username,
        "jakarta.persistence.jdbc.password" to config.password,
        "jakarta.persistence.jdbc.driver" to "com.mysql.cj.jdbc.Driver",
        "hibernate.dialect" to "org.hibernate.dialect.MySQLDialect",
        "hibernate.hbm2ddl.auto" to "update",
        "hibernate.show_sql" to "false",
        "hibernate.format_sql" to "false",
    )

    return Persistence.createEntityManagerFactory("molla-ai", properties)
}

inline fun <T> EntityManagerFactory.withEntityManager(block: (EntityManager) -> T): T {
    val entityManager = createEntityManager()
    return try {
        block(entityManager)
    } finally {
        entityManager.close()
    }
}

inline fun <T> EntityManagerFactory.withTransaction(block: (EntityManager) -> T): T {
    return withEntityManager { entityManager ->
        val transaction = entityManager.transaction
        transaction.begin()
        try {
            val result = block(entityManager)
            transaction.commit()
            result
        } catch (exception: Throwable) {
            if (transaction.isActive) {
                transaction.rollback()
            }
            throw exception
        }
    }
}
