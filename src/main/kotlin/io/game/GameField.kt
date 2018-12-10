package io.game

import io.objects.Box
import io.objects.Floor
import io.objects.ObjectTypes.GameObject
import io.objects.TileType
import io.objects.Wall

class GameField(val game: Match, private val length: Int, private val height: Int) {
    var field: Array<Array<GameObject>> = Array(length) {
        Array(height) { GameObject(TileType.BOX) }
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
                if (i == 0 || i == length - 1 || j == 0 || j == height - 1 || (i % 2 == 0 && j % 2 == 0)) {
                    field[i][j] = Wall()
                } else if (cornerXIndex.contains(i) && cornerYIndex.contains(j)) {
                    field[i][j] = Floor()
                } else {
                    field[i][j] = Box(game, i, j)
                }
            }
        }
    }
}