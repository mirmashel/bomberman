package io.rybalkinsd.kotlinbootcamp.game

import io.rybalkinsd.kotlinbootcamp.objects.Bomb
import io.rybalkinsd.kotlinbootcamp.objects.Box
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.Bonus
import io.rybalkinsd.kotlinbootcamp.objects.TileType
import io.rybalkinsd.kotlinbootcamp.objects.Wall

class Player(val game: Match, val name: String, var xPos: Int, var yPos: Int) {
    var explosionSize = 1
    var speed = 1
    var maxNumberOfBombs = 1
    var bombsPlanted = 0
    private var isAlive = 1

    fun kill() {
        isAlive = 0
        game.removePlayer(name)
    }

    fun moveUp() {
        if (!game.field[xPos, yPos + 1].isImpassable()) {
            yPos++
        }
        if (game.field[xPos, yPos].isBonus()) {
            (game.field[xPos, yPos] as Bonus).pickUp(this)
        }
    }

    fun moveDown() {
        if (!game.field[xPos, yPos - 1].isImpassable()) {
            yPos--
        }
        if (game.field[xPos, yPos].isBonus()) {
            (game.field[xPos, yPos] as Bonus).pickUp(this)
        }
    }

    fun moveLeft() {
        if (!game.field[xPos + 1, yPos].isImpassable()) {
            xPos++
        }
        if (game.field[xPos, yPos].isBonus()) {
            (game.field[xPos, yPos] as Bonus).pickUp(this)
        }
    }

    fun moveRight() {
        if (!game.field[xPos - 1, yPos].isImpassable()) {
            xPos--
        }
        if (game.field[xPos, yPos].isBonus()) {
            (game.field[xPos, yPos] as Bonus).pickUp(this)
        }
    }

    fun plantBomb() {
        if (bombsPlanted < maxNumberOfBombs) {
            game.field[xPos, yPos] = Bomb(this, game, xPos, yPos)
            game.tickables.registerTickable(Bomb(this, game, xPos, yPos))
        }
    }
}