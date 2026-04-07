package com.molla.mollaai.auth.persistence

import com.molla.mollaai.auth.entity.AppUserEntity
import com.molla.mollaai.auth.model.AppUserRecord
import com.molla.mollaai.auth.repository.UserRepository
import jakarta.persistence.EntityManagerFactory
import java.time.Instant
import org.slf4j.LoggerFactory

class JpaUserRepository(
    private val entityManagerFactory: EntityManagerFactory,
) : UserRepository {
    private val logger = LoggerFactory.getLogger(JpaUserRepository::class.java)

    override fun upsert(
        googleSubject: String,
        email: String,
        displayName: String?,
        pictureUrl: String?,
        now: Instant,
    ): AppUserRecord {
        logger.info("Upserting user into MySQL: subject={}", googleSubject)
        return entityManagerFactory.withTransaction { entityManager ->
            val entity = AppUserEntity().apply {
                this.googleSubject = googleSubject
                this.email = email
                this.displayName = displayName
                this.pictureUrl = pictureUrl
                this.lastLoginAt = now
            }

            val saved = entityManager.merge(entity).toRecord()
            logger.info("Upsert committed: subject={}", saved.googleSubject)
            saved
        }
    }

    override fun findBySubject(googleSubject: String): AppUserRecord? {
        logger.info("Finding user by subject={}", googleSubject)
        return entityManagerFactory.withEntityManager { entityManager ->
            val result = entityManager.find(AppUserEntity::class.java, googleSubject)?.toRecord()
            if (result == null) {
                logger.info("User not found: subject={}", googleSubject)
            } else {
                logger.info("User found: subject={}", googleSubject)
            }
            result
        }
    }
}
