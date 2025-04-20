package io.minxyzgo
import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*

fun Application.configureHTTP() {
    install(Compression) {
        gzip()
    }
}
