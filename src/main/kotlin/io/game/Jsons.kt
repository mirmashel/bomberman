package io.game

import io.util.toJson

data class Cords(var x: Int, var y: Int)

// for sending pawns in JSON
data class Chel(val id: Int, val type: String, val position: Cords, var alive: Boolean, var direction: String) {
    fun json() = this.toJson()
}

// for sending objects in JSON
data class Obj(val id: Int, val type: String, val position: Cords) {
    fun json() = this.toJson()
}

// for sending bonuses in JSON
data class PowerUp(val id: Int, val type: String, val position: Cords, val bonusType: String)