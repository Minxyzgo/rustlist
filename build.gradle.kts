
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.serialization)
}

group = "io.minxyzgo"
version = "1.1.0"

application {
    // 使用自定义Application.main而不是EngineMain
    // 原因：项目有自定义配置加载逻辑(config.yaml)和启动流程
    // EngineMain需要application.conf而Application.main使用config.yaml
    // 修改后程序将使用src/main/kotlin/Application.kt中的main函数
    mainClass = "io.minxyzgo.ApplicationKt"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.rate.limit)
    implementation(libs.ktor.server.default.headers)
    implementation(libs.ktor.server.compression)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
