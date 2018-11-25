package io.rybalkinsd.kotlinbootcamp.game

import io.rybalkinsd.kotlinbootcamp.objects.TileType

class GameField(private val length: Int, private val height: Int) {
    var field: Array<Array<TileType>> = Array(height) {
        Array(length) { TileType.BOX }
    }

    operator fun get(i: Int, j: Int) = field[i][j]

    operator fun set(i: Int, j: Int, value: TileType) {
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
                    field[i][j] = TileType.WALL
                } else if (cornerXIndex.contains(i) && cornerYIndex.contains(j)) {
                    field[i][j] = TileType.FLOOR
                }
            }
        }
    }
}