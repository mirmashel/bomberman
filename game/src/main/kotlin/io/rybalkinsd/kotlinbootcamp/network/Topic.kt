package io.rybalkinsd.kotlinbootcamp.network

enum class Topic {
    HELLO,
    MOVE,
    PLANT_BOMB,
    REPLICA,
    START,
    END_MATCH,
    POSSESS,
    FINISH
}



data class Action(var topic: Topic, var data: Direction)