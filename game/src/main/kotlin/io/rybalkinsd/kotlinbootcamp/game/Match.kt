package io.rybalkinsd.kotlinbootcamp.game

import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.GameObject

class Match(val id: String, val names: List<String>) {
    var field = GameField(13, 17)
}