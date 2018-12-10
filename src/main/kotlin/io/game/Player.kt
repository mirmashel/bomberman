package io.game

import io.network.Actions
import io.objects.*
import io.objects.ObjectTypes.GameObject
import io.objects.ObjectTypes.Tickable
import io.util.logger
import org.springframework.web.socket.WebSocketSession

class Player(val id: Int, val game: Match, val name: String, var xPos: Int, var yPos: Int, val session: WebSocketSession) : Tickable {
    var explosionSize = 1
    var speed = 2
    var maxNumberOfBombs = 1
    var bombsPlanted = 0
    var isAlive = true
        set(value) {
            playerInfo.alive = value
            field = value
        }
    private var idleCounter = 0
    var direction = Actions.IDLE
    var prib = true

    val xCentrer: Int
        get() = xPos + 2 * Match.mult / 5
    val yCentrer: Int
        get() = yPos + Match.mult / 3

    fun downBorderX(x: Int): Int = x
    fun leftBorderY(y: Int): Int = y + Match.mult / 8
    fun upBorderX(x: Int): Int = x + Match.mult / 2
    fun rightBorderY(y: Int): Int = y + 2 * Match.mult / 3

    val playerInfo = Chel(id, "Pawn", Cords(yPos, xPos), isAlive, direction.name.substringAfter("MOVE_"))

    fun kill() {
        isAlive = false
        //game.removePlayer(name)
        log.info("Player $name dead")
        game.addToOutputQueue(playerInfo.json())
       // game.connections.connections.minus(session)
       // session.close()
    }

    fun checkStep(obj: GameObject) =
            !(obj is Wall || obj is Box || (obj is Bomb && obj.owner != this))

    fun checkFire(obj: GameObject) =
            obj is Fire


    override fun tick(elapsed: Long) {
        var nX: Int = xPos
        var nY: Int = yPos

        if (prib) {
            when (direction) {
                Actions.MOVE_UP -> nX += speed
                Actions.MOVE_LEFT -> nY -= speed
                Actions.MOVE_DOWN -> nX -= speed
                Actions.MOVE_RIGHT -> nY +=  speed
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
        val newX1 = downBorderX(nX)
        val newY1 = leftBorderY(nY)
        val newX2 = upBorderX(nX)
        val newY2 = rightBorderY(nY)
        idleCounter = 0

        var obj1 = game.field[newX1.div(Match.mult), newY1.div(Match.mult)]// левый нижний
        var obj2 = game.field[newX1.div(Match.mult), newY2.div(Match.mult)]// правый нижний
        var obj3 = game.field[newX2.div(Match.mult), newY1.div(Match.mult)]// левый верхний
        var obj4 = game.field[newX2.div(Match.mult), newY2.div(Match.mult)]//\

        if (checkFire(obj1) || checkFire(obj2) || checkFire(obj2) || checkFire(obj2)) {
            kill()
        }
        if (direction.name != "IDLE" && checkStep(obj1) && checkStep(obj2) && checkStep(obj3) && checkStep(obj4)) {
            xPos = nX
            yPos = nY
            playerInfo.position.x = nY
            playerInfo.position.y = nX
        }
        send(direction.name.substringAfter("MOVE_"))
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
        if (game.field[xPos / Match.mult, yPos / Match.mult] is Floor &&
                bombsPlanted < maxNumberOfBombs && isAlive) {
            bombsPlanted++
            val b = Bomb(this, game, xCentrer, yCentrer)
            game.field[xCentrer / Match.mult, yCentrer / Match.mult] = b
            game.tickables.registerTickable(b)
            logger().info("Bomb id: ${b.id} planted")
        }
    }

    companion object {
        const val maxIdleTick = Ticker.FPS / 30
        val log = logger()
    }
}