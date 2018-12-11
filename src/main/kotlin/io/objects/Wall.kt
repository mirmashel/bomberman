package io.objects

import io.game.Match
import io.objects.ObjectTypes.GameObject

class Wall(game: Match) : GameObject(TileType.WALL, game.ids++)