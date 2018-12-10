package io.objects

import io.game.Cords
import io.game.Match
import io.game.Obj
import io.game.PowerUp
import io.objects.ObjectTypes.BonusType
import io.objects.ObjectTypes.Destructable
import io.objects.ObjectTypes.GameObject
import io.util.logger
import io.util.toJson
import kotlin.math.abs
import kotlin.random.Random

class Box(val game: Match, val xPos: Int, val yPos: Int) : Destructable, GameObject(TileType.BOX) {
    override fun destroy() {
        val rnd = abs(Random.nextInt() % 100)
        game.field[xPos, yPos] = when {
            rnd <= dropChance -> Bonus(game, xPos, yPos, BonusType.BOMBS)
            rnd <= dropChance * 2 -> Bonus(game, xPos, yPos, BonusType.RANGE)
            rnd <= dropChance * 3 -> Bonus(game, xPos, yPos, BonusType.SPEED)
            else -> Floor()
        }
        logger().info(game.field[xPos, yPos].t.toString())
        game.addToOutputQueue(Obj(id, "Wood", Cords(yPos * Match.mult, xPos * Match.mult)).json())
        val b = game.field[xPos, yPos]
        if (b is Bonus) {
            // logger().info("bonus id ${b.id} created at $xPos $yPos")
            game.addToOutputQueue(PowerUp(b.id, "Bonus",
                    Cords(yPos * Match.mult, xPos * Match.mult),
                    b.type.name).toJson())
        }
    }

    companion object {
        const val dropChance = 20
    }
}