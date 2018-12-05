package io.rybalkinsd.kotlinbootcamp.game

import io.rybalkinsd.kotlinbootcamp.network.Actions
import io.rybalkinsd.kotlinbootcamp.network.Topic
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
        game.addToOutputQueue(Topic.MOVE,
                "\"type\":\"Pawn\",\"position\":{\"y\":$yPos,\"x\":$xPos},\"alive\":$isAlive,\"direction\":\"")
    }

    fun move(a: Actions) {
        var newX = xPos
        var newY = yPos
        when (a) {
            Actions.MOVE_UP -> newY++
            Actions.MOVE_LEFT -> newX--
            Actions.MOVE_DOWN -> newY++
            Actions.MOVE_RIGHT -> newX++
            else -> {
            }
        }
        if (!game.field[newX, newY].isImpassable()) {
            xPos = newX
            yPos = newY
            game.addToOutputQueue(Topic.MOVE,
                    "\"type\":\"Pawn\",\"position\":{\"y\":$yPos,\"x\":$xPos},\"alive\":$isAlive,\"direction\":\"$a")
        }
        if (game.field[xPos, yPos].isBonus()) {
            (game.field[xPos, yPos] as Bonus).pickUp(this)
        }

    }

    fun plantBomb() {
        if (bombsPlanted < maxNumberOfBombs) {
            game.field[xPos, yPos] = Bomb(this, game, xPos, yPos)
            game.tickables.registerTickable(Bomb(this, game, xPos, yPos))
            game.addToOutputQueue(Topic.PLANT_BOMB,
                    "\"type\":\"Bomb\",\"position\":{\"y\":$yPos,\"x\":$xPos}")
        }
    }
}