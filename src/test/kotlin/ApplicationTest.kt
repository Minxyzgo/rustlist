package io.minxyzgo

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.Test

class ApplicationTest {

    @Test
    fun testUpdate() {
        val client = HttpClient(CIO)
        readConfig()

        runBlocking {
            val response = client.post(urlString = "http://127.0.0.1:${config.port}/api/update") {
                headers {
                    append("Authorization", "Bearer ${config.accessToken}")
                }

                setBody(Json.encodeToString(RoomDescription(creator = "you")))
            }

            println("status: ${response.status}")
            assert(response.status == HttpStatusCode.Created)
        }

        client.close()
    }

    @Test
    fun testList() {
        val client = HttpClient(CIO)
        readConfig()

        runBlocking {
            val response = client.get("http://127.0.0.1:${config.port}?action=list")
            println(response.bodyAsText())
            assert(response.status == HttpStatusCode.OK)
        }

        client.close()
    }

}
