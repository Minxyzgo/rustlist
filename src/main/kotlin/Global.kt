package io.minxyzgo

import net.mamoe.yamlkt.Yaml
import java.util.concurrent.ConcurrentHashMap
import kotlin.text.appendLine



const val header = "CORRODINGGAMES[1.0]"

val yaml = Yaml()

@Volatile
var cacheString = ""
lateinit var config: Config
val globalRoomDataMap: ConcurrentHashMap<String, RoomData> = ConcurrentHashMap()

private val roomFields by lazy {
    RoomDescription::class.java.declaredFields
        .toMutableList().apply {
            removeFirst() // 删除掉 Companion
            forEach { it.isAccessible = true }
        }
}

fun parseRoomDataToString(): String= buildString {
    appendLine(header)
    globalRoomDataMap.values.forEach { data ->
        append(
            roomFields
                .map { it.get(data.room) }.joinToString(",")
        )
        appendLine()
    }
}

fun ConcurrentHashMap<String, RoomData>.updateRoom(uuid: String, data: RoomData) {
    val tick = this[uuid]?.tick ?: config.roomTick
    this[uuid] = data.apply { this.tick = tick }
}