package db

import util.logger
import org.jetbrains.exposed.sql.Database
import java.lang.Exception

object DbConnector {
    private val log = logger()

    private val host = "54.224.37.210"
    private val port = 5432
    private val dbName = "chatdb_atom8"
    private val user = "atom8"
    private val password = "atom8"

    init {
        try {
            Database.connect(
                url = "jdbc:postgresql://$host:$port/$dbName",
                driver = "org.postgresql.Driver",
                user = user,
                password = password
            )
        } catch (e: Exception) {
            log.info(e.toString())
        }

        log.info("Success. DbConnector init.")
    }
}