package com.molla.mollaai

import com.molla.mollaai.auth.AppAuthConfig
import com.molla.mollaai.auth.JpaUserRepository
import com.molla.mollaai.auth.createUserEntityManagerFactory
import com.molla.mollaai.auth.GoogleIdTokenAuthService
import com.molla.mollaai.auth.loadAppDatabaseConfig
import com.molla.mollaai.auth.UserRepository
import com.molla.mollaai.auth.loadAppAuthConfig
import com.molla.mollaai.auth.toJsonObject
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.config.yaml.YamlConfig
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private const val JSON_MEDIA_TYPE = "application/json; charset=utf-8"

fun main() {
    val yamlConfig = YamlConfig("application.yaml")
        ?: error("server/src/main/resources/application.yaml을 찾을 수 없습니다.")
    val port = yamlConfig.property("ktor.deployment.port").getString().toInt()
    val host = yamlConfig.property("ktor.deployment.host").getString()
    val authConfig = loadAppAuthConfig(yamlConfig)
    val databaseConfig = loadAppDatabaseConfig(yamlConfig)
    val entityManagerFactory = createUserEntityManagerFactory(databaseConfig)
    val userRepository = JpaUserRepository(entityManagerFactory)

    Runtime.getRuntime().addShutdownHook(Thread {
        entityManagerFactory.close()
    })

    embeddedServer(
        Netty,
        port = port,
        host = host,
        module = { module(authConfig = authConfig, userRepository = userRepository) },
    )
        .start(wait = true)
}

fun Application.module(
    authConfig: AppAuthConfig = AppAuthConfig(
        googleWebClientId = "test-client-id",
        jwtSecret = "test-secret",
        jwtIssuer = "molla-ai",
        jwtAudience = "molla-ai-mobile",
    ),
    userRepository: UserRepository,
    authService: GoogleIdTokenAuthService = GoogleIdTokenAuthService(authConfig, userRepository),
) {
    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }

        get("/health") {
            call.respondText("ok")
        }

        post("/auth/google") {
            try {
                val requestJson = Json.parseToJsonElement(call.receiveText()).jsonObject
                val idToken = requestJson["idToken"]?.jsonPrimitive?.content
                    ?: return@post call.respondText(
                        text = """{"message":"idToken이 필요합니다."}""",
                        contentType = ContentType.parse(JSON_MEDIA_TYPE),
                        status = HttpStatusCode.BadRequest,
                    )

                val session = authService.authenticate(idToken)
                call.respondText(
                    text = session.toJsonObject().toString(),
                    contentType = ContentType.parse(JSON_MEDIA_TYPE),
                    status = HttpStatusCode.OK,
                )
            } catch (exception: IllegalArgumentException) {
                call.respondText(
                    text = """{"message":"${exception.message ?: "인증 실패"}"}""",
                    contentType = ContentType.parse(JSON_MEDIA_TYPE),
                    status = HttpStatusCode.Unauthorized,
                )
            }
        }
    }
}
