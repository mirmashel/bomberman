package io.game

import io.network.Actions
import io.network.Topic
import io.objects.Bomb
import io.objects.Box
import io.objects.Floor
import io.objects.ObjectTypes.Bonus
import io.objects.ObjectTypes.Tickable
import io.objects.Wall
import io.util.logger
import io.util.toJson

class Player(val id: Int, val game: Match, val name: String, var xPos: Int, var yPos: Int) : Tickable {
    var explosionSize = 1
    var speed = 1
    var maxNumberOfBombs = 1
    var bombsPlanted = 0
    var isAlive = true
    private var idleCounter = 0
    var direction = Actions.IDLE

    fun kill() {
        isAlive = false
        game.removePlayer(name)
        // game.addToOutputQueue(Topic.MOVE,
        //        "\"type\":\"Pawn\",\"position\":{\"y\":$yPos,\"x\":$xPos},\"alive\":$isAlive,\"direction\":\"")
    }

    override fun tick(elapsed: Long) {
        var newX = xPos
        var newY = yPos
        when (direction) {
            Actions.MOVE_UP -> newX++
            Actions.MOVE_LEFT -> newY--
            Actions.MOVE_DOWN -> newX--
            Actions.MOVE_RIGHT -> newY++
            else -> {
                send("IDLE")
                idleCounter++
                if (idleCounter >= maxIdleTick) {
                    direction = Actions.IDLE
                } else {
                    return
                }
            }
        }
        idleCounter = 0
        val obj = game.field[newX / Match.mult, newY / Match.mult]
        //logger().info("x = $newX, y = $newY obj = ${obj.t}")
        if (!(obj is Wall || obj is Box || (obj is Bomb && obj.owner != this))) {
            xPos = newX
            yPos = newY
        }
        send(direction.name.substringAfter("MOVE_"))
        if (game.field[xPos / Match.mult, yPos / Match.mult].isBonus()) {
            (game.field[xPos, yPos] as Bonus).pickUp(this)
        }
        direction = Actions.IDLE
    }

    private fun send(act: String) {
        val chel = Chel(id, "Pawn", Cords(yPos, xPos), isAlive, act)
        game.addToOutputQueue(chel.toJson())
    }

    fun move(a: Actions) {
        direction = a
    }

    fun plantBomb() {
        if (game.field[xPos / Match.mult, yPos / Match.mult] is Floor &&
                bombsPlanted < maxNumberOfBombs) {
            bombsPlanted++
            val b = Bomb(this, game, xPos, yPos)
            game.field[xPos / Match.mult, yPos / Match.mult] = b
            game.tickables.registerTickable(b)
            logger().info("Bomb id: ${b.id} planted")
// {"id":1,"type":"Bomb","position":{"y":20,"x":10}}
            val newY = yPos.div(Match.mult)
            val newX = xPos.div(Match.mult)
            game.addToOutputQueue(Obj(b.id, "Bomb", Cords(newY * Match.mult, newX * Match.mult)).json())
        }
    }

    companion object {
        const val maxIdleTick = Ticker.FPS / 60
    }
}