package io.game

import io.network.Actions
import io.network.ConnectionPool
import io.network.Message
import io.network.Topic
import io.objects.Box
import io.objects.ObjectTypes.Tickable
import io.objects.Wall
import io.util.logger
import io.util.toJson
import org.springframework.web.socket.WebSocketSession
import java.lang.Math.abs
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.random.Random

class Match(val id: String, val numberOfPlayers: Int) : Tickable {
    var field = GameField(this, length, height)
    val inputQueue = ConcurrentLinkedQueue<RawData>()
    private var outputQueue = ConcurrentLinkedQueue<String>()
    val players = mutableMapOf<String, Player>()
    val tickables = Ticker()
    val connections = ConnectionPool()
    var currentPlayers = numberOfPlayers

    fun findPlayer(x: Int, y: Int): Player? {
        players.values.forEach {
            if (it.xPos == x && it.yPos == y) {
                return it
            }
        }
        return null
    }

    fun addPlayer(name: String, session: WebSocketSession) {
        players += Pair(name, Player(ids++, this, name,
                (startingPositions[rand].first * mult).toInt(),
                (startingPositions[rand].second * mult).toInt(),
                session))
        rand++
        rand %= 4
        // numberOfPlayers++
    }

    private fun sendGameField() {
        for (i in 0 until length) {
            for (j in 0 until height) {
                val type = when (field[i, j]) {
                    is Box -> "Wood"
                    is Wall -> "Wall"
                    else -> ""
                }
                if (type != "") {
                    val act = Obj(field[i, j].id, type, Cords(j * mult, i * mult))
                    // log.info(act.toJson())
                    // var alo = "\"type\":\"$type\",position\":{\"y\":${i * 10},\"x\":$j\")"
                    // connections.broadcast("[{\"id\":1,\"type\":\"Pawn\",\"position\":{\"x\":800,\"y\":32},\"alive\":true,\"direction\":\"\"}]")
                    addToOutputQueue(act.toJson())
                    if (type == "Wood") { // delete
                        // addToOutputQueue(act.toJson())
                        // field[i, j] = Floor()
                    }
                }
            }
        }
    }

    fun sendPlayerStatus() = players.values.forEach {
        val chel = Chel(it.id, "Pawn", Cords(it.yPos, it.xPos), it.isAlive, "IDLE")
        addToOutputQueue(chel.toJson())
    }

    fun removePlayer(name: String) = players.remove(name)

    override fun tick(elapsed: Long) {
        parseInput()
        parseOutput()
        // sendPlayerStatus()
        if (numberOfPlayers != 1 && currentPlayers <= 1) {
            // addToOutputQueue(Topic.END_MATCH, "")
            var alive = players.values.find {
                it.isAlive
            }
            if (alive != null)
                connections.send(alive.session, Message(Topic.WIN, "").toJson())
            tickables.isEnded = true
        }
    }

    private fun parseOutput() {
        /*while (!outputQueue.isEmpty()) {
            var x = outputQueue.poll()
            //log.info(x)
            connections.broadcast(x)
        }*/
        // log.info(x)
        connections.broadcast(Message(Topic.REPLICA, outputQueue.toJson()).toJson())
        outputQueue.clear()
    }

    private fun parseInput() {
        while (!inputQueue.isEmpty()) {
            val curEntry = inputQueue.poll()
            if (players[curEntry.name] == null)
                continue
            val pl = players[curEntry.name] as Player
            when (curEntry.action) {
                "UP" -> pl.move(Actions.MOVE_UP)
                "DOWN" -> pl.move(Actions.MOVE_DOWN)
                "LEFT" -> pl.move(Actions.MOVE_LEFT)
                "RIGHT" -> pl.move(Actions.MOVE_RIGHT)
                "IDLE" -> pl.move(Actions.IDLE)
                "PLANT_BOMB" -> pl.plantBomb()
                else -> {
                }
            }
        }
    }

    fun addToOutputQueue(data: String) = outputQueue.add(data)

    fun sendNames() {
        connections.broadcast(Message(Topic.NAMES, players.values.map { it.name }.toJson()).toJson())
    }

    fun start() {
        tickables.registerTickable(this)
        players.values.forEach { tickables.registerTickable(it) }
        sendGameField()
        sendPlayerStatus()
        sendNames()
        tickables.gameLoop()
    }

    companion object {
        var ids = 0
        var log = logger()
        const val length = 17
        const val height = 27
        const val mult = 32
        const val step = 64
        var rand = abs(Random.nextInt()) % 4
        val startingPositions = listOf(
                Pair(1, 1), Pair(length - 2, 1),
                Pair(1, height - 2), Pair(length - 2, height - 2)
        )
    }
}