package matchmaking

import com.kohttp.dsl.httpPost
import com.kohttp.ext.EagerResponse
import com.kohttp.ext.eager
import util.logger

class ToServer {
    companion object {
        private const val HOST = "localhost"
        private const val PORT = 8080

        fun create(): EagerResponse {
            val log = logger()
            log.info("jopa")
            /*return httpPost {
                host = HOST
                port = PORT
                path = "/game/create"
            }.eager()*/
            return httpPost {
                host = HOST
                port = PORT
                path = "/game/create"
                body {
                    form {
                        "name" to "name"
                    }
                }
            }.eager()
        }
/*
    fun connect(name: String, gameId: String) = httpPost {
        host = HOST
        port = PORT
        path = "/game/connect"
        body {
            form {
                "gameId" to gameId
                "name" to name
            }
        }
    }.eager().code*/

        fun start(gameId: String) = httpPost {
            host = HOST
            port = PORT
            path = "/game/connect"
            body {
                form {
                    "gameId" to gameId
                }
            }
        }.eager()
    }
}