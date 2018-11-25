package io.rybalkinsd.kotlinbootcamp.objects

import io.rybalkinsd.kotlinbootcamp.game.GameField
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.Destructable
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.GameObject

class Box: Destructable, GameObject(TileType.BOX) {
    override fun destroy(field: GameField, xPos: Int, yPos: Int) {
        field[xPos, yPos] = TileType.FLOOR
    }

}