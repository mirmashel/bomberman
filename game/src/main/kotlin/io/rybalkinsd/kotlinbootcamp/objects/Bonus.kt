package io.rybalkinsd.kotlinbootcamp.objects

import io.rybalkinsd.kotlinbootcamp.game.Player
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.Bonus
import io.rybalkinsd.kotlinbootcamp.objects.ObjectTypes.GameObject

class BombBonus : GameObject(TileType.BOMB_BONUS), Bonus {
    override fun pickUp(p: Player) = p.maxNumberOfBombs++
}

class SpeedBonus : GameObject(TileType.SPEED_BONUS), Bonus {
    override fun pickUp(p: Player) = p.speed++
}

class ExplosionBonus : GameObject(TileType.EXPLOSION_BONUS), Bonus {
    override fun pickUp(p: Player) = p.explosionSize++
}