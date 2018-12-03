package io.rybalkinsd.kotlinbootcamp.game

import io.rybalkinsd.kotlinbootcamp.objects.Floor
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.GameObject
import io.rybalkinsd.kotlinbootcamp.objects.TileType
import io.rybalkinsd.kotlinbootcamp.objects.Wall

class GameField(private val length: Int, private val height: Int) {
    var field: Array<Array<GameObject>> = Array(height) {
        Array(length) { GameObject(TileType.BOX) }
    }

    operator fun get(i: Int, j: Int) = field[i][j]

    operator fun set(i: Int, j: Int, value: GameObject) {
        field[i][j] = value
    }

    init {
        val cornerXIndex = arrayOf(1, 2, length - 2, length - 3)
        val cornerYIndex = arrayOf(1, 2, height - 2, height - 3)
        field.forEachIndexed { i, row ->
            row.forEachIndexed { j, _ ->
                if (i == 0 || i == height - 1 || j == 0 || j == length - 1 ||
                    (i % 2 == 0 && j % 2 == 0)
                ) {
                    field[i][j] = Wall()
                } else if (cornerXIndex.contains(i) && cornerYIndex.contains(j)) {
                    field[i][j] = Floor()
                }
            }
        }
    }
}