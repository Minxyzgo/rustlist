package io.minxyzgo

import net.mamoe.yamlkt.Yaml
import java.util.concurrent.ConcurrentHashMap
import kotlin.text.appendLine

val yaml = Yaml()

@Volatile
var cacheString = ""
lateinit var config: Config
val globalRoomDataMap: ConcurrentHashMap<String, RoomData> = ConcurrentHashMap()

fun parseRoomDataToString(): String= buildString {
    appendLine("CORRODINGGAMES[1.0]")
    globalRoomDataMap.values.forEach { data ->
        append(
            data.room::class.java.declaredFields
                .toMutableList().apply { removeFirst() } // 删除掉 Companion
                .map {
                    it.isAccessible = true
                    it.get(data.room)
                }.joinToString(",")
        )
        appendLine()
    }
}