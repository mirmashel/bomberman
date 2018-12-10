package io.objects

import io.game.Match
import io.network.Topic
import io.objects.ObjectTypes.Destructable
import io.objects.ObjectTypes.GameObject
import kotlin.random.Random

class Box(val game: Match, val xPos: Int, val yPos: Int) : Destructable, GameObject(TileType.BOX) {
    override fun destroy() {
        val r = Random.nextInt() % 100
        var type = ""
        game.field[xPos, yPos] = when {
            r <= 5 -> {
                type = "BOMBS"
                BombBonus()
            }
            r <= 10 -> {
                type = "RANGE"
                ExplosionBonus()
            }
            r <= 15 -> {
                type = "SPEED"
                SpeedBonus()
            }
            else -> {
                Floor()
            }
        }
    }

}