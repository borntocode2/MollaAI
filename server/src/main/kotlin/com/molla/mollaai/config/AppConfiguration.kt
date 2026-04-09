package com.molla.mollaai.config

import com.molla.mollaai.auth.config.AppAuthConfig
import com.molla.mollaai.auth.config.AppDatabaseConfig
import com.molla.mollaai.auth.persistence.JpaUserRepository
import com.molla.mollaai.auth.persistence.createUserEntityManagerFactory
import com.molla.mollaai.auth.repository.UserRepository
import com.molla.mollaai.auth.service.AccessTokenService
import com.molla.mollaai.auth.service.AuthService
import com.molla.mollaai.auth.service.JwtAccessTokenService
import com.molla.mollaai.auth.service.GoogleIdTokenAuthService
import com.molla.mollaai.phone.service.InMemoryPhoneVerificationCodeStore
import com.molla.mollaai.phone.service.PhoneVerificationCodeStore
import com.molla.mollaai.phone.service.PhoneVerificationService
import com.molla.mollaai.phone.service.SolapiSmsSender
import jakarta.persistence.EntityManagerFactory
import java.time.Clock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.boot.context.properties.EnableConfigurationProperties

@Configuration
@EnableConfigurationProperties(AppProperties::class, OpenAiProperties::class)
class AppConfiguration {
    @Bean
    fun appAuthConfig(appProperties: AppProperties): AppAuthConfig = AppAuthConfig(
        googleWebClientId = appProperties.googleWebClientId,
        jwtSecret = appProperties.jwtSecret,
        jwtIssuer = appProperties.jwtIssuer,
        jwtAudience = appProperties.jwtAudience,
    )

    @Bean
    fun appDatabaseConfig(appProperties: AppProperties): AppDatabaseConfig = AppDatabaseConfig(
        jdbcUrl = appProperties.jdbcUrl,
        username = appProperties.username,
        password = appProperties.password,
    )

    @Bean(destroyMethod = "close")
    fun entityManagerFactory(databaseConfig: AppDatabaseConfig): EntityManagerFactory {
        return createUserEntityManagerFactory(databaseConfig)
    }

    @Bean
    fun userRepository(entityManagerFactory: EntityManagerFactory): UserRepository {
        return JpaUserRepository(entityManagerFactory)
    }

    @Bean
    fun googleIdTokenAuthService(
        authConfig: AppAuthConfig,
        userRepository: UserRepository,
    ): AuthService {
        return GoogleIdTokenAuthService(authConfig, userRepository)
    }

    @Bean
    fun accessTokenService(appAuthConfig: AppAuthConfig): AccessTokenService {
        return JwtAccessTokenService(appAuthConfig)
    }

    @Bean
    fun phoneVerificationCodeStore(): PhoneVerificationCodeStore {
        return InMemoryPhoneVerificationCodeStore()
    }

    @Bean
    fun smsSender(): SolapiSmsSender {
        return SolapiSmsSender(
            apiKey = requiredEnv("SOLAPI_API_KEY"),
            apiSecret = requiredEnv("SOLAPI_API_SECRET"),
            fromNumber = requiredEnv("SOLAPI_FROM_NUMBER"),
        )
    }

    @Bean
    fun phoneVerificationService(
        accessTokenService: AccessTokenService,
        userRepository: UserRepository,
        phoneVerificationCodeStore: PhoneVerificationCodeStore,
        smsSender: SolapiSmsSender,
    ): PhoneVerificationService {
        return PhoneVerificationService(
            accessTokenService = accessTokenService,
            userRepository = userRepository,
            codeStore = phoneVerificationCodeStore,
            smsSender = smsSender,
            clock = Clock.systemUTC(),
        )
    }

    private fun requiredEnv(name: String): String {
        return System.getenv(name)?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("Required environment variable is missing: $name")
    }
}
