package matchmaking

import com.kohttp.dsl.httpPost
import com.kohttp.ext.EagerResponse
import com.kohttp.ext.eager
import okhttp3.RequestBody
import org.springframework.http.StreamingHttpOutputMessage
import util.logger

class ToServer {
    companion object {
        private const val HOST = "localhost"
        private const val PORT = 8090

        fun create(players: Int): EagerResponse = httpPost {
                host = HOST
                port = PORT
                path = "/game/create"
                body {
                    form {
                        "players" to players
                    }
                }
            }.eager()
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
            path = "/game/start"
            body {
                form {
                    "gameId" to gameId
                }
            }
        }.eager()

        fun reload() = httpPost {
            host = HOST
            port = PORT
            path = "/game/reload"
            body = RequestBody.create(null, byteArrayOf(0))
        }
    }
}