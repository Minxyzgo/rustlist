package io.minxyzgo

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.request.receiveText
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties

fun Application.configureRouting() {
    routing {
        get("/") {
            val action = call.queryParameters["action"]
            val gameVersion = call.queryParameters["game_version"]
            val beta = call.queryParameters["game_version_beta"]

            when (action) {
                "list" -> {
                    call.respondText(
                        contentType = ContentType.Text.Plain,
                        text = cacheString
                    )
                }

                else -> call.respond(HttpStatusCode.BadRequest)
            }
        }

        authenticate("auth-bearer") {
            post("/api/update") {
                try {
                    val room = Json.decodeFromString(RoomDescription.serializer(), call.receiveText())
                    globalRoomDataMap[room.uuid] = RoomData(room, false)
                    log.info("Add room: ${room.uuid}")
                    call.respond(HttpStatusCode.Created)
                } catch (_: Throwable) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            post("/api/keep") {
                val data = globalRoomDataMap[call.parameters["uuid"]]
                data?.tick = config.roomTick
                call.respond(HttpStatusCode.OK)
            }

            post("/api/remove") {
                globalRoomDataMap.remove(call.parameters["uuid"])
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
