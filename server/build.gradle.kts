plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSpring)
    alias(libs.plugins.springBoot)
    application
}

group = "com.molla.mollaai"
version = "1.0.0"

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("com.molla.mollaai.MollaAiApplicationKt")
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:${libs.versions.springBoot.get()}"))

    implementation(projects.shared)
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation(kotlin("reflect"))

    implementation(libs.google.api.client)
    implementation(libs.google.oauth.client)
    implementation(libs.google.http.client.jackson2)
    implementation(libs.mysql.connector.j)
    implementation(libs.hibernate.core)
    implementation(libs.java.jwt)

    //SMS발송 솔라피 API
    implementation("com.solapi:sdk:1.1.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
