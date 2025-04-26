package io.minxyzgo

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress


fun Application.configureRouting() {
    routing {
        rateLimit(RateLimitName("client")) {
            get("/interface") {
                val action = call.queryParameters["action"]

                val gameVersion = call.queryParameters["game_version"]
                val beta = call.queryParameters["game_version_beta"]
                //val timestamp = call.queryParameters["tx1"] 有这必要？

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

            post("/interface") {
                val parameters = call.receiveParameters()
                val action = parameters["action"]

                when (action) {
                    "add" -> {
                        if (!config.enableClientAction) {
                            call.respond(HttpStatusCode.Forbidden)
                            return@post
                        }
                        val uuid = parameters["user_id"]
                        if (uuid == null) {
                            call.respond(HttpStatusCode.BadRequest)
                            return@post
                        }
                        val data = getRoomData(uuid, parameters, call.request.origin.remoteAddress)
                        globalRoomDataMap.updateRoom(data.room.uuid, data)
                        call.respondText(
                            """
                        $header
                        ${data.room.uuid}
                    """.trimIndent()
                        )
                    }

                    "update" -> {
                        if (!config.enableClientAction) {
                            call.respond(HttpStatusCode.Forbidden)
                            return@post
                        }
                        val uuid = parameters["id"]
                        if (uuid == null) {
                            call.respond(HttpStatusCode.BadRequest)
                            return@post
                        }
                        val data = globalRoomDataMap[uuid]
                        if (data == null) {
                            call.respondText(
                                """
                            $header[FAILED]
                            GAME NOT FOUND
                        """.trimIndent()
                            )
                            return@post
                        }
                        if (parameters["private_token"] == data.privateToken) {
                            val newData = getRoomData(uuid, parameters, call.request.origin.remoteAddress)
                            globalRoomDataMap.updateRoom(uuid, newData)
                            call.respond(HttpStatusCode.OK)
                        } else {
                            call.respond(HttpStatusCode.Forbidden)
                        }
                    }

                    "self_info" -> {
                        if (!config.enableClientAction) {
                            call.respond(HttpStatusCode.Forbidden)
                            return@post
                        }
                        val address = call.request.origin.remoteAddress
                        val port = parameters["port"] ?: ""
                        val isOpen = pingIpAndPort(address, port.toIntOrNull() ?: 5123)
                        call.respondText(
                            """
                            $header
                            $address,$isOpen
                        """.trimIndent()
                        )
                    }

                    "remove" -> {
                        if (!config.enableClientAction) {
                            call.respond(HttpStatusCode.Forbidden)
                            return@post
                        }
                        val id = parameters["id"]
                        val data = globalRoomDataMap[id]
                        data?.run {
                            if (parameters["private_token"] == privateToken) {
                                globalRoomDataMap.remove(id)
                                call.respond(HttpStatusCode.OK)
                            } else {
                                call.respond(HttpStatusCode.Forbidden)
                            }
                        } ?: call.respond(HttpStatusCode.NoContent)
                    }
                }
            }
        }

        authenticate("auth-bearer") {
            post("/api/update") {
                try {
                    val room = Json.decodeFromString(RoomDescription.serializer(), call.receiveText())
                    val isStatic = call.receiveParameters()["isStatic"].toBoolean()
                    globalRoomDataMap.updateRoom(room.uuid, RoomData(room, isStatic))
                    log.info("Add room: ${room.uuid}")
                    call.respond(HttpStatusCode.Created)
                } catch (_: Throwable) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            get("/api/keep") {
                val data = globalRoomDataMap[call.queryParameters["uuid"]]
                data?.tick = config.roomTick
                call.respond(HttpStatusCode.OK)
            }

            get("/api/remove") {
                globalRoomDataMap.remove(call.queryParameters["uuid"])
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

fun pingIpAndPort(ip: String?, port: Int): Boolean {
    val s = Socket()
    try {
        val add: SocketAddress = InetSocketAddress(ip, port)
        s.connect(add, config.selfInfoTimeout)
        return true
    } catch (_: Exception) {
        return false
    } finally {
        try {
            s.close()
        } catch (_: Exception) {
        }
    }
}

fun getRoomData(uuid: String, parameters: Parameters, remoteAddress: String): RoomData {
    val oldRoom = globalRoomDataMap[uuid]?.room
    val room = oldRoom ?: RoomDescription()
    val port = parameters["port_number"]?.toIntOrNull() ?: room.port
    val newRoom = room.copy(
        uuid = uuid,
        roomOwner = parameters["game_name"] ?: room.roomOwner,
        version = parameters["game_version_string"] ?: room.version,
        requiredPassword = parameters["password_required"].toBoolean(),
        creator = parameters["created_by"] ?: room.creator,
        localAddress = parameters["private_ip"] ?: room.localAddress,
        port = port,
        netWorkAddress = remoteAddress,
        status = parameters["game_status"] ?: room.status,
        mapType = parameters["game_mode"] ?: room.mapType,
        mapName = parameters["game_map"] ?: room.mapName,
        playerCurrentCount = parameters["player_count"]?.toIntOrNull() ?: room.playerCurrentCount,
        playerMaxCount = parameters["max_player_count"]?.toIntOrNull() ?: room.playerMaxCount,
        isOpen = oldRoom?.isOpen ?: pingIpAndPort(remoteAddress, port)
    )
    
    return RoomData(newRoom, false, privateToken = parameters["private_token"])
}