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
    private val players = mutableMapOf<String, Player>()
    val tickables = Ticker()
    val connections = ConnectionPool()
    var currentPlayers = numberOfPlayers
    var started = false

    fun addPlayer(name: String, session: WebSocketSession) {
        players += Pair(name, Player(ids++, this, name,
                (startingPositions[rand].first * mult).toInt(),
                (startingPositions[rand].second * mult).toInt(),
                session))
        rand++
        rand %= 4
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
                    addToOutputQueue(act.toJson())
                    if (type == "Wood") {
                    }
                }
            }
        }
    }

    fun sendPlayerStatus() = players.values.forEach {
        val chel = Chel(it.id, "Pawn", Cords(it.yPos, it.xPos), it.isAlive, "IDLE")
        addToOutputQueue(chel.toJson())
    }

    fun sendPlayers() {
        connections.broadcast(Message(Topic.COUNT, (connections.connections.values).toJson()).toJson())
    }

    fun removePlayer(name: String) = players.remove(name)

    override fun tick(elapsed: Long) {
        parseInput()
        parseOutput()
        // sendPlayerStatus()
        if ((numberOfPlayers != 1 && connections.countOpenWebsocks() <= 1) || currentPlayers <= 0) {
            // addToOutputQueue(Topic.END_MATCH, "")
            var alive = players.values.find {
                it.isAlive
            }
            if (alive != null)
                connections.send(alive.session, Message(Topic.WIN, "").toJson())
            tickables.isEnded = true
            while (outputQueue.isNotEmpty()) {}
            log.info("Game $id ended")
            connections.shutdown()
        }
    }

    private fun parseOutput() {
        if (outputQueue.isNotEmpty())
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
        connections.broadcast(Message(Topic.NAMES, players.values.map { it.name to it.id }.toJson()).toJson())
    }

    fun start() {
        tickables.registerTickable(this)
        players.values.forEach { tickables.registerTickable(it) }
        sendGameField()
        sendPlayerStatus()
        started = true
        tickables.gameLoop()
    }

    var ids = 0

    var rand = abs(Random.nextInt()) % 4
    companion object {
        var log = logger()
        const val length = 17
        const val height = 27
        const val mult = 32
        val startingPositions = listOf(
                Pair(1, 1), Pair(length - 2, 1),
                Pair(1, height - 2), Pair(length - 2, height - 2)
        )
    }
}