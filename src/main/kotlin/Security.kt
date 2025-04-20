package io.minxyzgo

import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureSecurity() {
    install(Authentication) {
        bearer("auth-bearer") {
            realm = "Access all routes"
            authenticate { tokenCredential ->
                if (tokenCredential.token == config.accessToken) {
                    Unit
                } else null
            }
        }
    }
}