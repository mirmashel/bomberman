package io.objects

import io.game.Cords
import io.game.Match
import io.game.Obj
import io.game.Ticker
import io.objects.ObjectTypes.GameObject
import io.objects.ObjectTypes.Tickable

class Fire(val game: Match, val xPos: Int, val yPos: Int) : GameObject(game.ids++), Tickable {
    private var timer = Ticker.FPS / 7

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