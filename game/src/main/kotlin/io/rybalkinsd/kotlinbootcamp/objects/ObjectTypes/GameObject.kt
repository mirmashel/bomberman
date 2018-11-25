package io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes

import io.rybalkinsd.kotlinbootcamp.objects.TileType

open class GameObject (val t: TileType)  {
    fun getType() = t
}