package io.rybalkinsd.kotlinbootcamp.objects

import io.rybalkinsd.kotlinbootcamp.game.GameField
import io.rybalkinsd.kotlinbootcamp.game.Player
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.Destructable
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.GameObject
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.Tickable

class Bomb(val owner: Player): Tickable, Destructable, GameObject(TileType.BOMB) {
    override fun destroy(field: GameField, xPos: Int, yPos: Int) {
        field[xPos, yPos] = TileType.FLOOR
        for (i in 1..owner.explosionSize) {
            field[xPos - i, yPos] = TileType.FLOOR
            field[xPos + i, yPos] = TileType.FLOOR
            field[xPos, yPos - i] = TileType.FLOOR
            field[xPos, yPos + i] = TileType.FLOOR
        }
    }

    override fun tick(elapsed: Long) {
        TODO("not implemented")
    }
}