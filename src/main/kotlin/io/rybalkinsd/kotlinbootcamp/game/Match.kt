package io.rybalkinsd.kotlinbootcamp.game

import com.kohttp.util.Json
import com.sun.org.apache.xpath.internal.operations.Bool
import io.rybalkinsd.kotlinbootcamp.network.*
import io.rybalkinsd.kotlinbootcamp.objects.*
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.GameObject
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.Tickable
import io.rybalkinsd.kotlinbootcamp.util.logger
import io.rybalkinsd.kotlinbootcamp.util.toJson
import java.lang.Math.abs
import java.lang.Thread.sleep
import java.text.FieldPosition
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread
import kotlin.random.Random

class Match(val id: String, val numberOfPlayers: Int) : Tickable {
    var field = GameField(this, length, height)
    val inputQueue = ConcurrentLinkedQueue<RawData>()
    private var outputQueue = ConcurrentLinkedQueue<String>()
    val players = mutableMapOf<String, Player>()
    val tickables = Ticker()
    val connections = ConnectionPool()

    fun findPlayer(x: Int, y: Int): Player? {
        players.values.forEach {
            if (it.xPos == x && it.yPos == y) {
                return it
            }
        }
        return null
    }

    fun addPlayer(name: String) {
        players += Pair(name, Player(ids++, this, name,
                (startingPositions[rand].first * mult).toInt(),
                (startingPositions[rand].second * mult).toInt()))
        rand++
        rand %= 4
        // numberOfPlayers++
    }

    private fun sendGameField() {
        for (i in 0 until length) { // Раскоментить
            for (j in 0 until height) {
                val type = when (field[i, j]) {
                    is Box -> "Wood"
                    is Wall -> "Wall"
                    else -> ""
                }
                if (type != "") {
                    var act = Item(ids++, type, Cords(j * mult, i * mult))
                    //log.info(act.toJson())
                    // var alo = "\"type\":\"$type\",position\":{\"y\":${i * 10},\"x\":$j\")"
                    //connections.broadcast("[{\"id\":1,\"type\":\"Pawn\",\"position\":{\"x\":800,\"y\":32},\"alive\":true,\"direction\":\"\"}]")
                    addToOutputQueue(Topic.REPLICA, act.toJson())
                    if (type == "Wood") {
                        addToOutputQueue(Topic.REPLICA, act.toJson())
                        field[i, j] = Floor()
                    }
                }
            }
        }
    }

    fun sendPlayerStatus() = players.values.forEach {
        val chel = Chel(it.id, "Pawn", Cords(it.yPos, it.xPos), it.isAlive, "IDLE")
        addToOutputQueue(Topic.REPLICA, chel.toJson())
    }

    fun removePlayer(name: String) = players.remove(name)

    override fun tick(elapsed: Long) {
        parseInput()
        parseOutput()
        sendPlayerStatus()
        if (numberOfPlayers != 1 && players.size <= 1) {
            addToOutputQueue(Topic.END_MATCH, "")
            tickables.isEnded = true
        }

    }

    private fun parseOutput() {
        while (!outputQueue.isEmpty()) {
            var x = outputQueue.poll()
            //log.info(x)
            connections.broadcast(x)
        }
    }

    private fun parseInput() {
        while (!inputQueue.isEmpty()) {
            val curEntry = inputQueue.poll()
            val pl = players[curEntry.name] as Player
            when (curEntry.action) {
                "UP" -> pl.move(Actions.MOVE_UP)
                "DOWN" -> pl.move(Actions.MOVE_DOWN)
                "LEFT" -> pl.move(Actions.MOVE_LEFT)
                "RIGHT" -> pl.move(Actions.MOVE_RIGHT)
                "PLANT_BOMB" -> pl.plantBomb()
                else -> {
                }
            }
        }
    }

    fun addToOutputQueue(topic: Topic, data: String) = outputQueue.add(Message(topic, data).toJson())

    fun start() {
        tickables.registerTickable(this)
        sendGameField()
        sendPlayerStatus()
        connections.broadcast(Topic.START.toJson())
        tickables.gameLoop()
    }

    companion object {
        var ids = 0
        var log = logger()
        const val length = 17
        const val height = 27
        const val mult = 32
        var rand = abs(Random.nextInt()) % 4
        val startingPositions = listOf(
                Pair(1.5, 1.5), Pair(length - 1.5, 1.5),
                Pair(1.5, height - 1.5), Pair(length - 1.5, height - 1.5)
        )
    }
}