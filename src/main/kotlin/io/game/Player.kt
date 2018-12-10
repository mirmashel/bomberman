package io.game

import io.network.Actions
import io.network.Topic
import io.objects.Bomb
import io.objects.Box
import io.objects.Floor
import io.objects.ObjectTypes.Bonus
import io.objects.ObjectTypes.GameObject
import io.objects.ObjectTypes.Tickable
import io.objects.Wall
import io.util.logger
import io.util.toJson

class Player(val id: Int, val game: Match, val name: String, var xPos: Int, var yPos: Int) : Tickable {
    var explosionSize = 1
    var speed = 2
    var maxNumberOfBombs = 1
    var bombsPlanted = 0
    var isAlive = true
    private var idleCounter = 0
    var direction = Actions.IDLE
    var prib = true

    fun kill() {
        isAlive = false
        game.removePlayer(name)
        // game.addToOutputQueue(Topic.MOVE,
        //        "\"type\":\"Pawn\",\"position\":{\"y\":$yPos,\"x\":$xPos},\"alive\":$isAlive,\"direction\":\"")
    }

    fun check_step(obj: GameObject) =
        !(obj is Wall || obj is Box || (obj is Bomb && obj.owner != this))



    override fun tick(elapsed: Long) {
        var newX1 = xPos
        var newY1 = yPos

        if (prib) {
            when (direction) {
                Actions.MOVE_UP -> newX1 += speed
                Actions.MOVE_LEFT -> newY1 -= speed
                Actions.MOVE_DOWN -> newX1 -= speed
                Actions.MOVE_RIGHT -> newY1 +=  speed
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
            prib = false
        } else
            prib = true
        val newX2 = newX1 + Match.mult / 2
        val newY2 = newY1 + Match.mult / 2
        idleCounter = 0
        val obj1 = check_step(game.field[newX1.div(Match.mult), newY1.div(Match.mult)])// левый нижний
        val obj2 = check_step(game.field[newX1.div(Match.mult), newY2.div(Match.mult)])// правый нижний
        val obj3 = check_step(game.field[newX2.div(Match.mult), newY1.div(Match.mult)])// левый верхний
        val obj4 = check_step(game.field[newX2.div(Match.mult), newY2.div(Match.mult)])// правый верхний
        if (obj1 && obj2 && obj3 && obj4) {
            xPos = newX1
            yPos = newY1
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
            val newY = yPos.div(Match.mult)
            val newX = xPos.div(Match.mult)
            val b = Bomb(this, game, xPos, yPos)
            game.field[xPos / Match.mult, yPos / Match.mult] = b
            game.tickables.registerTickable(b)
            logger().info("Bomb id: ${b.id} planted")

            game.addToOutputQueue(Bmb(b.id, "Bomb", Cords(newY * Match.mult, newX * Match.mult)).json())
        }
    }

    companion object {
        const val maxIdleTick = Ticker.FPS / 60
        val log = logger()
    }
}