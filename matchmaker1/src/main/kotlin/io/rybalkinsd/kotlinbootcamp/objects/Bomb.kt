package io.rybalkinsd.kotlinbootcamp.objects

import io.rybalkinsd.kotlinbootcamp.game.GameField
import io.rybalkinsd.kotlinbootcamp.game.Match
import io.rybalkinsd.kotlinbootcamp.game.Player
import io.rybalkinsd.kotlinbootcamp.game.Ticker
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.Destructable
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.GameObject
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.Tickable

class Bomb(val owner: Player, val game: Match, private val xPos: Int, private val yPos: Int) :
    Tickable, Destructable,
    GameObject(TileType.BOMB) {
    override fun destroy(xPos: Int, yPos: Int) {
        for (i in 1..owner.explosionSize) {
            game.findPlayer(xPos + i, yPos)?.kill()
            if (!explode(xPos + i, yPos)) {
                break
            }
        }
        for (i in 1..owner.explosionSize) {
            game.findPlayer(xPos - i, yPos)?.kill()
            if (!explode(xPos - i, yPos)) {
                break
            }
        }
        for (i in 1..owner.explosionSize) {
            game.findPlayer(xPos, yPos + i)?.kill()
            if (!explode(xPos, yPos + i)) {
                break
            }
        }
        for (i in 1..owner.explosionSize) {
            game.findPlayer(xPos, yPos - i)?.kill()
            if (!explode(xPos, yPos - i)) {
                break
            }
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