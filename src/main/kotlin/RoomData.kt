package io.minxyzgo

class RoomData(
    val room: RoomDescription,
    val isStatic: Boolean,

    @Volatile
    var tick: Int = config.roomTick
)