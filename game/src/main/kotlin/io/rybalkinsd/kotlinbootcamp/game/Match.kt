package io.rybalkinsd.kotlinbootcamp.game

import com.kohttp.util.Json
import io.rybalkinsd.kotlinbootcamp.network.*
import io.rybalkinsd.kotlinbootcamp.objects.Bomb
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.GameObject
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.Tickable
import io.rybalkinsd.kotlinbootcamp.objects.TileType
import io.rybalkinsd.kotlinbootcamp.util.toJson
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread
import kotlin.random.Random

class Match(val id: String) : Tickable {
    var field = GameField(length, height)
    private val inputQueue = ConcurrentLinkedQueue<RawData>()
    private var outputQueue = ConcurrentLinkedQueue<Json>()
    private var numberOfPlayers = 0
    private val players = mutableMapOf<String, Player>()
    val tickables = Ticker()
    private val connection = ConnectionPool()

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
                startingPositions[rand++].second
            )
        )
        rand %= 4
        numberOfPlayers++
    }

    fun removePlayer(name: String) = players.remove(name)

    override fun tick(elapsed: Long) {
        parseInput()
        parseOutput()
        if (players.size <= 1) {
            connection.broadcast(Topic.END_MATCH.toJson())
        }
    }

    fun parseOutput() {
        while (!outputQueue.isEmpty()) {
            connection.broadcast(outputQueue.poll().toJson())
        }
    }

    private fun parseInput() {
        while (!inputQueue.isEmpty()) {
            val curEntry = inputQueue.poll()
            val pl = players[curEntry.name] as Player
            when (curEntry.action) {
                "MOVE_UP" -> pl.moveUp()
                "MOVE_DOWN" -> pl.moveDown()
                "MOVE_LEFT" -> pl.moveLeft()
                "MOVE_RIGHT" -> pl.moveRight()
                "PLANT_BOMB" -> pl.plantBomb()
                else -> {
                }
            }
        }
    }

    fun addToOutputQueue(): Json {
        TODO()
    }

    fun start() {
        tickables.registerTickable(this)
        tickables.gameLoop()
    }

    companion object {
        const val length = 17
        const val height = 13
        var rand = Random.nextInt() % 4
        val startingPositions = listOf(
            Pair(1, 1), Pair(length - 1, 1),
            Pair(1, height - 1), Pair(length - 1, height - 1)
        )
    }
}