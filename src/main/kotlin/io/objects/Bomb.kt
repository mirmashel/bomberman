package io.objects

import io.game.Match
import io.game.Player
import io.game.Ticker
import io.network.Topic
import io.objects.ObjectTypes.Destructable
import io.objects.ObjectTypes.GameObject
import io.objects.ObjectTypes.Tickable

class Bomb(val owner: Player, val game: Match, private val xPos: Int, private val yPos: Int) :
        Tickable, Destructable,
        GameObject(TileType.BOMB) {
    override fun destroy(xPos: Int, yPos: Int) {
        for (i in 1..owner.explosionSize) {
            game.findPlayer(xPos + i, yPos)?.kill()
            if (!explode(xPos + i, yPos)) {
                break
            }
            game.addToOutputQueue(Topic.REPLICA,
                    "\"type\":\"Fire\",\"position\":{\"y\":$yPos,\"x\":${xPos + i}\")")
        }
        for (i in 1..owner.explosionSize) {
            game.findPlayer(xPos - i, yPos)?.kill()
            if (!explode(xPos - i, yPos)) {
                break
            }
            game.addToOutputQueue(Topic.REPLICA,
                    "\"type\":\"Fire\",\"position\":{\"y\":$yPos,\"x\":${xPos - i}\")")
        }
        for (i in 1..owner.explosionSize) {
            game.findPlayer(xPos, yPos + i)?.kill()
            if (!explode(xPos, yPos + i)) {
                break
            }
            game.addToOutputQueue(Topic.REPLICA,
                    "\"type\":\"Fire\",\"position\":{\"y\":${yPos + i},\"x\":$xPos\")")
        }
        for (i in 1..owner.explosionSize) {
            game.findPlayer(xPos, yPos - i)?.kill()
            if (!explode(xPos, yPos - i)) {
                break
            }
            game.addToOutputQueue(Topic.REPLICA,
                    "\"type\":\"Fire\",\"position\":{\"y\":${yPos - i},\"x\":$xPos\")")
        }
    }

    private fun explode(x: Int, y: Int): Boolean {
        when (game.field[x, y]) {
            is Box -> (game.field[x, y] as Box).destroy(x, y)
            is Bomb -> (game.field[x, y] as Bomb).destroy(x, y)
            is Wall -> return false
            else -> {
            }
        }
        return true
    }

    private var timer = Ticker.FPS * 3

    override fun tick(elapsed: Long) {
        timer--
        if (timer == 0) {
            destroy(xPos, yPos)
            owner.bombsPlanted--
            game.tickables.unregisterTickable(this)
        }
    }
}