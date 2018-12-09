package io.objects

import io.game.Player
import io.objects.ObjectTypes.Bonus
import io.objects.ObjectTypes.GameObject

class BombBonus : GameObject(TileType.BOMB_BONUS), Bonus {
    override fun pickUp(p: Player) = p.maxNumberOfBombs++
}

class SpeedBonus : GameObject(TileType.SPEED_BONUS), Bonus {
    override fun pickUp(p: Player) = p.speed++
}

class ExplosionBonus : GameObject(TileType.EXPLOSION_BONUS), Bonus {
    override fun pickUp(p: Player) = p.explosionSize++
}