plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "com.molla.mollaai"
version = "1.0.0"
application {
    mainClass.set("com.molla.mollaai.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.serverConfigYaml)
    implementation(libs.google.api.client)
    implementation(libs.google.oauth.client)
    implementation(libs.google.http.client.jackson2)
    implementation(libs.mysql.connector.j)
    implementation(libs.hibernate.core)
    implementation(libs.java.jwt)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}
