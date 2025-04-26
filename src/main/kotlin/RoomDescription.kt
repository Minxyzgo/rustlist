package io.minxyzgo

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class RoomDescription(
    val uuid: String = UUID.randomUUID().toString(),
    val roomOwner: String = "",
    val gameVersion: Int = 176,
    val netWorkAddress: String = "",
    val localAddress: String = "",
    val port: Int = 5123,
    val isOpen: Boolean = true,
    val creator: String = "",
    val requiredPassword: Boolean = false,
    val mapName: String = "",
    val mapType: String = "",
    val status: String = "battleroom",
    val version: String = "",
    val isLocal: Boolean = false,
    val displayMapName: String = "",
    val playerCurrentCount: Int? = null,
    val playerMaxCount: Int? = null,
    val isUpperCase: Boolean = false,
    val uuid2: String = "",
    val unknown: Boolean = false, //未知
    val mods: String = "",
    /**
     * 当此id为0时，则直接使用明文[netWorkAddress]和[port]加入房间
     */
    val roomId: Int = 0,
)