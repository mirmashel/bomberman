package io.game

import io.util.toJson

data class Cords(var x: Int, var y: Int)
data class Item(val id: Int, val type: String, val position: Cords) {
    fun json() = this.toJson()
}
data class Chel(val id: Int, val type: String, val position: Cords, var alive: Boolean, var direction: String) {
    fun json() = this.toJson()
}
data class Obj(val id: Int, val type: String, val position: Cords) {
    fun json() = this.toJson()
}