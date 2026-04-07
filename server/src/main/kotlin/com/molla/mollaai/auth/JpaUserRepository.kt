package com.molla.mollaai.auth

import jakarta.persistence.EntityManagerFactory
import java.time.Instant

class JpaUserRepository(
    private val entityManagerFactory: EntityManagerFactory,
) : UserRepository {
    override fun upsert(
        googleSubject: String,
        email: String,
        displayName: String?,
        pictureUrl: String?,
        now: Instant,
    ): AppUserRecord {
        return entityManagerFactory.withTransaction { entityManager ->
            val entity = AppUserEntity().apply {
                this.googleSubject = googleSubject
                this.email = email
                this.displayName = displayName
                this.pictureUrl = pictureUrl
                this.lastLoginAt = now
            }

            entityManager.merge(entity).toRecord()
        }
    }

    override fun findBySubject(googleSubject: String): AppUserRecord? {
        return entityManagerFactory.withEntityManager { entityManager ->
            entityManager.find(AppUserEntity::class.java, googleSubject)?.toRecord()
        }
    }
}
