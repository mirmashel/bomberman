package io.rybalkinsd.kotlinbootcamp.game

import io.rybalkinsd.kotlinbootcamp.util.toJson

data class Cords(val x: Int, val y: Int)
data class Item(val id: Int, val type: String, val position: Cords) {
    fun json() = this.toJson()
}
data class Chel(val id: Int, val type: String, val position: Cords, val alive: Boolean, val direction: String) {
    fun json() = this.toJson()
}
data class Bmb(val id: Int, val type: String, val position: Cords) {
    fun json() = this.toJson()
}