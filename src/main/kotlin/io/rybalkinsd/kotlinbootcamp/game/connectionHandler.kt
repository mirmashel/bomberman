package io.rybalkinsd.kotlinbootcamp.game

import com.fasterxml.jackson.databind.ObjectMapper
import io.rybalkinsd.kotlinbootcamp.util.logger
import kotlinx.coroutines.channels.consumesAll
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap


/*data class RawData(val name: String, val action: String)

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
}*/

class ConnectionHandler : TextWebSocketHandler() {
    companion object {
        val log = logger()
        val websocks = ConcurrentHashMap<WebSocketSession, String>()
        val players = ConcurrentHashMap<String, Match>()
        val matches = ConcurrentHashMap<String, Match>()
        val threads = ConcurrentHashMap<String, Thread>()
        fun addMatch(gameId: String, num: Int)
        {
            matches[gameId] = Match(gameId, num)
            //log.info("Matches " + matches.toString())
        }

        fun startMatch(gameId: String) {
            val match = matches[gameId]!!
            threads[gameId] = Thread {
                while (match.connections.connections.size < match.numberOfPlayers) {}
                match.start()
            }
            threads[gameId]!!.start()
        }
    }

    public override fun handleTextMessage(session: WebSocketSession?, message: TextMessage?) {
        val json = ObjectMapper().readTree(message?.payload)
        when (json.get("topic").asText()) {
            "connect" -> {// name gameId
                log.info("${json.get("name").asText()} connected to game ${json.get("gameId").asText()}")
                val match = matches[json.get("gameId").asText()]!!
                match.addPlayer(json.get("name").asText())
                match.connections.add(session!!, json.get("name").asText())
                websocks[session] = json.get("name").asText()
                players[json.get("name").asText()] = match
            }
            "MOVE" -> {
                val act = json.get("data").asText()
                val match = players[websocks[session]]!!
                val player = match.connections.getPlayer(session!!)!!
                //log.info("player $player moved ${json.get("data").get("direction").asText()} in game ${match.id}")
                match.inputQueue += RawData(player, json.get("data").get("direction").asText())
            }
            "PLANT_BOMB" -> {
                val match = players[websocks[session]]!!
                val player = match.connections.getPlayer(session!!)!!
                //log.info("player ${player} planted bomb}")
                match.inputQueue += RawData(player, "PLANT_BOMB")
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
                .setAllowedOrigins("*")
                .withSockJS()
    }
}
