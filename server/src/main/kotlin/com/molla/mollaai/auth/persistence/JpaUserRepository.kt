package com.molla.mollaai.auth.persistence

import com.molla.mollaai.auth.entity.AppUserEntity
import com.molla.mollaai.auth.model.AppUserRecord
import com.molla.mollaai.auth.repository.UserRepository
import jakarta.persistence.EntityManagerFactory
import java.time.Instant
import java.util.UUID
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
            val existing = findEntityBySubject(entityManager, googleSubject)
            if (existing == null) {
                val entity = AppUserEntity().apply {
                    id = UUID.randomUUID()
                    this.googleSubject = googleSubject
                    this.email = email
                    this.displayName = displayName
                    this.pictureUrl = pictureUrl
                    this.lastLoginAt = now
                }
                entityManager.persist(entity)
                return@withTransaction entity.toRecord()
            }

            val updated = entityManager.createNativeQuery(
                """
                update app_users
                set email = :email,
                    display_name = :displayName,
                    picture_url = :pictureUrl,
                    last_login_at = :lastLoginAt
                where google_subject = :googleSubject
                """.trimIndent(),
            )
                .setParameter("email", email)
                .setParameter("displayName", displayName)
                .setParameter("pictureUrl", pictureUrl)
                .setParameter("lastLoginAt", now)
                .setParameter("googleSubject", googleSubject)
                .executeUpdate()

            if (updated != 1) {
                throw IllegalStateException("사용자 저장에 실패했습니다.")
            }

            entityManager.clear()
            val saved = findEntityBySubject(entityManager, googleSubject)?.toRecord()
                ?: throw IllegalStateException("저장 후 사용자를 다시 읽어오지 못했습니다.")
            logger.info("Upsert committed: id={}, subject={}", saved.id, saved.googleSubject)
            saved
        }
    }

    override fun findBySubject(googleSubject: String): AppUserRecord? {
        logger.info("Finding user by subject={}", googleSubject)
        return entityManagerFactory.withEntityManager { entityManager ->
            val result = findEntityBySubject(entityManager, googleSubject)?.toRecord()
            if (result == null) {
                logger.info("User not found: subject={}", googleSubject)
            } else {
                logger.info("User found: subject={}", googleSubject)
            }
            result
        }
    }

    override fun updatePhoneNumber(
        googleSubject: String,
        phoneNumber: String,
        now: Instant,
    ): AppUserRecord {
        logger.info("Updating phone number for subject={}", googleSubject)
        return entityManagerFactory.withTransaction { entityManager ->
            val entity = findEntityBySubject(entityManager, googleSubject)
                ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다.")

            val phoneOwner = findEntityByPhoneNumber(entityManager, phoneNumber)
            if (phoneOwner != null && phoneOwner.id != entity.id) {
                throw IllegalArgumentException("이미 사용 중인 전화번호입니다.")
            }

            val updated = entityManager.createNativeQuery(
                """
                update app_users
                set phone_number = :phoneNumber,
                    phone_verified_at = :phoneVerifiedAt
                where google_subject = :googleSubject
                """.trimIndent(),
            )
                .setParameter("phoneNumber", phoneNumber)
                .setParameter("phoneVerifiedAt", now)
                .setParameter("googleSubject", googleSubject)
                .executeUpdate()

            if (updated != 1) {
                throw IllegalStateException("전화번호 저장에 실패했습니다.")
            }

            entityManager.clear()
            val saved = findEntityBySubject(entityManager, googleSubject)?.toRecord()
                ?: throw IllegalStateException("전화번호 저장 후 사용자를 다시 읽어오지 못했습니다.")
            logger.info("Phone number updated: id={}, phoneNumber={}", saved.id, saved.phoneNumber)
            saved
        }
    }

    override fun findByPhoneNumber(phoneNumber: String): AppUserRecord? {
        logger.info("Finding user by phone number={}", phoneNumber)
        return entityManagerFactory.withEntityManager { entityManager ->
            findEntityByPhoneNumber(entityManager, phoneNumber)?.toRecord()
        }
    }

    private fun findEntityBySubject(
        entityManager: jakarta.persistence.EntityManager,
        googleSubject: String,
    ): AppUserEntity? {
        return entityManager.createQuery(
            "select u from AppUserEntity u where u.googleSubject = :googleSubject",
            AppUserEntity::class.java,
        )
            .setParameter("googleSubject", googleSubject)
            .resultList
            .firstOrNull()
    }

    private fun findEntityByPhoneNumber(
        entityManager: jakarta.persistence.EntityManager,
        phoneNumber: String,
    ): AppUserEntity? {
        return entityManager.createQuery(
            "select u from AppUserEntity u where u.phoneNumber = :phoneNumber",
            AppUserEntity::class.java,
        )
            .setParameter("phoneNumber", phoneNumber)
            .resultList
            .firstOrNull()
    }
}
