package io.objects.ObjectTypes

import io.game.Match
import io.objects.TileType

open class GameObject(val t: TileType) {
    val id = Match.ids++
}