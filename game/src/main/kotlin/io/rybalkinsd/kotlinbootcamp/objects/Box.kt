package io.rybalkinsd.kotlinbootcamp.objects

import io.rybalkinsd.kotlinbootcamp.game.Match
import io.rybalkinsd.kotlinbootcamp.network.Topic
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.Destructable
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.GameObject
import kotlin.random.Random

class Box(val game: Match) : Destructable, GameObject(TileType.BOX) {
    override fun destroy(xPos: Int, yPos: Int) {
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
                game.addToOutputQueue(Topic.REPLICA,
                        "\"type\":\"Floor\",\"position\":{\"y\":$yPos,\"x\":$xPos\")")
                Floor()
            }
        }
        game.addToOutputQueue(Topic.REPLICA,
                "\"type\":\"Bonus\",\"position\":{\"y\":$yPos,\"x\":$xPos},\"bonusType\":\"$type\"")
    }

}