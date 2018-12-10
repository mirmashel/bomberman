package io.network

import io.util.logger
import io.util.toJson

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
