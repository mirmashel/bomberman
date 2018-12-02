package io.rybalkinsd.kotlinbootcamp.objects

import io.rybalkinsd.kotlinbootcamp.game.Match
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.Destructable
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.GameObject
import kotlin.random.Random

class Box(val game: Match) : Destructable, GameObject(TileType.BOX) {
    override fun destroy(xPos: Int, yPos: Int) {
        val r = Random.nextInt() % 100
        game.field[xPos, yPos] = when {
            r <= 5 -> BombBonus()
            r <= 10 -> ExplosionBonus()
            r <= 15 -> SpeedBonus()
            else -> Floor()
        }
    }
}