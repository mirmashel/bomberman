package io.objects

import io.game.Cords
import io.game.Match
import io.game.Obj
import io.game.Player
import io.game.Ticker
import io.objects.ObjectTypes.Destructable
import io.objects.ObjectTypes.GameObject
import io.objects.ObjectTypes.Tickable

class Bomb(val owner: Player, val game: Match, private val xPos: Int, private val yPos: Int) :
        Tickable, Destructable,
        GameObject(TileType.BOMB) {

    override fun destroy() {
        val newX = xPos.div(Match.mult)
        val newY = yPos.div(Match.mult)
        game.field[newX, newY] = Floor()
        createFire(newX, newY)
        for (i in 1..owner.explosionSize) {
            if (!blowUp(newX + i, newY)) break
        }
        for (i in 1..owner.explosionSize) {
            if (!blowUp(newX, newY + i)) break
        }
        for (i in 1..owner.explosionSize) {
            if (!blowUp(newX - i, newY)) break
        }
        for (i in 1..owner.explosionSize) {
            if (!blowUp(newX, newY - i)) break
        }
    }

    private fun blowUp(x: Int, y: Int): Boolean {
        val tile = game.field[x, y]
        return when (tile) {
            is Floor -> {
                createFire(x, y)
                true
            }
            is Box -> {
                tile.destroy()
                if (!(game.field[x, y] is Bonus))
                    createFire(x, y)
                false
            }
            is Bomb -> {
                tile.explode()
                false
            }
            is Wall, is Fire -> false
            else -> {
                true
            }
        }
    }

    fun createFire(x: Int, y: Int) {
        val f = Fire(game, x * Match.mult, y * Match.mult)
        game.field[x, y] = f
        game.addToOutputQueue(Obj(f.id, "Fire", Cords(y * Match.mult, x * Match.mult)).json())
        game.tickables.registerTickable(f)
    }

    fun explode() {
        destroy()
        owner.bombsPlanted--
        game.tickables.unregisterTickable(this)
    }

    private var timer = Ticker.FPS * 2

    override fun tick(elapsed: Long) {
        val newY = yPos.div(Match.mult)
        val newX = xPos.div(Match.mult)
        game.addToOutputQueue(Obj(id, "Bomb", Cords(newY * Match.mult, newX * Match.mult)).json())
        timer--
        if (timer <= 0) {
            explode()
        }
    }
}