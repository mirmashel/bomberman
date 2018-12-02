package io.rybalkinsd.kotlinbootcamp.game

import com.fasterxml.jackson.databind.ObjectMapper
import io.rybalkinsd.kotlinbootcamp.network.Broker
import io.rybalkinsd.kotlinbootcamp.network.ConnectionPool
import io.rybalkinsd.kotlinbootcamp.websocket.Message
import io.rybalkinsd.kotlinbootcamp.websocket.User
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.handler.TextWebSocketHandler
import sun.security.ec.ECDSASignature
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue


data class RawData(val name: String, val action: String)

class Match(val gameId: String, val numberofPlayers: Int) {
    val connections = ConnectionPool()
    val inputQueue = ConcurrentLinkedQueue<RawData>()
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
        val players = ConcurrentHashMap<String, Match>()
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

    public override fun handleTextMessage(session: WebSocketSession?, message: TextMessage?) {
        val json = ObjectMapper().readTree(message?.payload)
        // {type: "connect/action", gameId: "gameId/msg"}

        when (json.get("type").asText()) {
            "connect" -> {// name gameId
                val match = matches[json.get("gameId").asText()]!!
                match.players += match.connections.getPlayer(session!!)!!
                players[json.get("name").asText()] = matches[json.get("gameId").asText()]!!

                /*broadcast(Message("say", json.get("data").asText()))
                broadcast(Message("say", "${json.get("user").asText()} logged in!"))
                val user = User(uids.getAndIncrement(), json.get("data").asText())
                connections.put(session!!, user)

                // tell this user about all other users
                emit(session, Message("online", connections.values))
                roadcastToOthers(session, Message("join", user))*/
            }
            "action" -> {// name, action
                val act = json.get("action").asText()
                val game = players[json.get("name").asText()]!!
                val player = game.connections.getPlayer(session!!)!!
                game.inputQueue += RawData(player, act)


                //broadcast(Message("say", "${json.get("user").asText()}: ${json.get("data").asText()}"))
            }
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
