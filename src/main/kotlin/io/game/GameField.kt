package io.game

import io.objects.Box
import io.objects.Floor
import io.objects.ObjectTypes.GameObject
import io.objects.Wall
import java.lang.Math.abs
import kotlin.random.Random

class GameField(val game: Match, private val length: Int, private val height: Int) {
    var field: Array<Array<GameObject>> = Array(length) {
        Array(height) { GameObject(-1) } // objects will be replaced, no need for id
    }

    operator fun get(i: Int, j: Int) = field[i][j]

    operator fun set(i: Int, j: Int, value: GameObject) {
        field[i][j] = value
    }

    init {
        val cornerXIndex = arrayOf(1, 2, length - 2, length - 3)
        val cornerYIndex = arrayOf(1, 2, height - 2, height - 3)
        // creating field with empty corners to spawn players
        field.forEachIndexed { i, row ->
            row.forEachIndexed { j, _ ->
                if (i == 0 || i == length - 1 || j == 0 || j == height - 1 ||
                        (i % 2 == 0 && j % 2 == 0)) {
                    field[i][j] = Wall(game)
                } else if (cornerXIndex.contains(i) && cornerYIndex.contains(j)) {
                    field[i][j] = Floor()
                } else {
                    field[i][j] = Box(game, i, j)
                }
            }
        }
    }

    fun getRandomEmptyPos(): Cords {
        val emptyTiles = emptyList<Cords>().toMutableList()
        field.forEachIndexed { i, arr ->
            arr.forEachIndexed { j, obj ->
                if (obj is Floor) {
                    emptyTiles += Cords(i, j)
                }
            }
        }
        return emptyTiles[abs(Random.nextInt() % emptyTiles.size)]
    }
}