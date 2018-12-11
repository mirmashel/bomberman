package io.game

import io.network.Actions
import io.network.Message
import io.network.Topic
import io.objects.Bomb
import io.objects.Bonus
import io.objects.Box
import io.objects.Fire
import io.objects.Floor
import io.objects.ObjectTypes.GameObject
import io.objects.ObjectTypes.Tickable
import io.objects.Wall
import io.util.logger
import io.util.toJson
import org.springframework.web.socket.WebSocketSession

class Player(val id: Int, val game: Match, val name: String, var xPos: Int, var yPos: Int, val session: WebSocketSession) : Tickable {
    var explosionSize = 1
    var speed = 3
    var maxNumberOfBombs = 1
    var bombsPlanted = 0
    var isAlive = true
        set(value) {
            playerInfo.alive = value
            field = value
        }
    var direction = Actions.IDLE
    var prib = 0

    val xCenter: Int
        get() = xPos + 2 * Match.mult / 5
    val yCenter: Int
        get() = yPos + Match.mult / 3

    val cordsOnFieldX: Int
        get() = xCenter.div(Match.mult)
    val cordsOnFieldY: Int
        get() = yCenter.div(Match.mult)

    fun downBorderX(x: Int): Int = x
    fun leftBorderY(y: Int): Int = y + Match.mult / 8
    fun upBorderX(x: Int): Int = x + Match.mult / 2
    fun rightBorderY(y: Int): Int = y + 2 * Match.mult / 3

    val playerInfo = Chel(id, "Pawn", Cords(yPos, xPos), isAlive, direction.name.substringAfter("MOVE_"))

    fun kill() {
        isAlive = false
        // game.removePlayer(name)
        log.info("Player $name dead")
        game.currentPlayers--
        game.addToOutputQueue(playerInfo.json())
        game.connections.send(session, Message(Topic.DEAD, "").toJson())
        // game.connections.connections.minus(session)
        // session.close()
    }

    fun checkStep(obj: GameObject) =
            obj is Wall || obj is Box || (obj is Bomb && obj.owner != this)

    fun checkFire(obj: GameObject) =
            obj is Fire

    fun updatePos(x: Int, y: Int) {
        xPos = x
        yPos = y
        playerInfo.position.x = y
        playerInfo.position.y = x
    }

    override fun tick(elapsed: Long) {
        var nX: Int = xPos
        var nY: Int = yPos

        if (prib == 0) {
            when (direction) {
                Actions.MOVE_UP -> nX += speed
                Actions.MOVE_LEFT -> nY -= speed
                Actions.MOVE_DOWN -> nX -= speed
                Actions.MOVE_RIGHT -> nY += speed
                else -> {
                }
            }
            prib++
        } else
            prib = (prib + 1) % 3
        val newX1 = downBorderX(nX)
        val newY1 = leftBorderY(nY)
        val newX2 = upBorderX(nX)
        val newY2 = rightBorderY(nY)

        val obj1 = game.field[newX1.div(Match.mult), newY1.div(Match.mult)] // левый нижний
        val obj2 = game.field[newX1.div(Match.mult), newY2.div(Match.mult)] // правый нижний
        val obj3 = game.field[newX2.div(Match.mult), newY1.div(Match.mult)] // левый верхний
        val obj4 = game.field[newX2.div(Match.mult), newY2.div(Match.mult)] // правый верхний

        if (isAlive && (checkFire(obj1) || checkFire(obj2) || checkFire(obj2) || checkFire(obj2))) {
            kill()
        }

        when {
            obj1 is Wall -> {
                when {
                    direction == Actions.MOVE_LEFT && !checkStep(obj3) -> nX++
                    direction == Actions.MOVE_DOWN && !checkStep(obj2) -> nY++
                    else -> {
                        nY = yPos
                        nX = xPos
                    }
                }
                updatePos(nX, nY)
            }
            obj2 is Wall -> {
                when {
                    direction == Actions.MOVE_RIGHT && !checkStep(obj4) -> nX++
                    direction == Actions.MOVE_DOWN && !checkStep(obj1) -> nY--
                    else -> {
                        nY = yPos
                        nX = xPos
                    }
                }
                updatePos(nX, nY)
            }
            obj3 is Wall -> {
                when {
                    direction == Actions.MOVE_LEFT && !checkStep(obj1) -> nX--
                    direction == Actions.MOVE_UP && !checkStep(obj4) -> nY++
                    else -> {
                        nY = yPos
                        nX = xPos
                    }
                }
                updatePos(nX, nY)
            }
            obj4 is Wall -> {
                when {
                    direction == Actions.MOVE_RIGHT && !checkStep(obj2) -> nX--
                    direction == Actions.MOVE_UP && !checkStep(obj3) -> nY--
                    else -> {
                        nY = yPos
                        nX = xPos
                    }
                }
                updatePos(nX, nY)
            }
        }

        if (direction.name != "IDLE" && !checkStep(obj1) && !checkStep(obj2) && !checkStep(obj3) && !checkStep(obj4))
            updatePos(nX, nY)
        send(direction.name.substringAfter("MOVE_"))
        if (isAlive)
            when {
                obj1 is Bonus -> obj1.pickUp(this)
                obj2 is Bonus -> obj2.pickUp(this)
                obj3 is Bonus -> obj3.pickUp(this)
                obj4 is Bonus -> obj4.pickUp(this)
            }
        direction = Actions.IDLE
    }

    private fun send(act: String) {
        playerInfo.direction = act
        game.addToOutputQueue(playerInfo.json())
    }

    fun move(a: Actions) {
        direction = a
    }

    fun plantBomb() {
        if (game.field[cordsOnFieldX, cordsOnFieldY] is Floor &&
                bombsPlanted < maxNumberOfBombs && isAlive) {
            bombsPlanted++
            val b = Bomb(this, game, xCenter, yCenter)
            game.field[cordsOnFieldX, cordsOnFieldY] = b
            game.tickables.registerTickable(b)
        }
    }

    companion object {
        val log = logger()
    }
}