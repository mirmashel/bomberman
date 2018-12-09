package io.objects.ObjectTypes

import io.game.Player

interface Bonus {
    fun pickUp(p: Player): Int
}