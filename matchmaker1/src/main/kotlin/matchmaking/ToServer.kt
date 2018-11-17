package matchmaking

import com.kohttp.dsl.httpPost
import com.kohttp.ext.EagerResponse
import com.kohttp.ext.eager
import okhttp3.RequestBody
import util.logger

class ToServer {
    companion object {
        private const val HOST = "localhost"
        private const val PORT = 8080

        fun create(): EagerResponse {

            return httpPost {
                host = HOST
                port = PORT
                path = "/game/create"
                body = RequestBody.create(null, byteArrayOf(0))
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