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

    override fun destroy(xPos: Int, yPos: Int) {

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

    private var timer = Ticker.FPS * 2

    override fun tick(elapsed: Long) {
        val newY = yPos.div(Match.mult)
        val newX = xPos.div(Match.mult)
        game.addToOutputQueue(Bmb(id, "Bomb", Cords(newY * Match.mult, newX * Match.mult)).json())
        timer--
        if (timer <= 0) {
            logger().info("Bomb id: ${id} detroyed")
            destroy(xPos, yPos)
            owner.bombsPlanted--
            game.tickables.unregisterTickable(this)
        }
    }
}