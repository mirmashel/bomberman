package io.objects

import io.game.*
import io.network.Topic
import io.objects.ObjectTypes.Destructable
import io.objects.ObjectTypes.GameObject
import io.objects.ObjectTypes.Tickable
import io.util.logger

class Bomb(val owner: Player, val game: Match, private val xPos: Int, private val yPos: Int) :
        Tickable, Destructable,
        GameObject(TileType.BOMB) {
    val id = Match.ids++

    override fun destroy() {
        val newX = xPos.div(Match.mult)
        val newY = yPos.div(Match.mult)
        createFire(newX, newY)
        for (i in 1..owner.explosionSize) {
            if (!createFire(newX + i, newY)) break
        }
        for (i in 1..owner.explosionSize) {
            if (!createFire(newX, newY + i)) break
        }
        for (i in 1..owner.explosionSize) {
            if (!createFire(newX - i, newY)) break
        }
        for (i in 1..owner.explosionSize) {
            if (!createFire(newX, newY - i)) break
        }
    }

    private fun createFire(x: Int, y: Int): Boolean {
        val tile = game.field[x, y]
        return when (tile) {
            is Floor, is Bomb -> {
                val f = Fire(game, x * Match.mult, y * Match.mult)
                game.field[x, y] = f
                game.addToOutputQueue(Obj(f.id, "Fire", Cords(y * Match.mult, x * Match.mult)).json())
                true
            }
            is Box -> {
                tile.destroy()
                false
            }
            is Wall -> false
            else -> {
                true
            }
        }
    }

    private fun explode(x: Int, y: Int): Boolean {
        when (game.field[x, y]) {
            is Box -> (game.field[x, y] as Box).destroy()
            is Bomb -> (game.field[x, y] as Bomb).destroy()
            is Wall -> return false
            else -> {
            }
        }
        return true
    }

    private var timer = Ticker.FPS * 2

    override fun tick(elapsed: Long) {
        val newY = yPos.div(Match.mult)
        val newX = xPos.div(Match.mult)
        game.addToOutputQueue(Obj(id, "Bomb", Cords(newY * Match.mult, newX * Match.mult)).json())
        timer--
        if (timer <= 0) {
            logger().info("Bomb id: ${id} detroyed")
            destroy()
            owner.bombsPlanted--
            game.tickables.unregisterTickable(this)
        }
    }
}