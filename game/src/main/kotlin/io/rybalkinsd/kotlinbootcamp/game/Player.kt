package io.rybalkinsd.kotlinbootcamp.game

import io.rybalkinsd.kotlinbootcamp.network.Actions
import io.rybalkinsd.kotlinbootcamp.network.Topic
import io.rybalkinsd.kotlinbootcamp.objects.Bomb
import io.rybalkinsd.kotlinbootcamp.objects.Box
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.Bonus
import io.rybalkinsd.kotlinbootcamp.objects.TileType
import io.rybalkinsd.kotlinbootcamp.objects.Wall

class Player(val id: Int, val game: Match, val name: String, var xPos: Int, var yPos: Int) {
    var explosionSize = 1
    var speed = 1
    var maxNumberOfBombs = 1
    var bombsPlanted = 0
    var isAlive = true

    fun kill() {
        isAlive = false
        game.removePlayer(name)
        game.addToOutputQueue(Topic.MOVE,
                "\"type\":\"Pawn\",\"position\":{\"y\":$yPos,\"x\":$xPos},\"alive\":$isAlive,\"direction\":\"")
    }

    fun move(a: Actions) {
        var newX = xPos
        var newY = yPos
        when (a) {
            Actions.MOVE_UP -> newX++
            Actions.MOVE_LEFT -> newY--
            Actions.MOVE_DOWN -> newX--
            Actions.MOVE_RIGHT -> newY++
            else -> {
            }
        }
        if (!game.field[newX / Match.mult, newY / Match.mult].isImpassable()) {
            xPos = newX
            yPos = newY
         //   game.addToOutputQueue(Topic.MOVE,
                    //        "\"type\":\"Pawn\",\"position\":{\"y\":$yPos,\"x\":$xPos},\"alive\":$isAlive,\"direction\":\"$a")
        }
        if (game.field[xPos / Match.mult, yPos / Match.mult].isBonus()) {
            (game.field[xPos, yPos] as Bonus).pickUp(this)
        }

    }
    fun plantBomb() {
        if (bombsPlanted < maxNumberOfBombs) {
            game.field[xPos, yPos] = Bomb(this, game, xPos, yPos)
            game.tickables.registerTickable(Bomb(this, game, xPos, yPos))

// {"id":1,"type":"Bomb","position":{"y":20,"x":10}}
            game.addToOutputQueue(Topic.PLANT_BOMB, Bmb(Match.ids++, "Bomb", Cords(xPos / Match.mult, yPos / Match.mult)).json())
        }
    }
}