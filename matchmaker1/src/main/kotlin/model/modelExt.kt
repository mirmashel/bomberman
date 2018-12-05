package model

import dao.Users
import org.jetbrains.exposed.sql.ResultRow

internal fun ResultRow.toUser() = User(this[Users.login], this[Users.score], this[Users.password])