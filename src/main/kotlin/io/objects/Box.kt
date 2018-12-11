package io.objects

import io.game.Cords
import io.game.Match
import io.game.Obj
import io.game.PowerUp
import io.objects.ObjectTypes.BonusType
import io.objects.ObjectTypes.Destructible
import io.objects.ObjectTypes.GameObject
import io.util.toJson
import kotlin.math.abs
import kotlin.random.Random

class Box(val game: Match, val xPos: Int, val yPos: Int) : Destructible, GameObject(game.ids++) {
    override fun destroy() {
        game.addToOutputQueue(Obj(id, "Wood",
                Cords(yPos * Match.mult, xPos * Match.mult)).json())
        val rnd = abs(Random.nextInt() % 100)
        game.field[xPos, yPos] = when {
            rnd < dropChance -> Bonus(game, xPos, yPos, BonusType.BOMBS)
            rnd < dropChance * 2 -> Bonus(game, xPos, yPos, BonusType.RANGE)
            rnd < dropChance * 3 -> Bonus(game, xPos, yPos, BonusType.SPEED)
            rnd < dropChance * 4 -> Bonus(game, xPos, yPos, BonusType.PORTAL)
            else -> Floor()
        }
        val b = game.field[xPos, yPos]
        if (b is Bonus) {
            game.addToOutputQueue(PowerUp(b.id, "Bonus",
                    Cords(yPos * Match.mult, xPos * Match.mult),
                    b.type.name).toJson())
        }
    }

    companion object {
        const val dropChance = 4
    }
}