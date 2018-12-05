package io.rybalkinsd.kotlinbootcamp.game

import com.kohttp.util.Json
import io.rybalkinsd.kotlinbootcamp.network.*
import io.rybalkinsd.kotlinbootcamp.objects.*
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.GameObject
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.Tickable
import io.rybalkinsd.kotlinbootcamp.util.toJson
import java.lang.Math.abs
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread
import kotlin.random.Random

class Match(val id: String, val numberOfPlayers: Int) : Tickable {
    var field = GameField(length, height)
    val inputQueue = ConcurrentLinkedQueue<RawData>()
    private var outputQueue = ConcurrentLinkedQueue<Message>()
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
        players += Pair(
                name, Player(
                this,
                name,
                startingPositions[rand].first,
                startingPositions[rand].second
        )
        )
        rand++
        rand %= 4
        // numberOfPlayers++
    }

    private fun sendGameField() {
        for (i in 0 until length) {
            for (j in 0 until height) {
                val type = when (field[j, i]) {
                    is Box -> "Wood"
                    is Wall -> "Wall"
                    is Floor -> "Floor"
                    else -> ""
                }
                if (type != "") {
                    addToOutputQueue(Topic.REPLICA,
                            "\"type\":\"$type\",\"position\":{\"y\":$i,\"x\":$j\")")
                }
            }
        }
    }

    fun sendPlayerStatus() = players.values.forEach {
        addToOutputQueue(Topic.MOVE,
                "\"type\":\"Pawn\",\"position\":{\"y\":${it.yPos},\"x\":${it.xPos}}," +
                        "\"alive\":$it.isAlive,\"direction\":\"\"")
    }

    fun removePlayer(name: String) = players.remove(name)

    override fun tick(elapsed: Long) {
        parseInput()
        parseOutput()
        if (numberOfPlayers != 1 && players.size <= 1) {
            addToOutputQueue(Topic.END_MATCH, "")
            tickables.isEnded = true
        }

    }

    private fun parseOutput() {
        while (!outputQueue.isEmpty()) {
            connections.broadcast(outputQueue.poll().toJson())
        }
    }

    private fun parseInput() {
        while (!inputQueue.isEmpty()) {
            val curEntry = inputQueue.poll()
            val pl = players[curEntry.name] as Player
            when (curEntry.action) {
                "MOVE_UP" -> pl.move(Actions.MOVE_UP)
                "MOVE_DOWN" -> pl.move(Actions.MOVE_DOWN)
                "MOVE_LEFT" -> pl.move(Actions.MOVE_LEFT)
                "MOVE_RIGHT" -> pl.move(Actions.MOVE_RIGHT)
                "PLANT_BOMB" -> pl.plantBomb()
                else -> {
                }
            }
        }
    }

    fun addToOutputQueue(topic: Topic, data: Any) = outputQueue.add(Message(topic, data.toJson()))

    fun start() {
        tickables.registerTickable(this)
        sendGameField()
        sendPlayerStatus()
        connections.broadcast(Topic.START.toJson())
        tickables.gameLoop()
    }

    companion object {
        const val length = 17
        const val height = 13
        var rand = abs(Random.nextInt()) % 4
        val startingPositions = listOf(
                Pair(1, 1), Pair(length - 1, 1),
                Pair(1, height - 1), Pair(length - 1, height - 1)
        )
    }
}