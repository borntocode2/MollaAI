package com.molla.mollaai.config

import com.molla.mollaai.auth.config.AppAuthConfig
import com.molla.mollaai.auth.config.AppDatabaseConfig
import com.molla.mollaai.auth.persistence.JpaUserRepository
import com.molla.mollaai.auth.persistence.createUserEntityManagerFactory
import com.molla.mollaai.auth.repository.UserRepository
import com.molla.mollaai.auth.service.AuthService
import com.molla.mollaai.auth.service.GoogleIdTokenAuthService
import jakarta.persistence.EntityManagerFactory
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
}
