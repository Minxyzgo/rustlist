package io.minxyzgo

import kotlin.concurrent.timer

fun coreTimer(period: Long) = timer("core", true, period = period) {
    val deadRoomData = mutableListOf<String>()
    globalRoomDataMap.values.forEach { data ->
        if (!data.isStatic) data.tick--

        if (data.tick <= 0) {
            deadRoomData += data.room.uuid
        }
    }

    if (deadRoomData.isNotEmpty()) {
        deadRoomData.forEach { globalRoomDataMap.remove(it) }
    }

    cacheString = parseRoomDataToString()
}
