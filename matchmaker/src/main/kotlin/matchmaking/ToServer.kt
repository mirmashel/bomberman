package matchmaking

import com.kohttp.dsl.httpPost
import com.kohttp.ext.eager

object ToServer {
    private const val HOST = "localhost"
    private const val PORT = 8080

    fun create() = httpPost {
        host = HOST
        port = PORT
        path = "/game/create"
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
        path = "/game/connect"
        body {
            form {
                "gameId" to gameId
            }
        }
    }.eager()
}