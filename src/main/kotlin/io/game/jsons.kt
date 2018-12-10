package io.game

import io.util.toJson
data class Cords(var x: Int, var y: Int)

data class Chel(val id: Int, val type: String, val position: Cords, var alive: Boolean, var direction: String) {

    fun json() = this.toJson()
}

data class Obj(val id: Int, val type: String, val position: Cords) {
    fun json() = this.toJson()
}

// {"id":3,"type":"Bonus","position":{"y":20,"x":10},"bonusType":"SPEED"}
data class PowerUp(val id: Int, val type: String, val position: Cords, val bonusType: String)