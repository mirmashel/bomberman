package dao

import model.User
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import model.toUser

class UserDao : Dao<User> {
    override val all: List<User>
    get() = transaction {
        Users.selectAll()
            .toList()
            .map(ResultRow::toUser)
    }
    override fun getAllWhere(vararg where: Op<Boolean>) = transaction {
        Users.select(where.reduce { resultExpr, condition -> resultExpr and condition })
            .toList()
            .map(ResultRow::toUser)
    }
    override fun insert(t: User) {
        transaction {
            Users.insert {
                it[login] = t.login
                it[score] = t.score
                it[password] = t.password
            }
        }
    }
}
