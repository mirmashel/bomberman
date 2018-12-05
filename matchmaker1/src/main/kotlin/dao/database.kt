package dao

import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val id = integer("id").primaryKey()
    val password = text("password")
    val login = text("login")
    var score = integer("score")
}
