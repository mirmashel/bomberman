package io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes

import io.rybalkinsd.kotlinbootcamp.game.GameField
import io.rybalkinsd.kotlinbootcamp.game.Player

interface Bonus {
    fun pickUp(p: Player): Int
}