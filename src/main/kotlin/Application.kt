package io.minxyzgo

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.File

fun main() {
    readConfig()
    coreTimer(config.updatePeriod)
    embeddedServer(Netty, port = config.port) {
        module()
    }.start(wait = true)
}

fun readConfig() {
    val configFile = File(System.getProperty("user.dir") + "/config.yaml")
    if (!configFile.exists()) configFile.createNewFile()
    val text = configFile.readText()
    if (text.isNotBlank()) {
        config = yaml.decodeFromString(Config.serializer(), text)
        globalRoomDataMap += config.staticRoomList.map { it.uuid to RoomData(it, true) }
    } else {
        config = Config()
        configFile.writeText(yaml.encodeToString(Config.serializer(), config))
    }

    cacheString = parseRoomDataToString()
}

fun Application.module() {
    configureSecurity()
    configureHTTP()
    configureRouting()
}
