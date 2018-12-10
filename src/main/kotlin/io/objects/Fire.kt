package io.objects

import io.game.*
import io.objects.ObjectTypes.GameObject
import io.objects.ObjectTypes.Tickable
import io.util.logger

class Fire(val game: Match, val xPos: Int, val yPos: Int): GameObject(TileType.FIRE), Tickable {
    private var timer = Ticker.FPS / 30

    override fun tick(elapsed: Long) {
        val newY = yPos.div(Match.mult)
        val newX = xPos.div(Match.mult)
        game.addToOutputQueue(Obj(id, "Fire", Cords(newY * Match.mult, newX * Match.mult)).json())
        timer--
        if (timer <= 0) {
            game.field[newX, newY] = Floor()
            game.tickables.unregisterTickable(this)
        }
    }
}