package io.rybalkinsd.kotlinbootcamp.game

import io.rybalkinsd.kotlinbootcamp.network.ConnectionPool
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue


class Match(val gameId: String, val numberofPlayers: Int) {
    val connections = ConnectionPool()
    val inputQueue = ConcurrentLinkedQueue<String>()
    val outputQueue =  ConcurrentLinkedQueue<String>()
    val players = listOf<String>().toMutableList()
    fun start() {
        return
    }
    fun stop() {
        return
    }
}

class ConnectionHandler : TextWebSocketHandler() {
    companion object {
        val matches = ConcurrentHashMap<String, Match>()
        val threads = ConcurrentHashMap<String, Thread>()
        fun addMatch(gameId: String, num: Int)
        {
            matches[gameId] = Match(gameId, num)
        }

        fun startMatch(gameId: String) {
            threads[gameId] = Thread {
                matches[gameId]!!.start()
            }
            threads[gameId]!!.start()
        }
    }


}

@Configuration
@EnableWebSocket
class WSConfig : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry
                .addHandler(ConnectionHandler(), "/connect")
                .withSockJS()
    }
}
