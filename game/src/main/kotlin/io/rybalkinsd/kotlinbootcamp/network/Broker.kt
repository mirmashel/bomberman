package io.rybalkinsd.kotlinbootcamp.network

import io.rybalkinsd.kotlinbootcamp.game.RawData
import io.rybalkinsd.kotlinbootcamp.util.JsonHelper
import io.rybalkinsd.kotlinbootcamp.util.logger
import io.rybalkinsd.kotlinbootcamp.util.toJson
import org.springframework.web.socket.WebSocketSession


class Broker(private val connectionPool: ConnectionPool) {

    fun send(player: String, topic: Topic, data: Any) {
        val message = Message(topic, data.toJson()).toJson()
        val session = connectionPool.getSession(player)
        connectionPool.send(session!!, message)
    }

/*    fun broadcast(topic: Topic, data: Any) {
        val message = Message(topic, data.toJson()).toJson()
        connectionPool.broadcast(message)
    }*/

    companion object {
        private val log = logger()
    }
}
