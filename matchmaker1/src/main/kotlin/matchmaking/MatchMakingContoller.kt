package matchmaking

//import org.springframework.http.MediaType
import dao.UserDao
import dao.Users
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import util.logger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque
import javax.annotation.PostConstruct
import db.DbConnector
import model.User
import org.jetbrains.exposed.sql.Op
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.http.MediaType

@Controller
@RequestMapping(
        path = ["/matchmaker"]
)
class MatchMakingContoller {
    val b = DbConnector
    val gamesFor2: ConcurrentLinkedDeque<String> = ConcurrentLinkedDeque()
    val gamesFor4: ConcurrentHashMap<String, Int> = ConcurrentHashMap()
    val log = logger()

//    @PostConstruct
//    fun rel() {
//        ToServer.reload() // при запуске матчмэйкера удалить все игры
//    }

    @RequestMapping(
            path = ["/join"],
            method = [RequestMethod.POST]
    )
    fun join(@RequestParam("name") name: String, @RequestParam("players") players: String): ResponseEntity<String> {
        println("$name $players")
        return when(players) {

            "1" -> joinToGame1(name)
            "2" -> joinToGame2(name)
            "4" -> joinToGame4(name)
            else -> ResponseEntity.badRequest().body("Invalid number of players")
        }
    }

    //: ResponseEntity<String> = joinToGame1(name)

    fun joinToGame1(name: String): ResponseEntity<String> {
        val gameReg = ToServer.create(1)
        log.info("${gameReg.code}")
        return if (gameReg.code != 200) {
            ResponseEntity.badRequest().body("Unable to create game")
        } else {
            ToServer.start(gameReg.body!!)
            ResponseEntity.ok("${gameReg.body}")
        }
    }

    fun joinToGame2(name: String): ResponseEntity<String> = when {
        gamesFor2.isEmpty() -> {
            val gameReq = ToServer.create(2)
            log.info("${gameReq.code}")
            if (gameReq.code != 200)
                ResponseEntity.badRequest().body("Unable to create game")
            else {
               // log.info("Server id: ${gameReq.body}")
                gamesFor2 += gameReq.body
                ResponseEntity.ok("${gameReq.body}")
            }
        }
        else -> {
            val game = gamesFor2.pop()
            log.info(game)
            ToServer.start(game)
            ResponseEntity.ok(game)
        }
    }

    fun joinToGame4(name: String): ResponseEntity<String> = when {
        gamesFor4.isEmpty() -> {
            val gameReq = ToServer.create(4)
            if (gameReq.code != 200)
                ResponseEntity.badRequest().body("Unable to create game")
            else {
                // log.info("Server id: ${gameReq.body}")
                gamesFor4[gameReq.body!!] = 1
                ResponseEntity.ok("${gameReq.body}")
            }
        }
        else -> {
            val game = gamesFor4.keys().nextElement()
            gamesFor4[game] = gamesFor4[game]!! + 1
            if (gamesFor4[game] == 4) {
                ToServer.start(game)
                gamesFor4.remove(game)
            }
            ResponseEntity.ok(game)
        }
    }

    @RequestMapping(
        path = ["/login"],
        method = [RequestMethod.POST],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE]
    )
        fun login(@RequestParam("name") name: String, @RequestParam("password") password: String): ResponseEntity<String> {
        if (name.isEmpty()) return  ResponseEntity.badRequest().body("Name is too short")
        if (name.length > 20) return ResponseEntity.badRequest().body("Name is too long")
        if (password.isEmpty()) return ResponseEntity.badRequest().body("password is too short")
        if (password.length > 20) return ResponseEntity.badRequest().body("password is too long")
        val a = UserDao()
        if (a.getAllWhere(Op.build { Users.login eq name}).isEmpty()) {
            return ResponseEntity.badRequest().body("User with this name doesn't exist")
        }
        var curUsr = a.getAllWhere(Op.build { Users.login eq name})
        if (password != curUsr[0].password) {
            return ResponseEntity.badRequest().body("Invalid password")
        }
        return ResponseEntity.ok(name)
        }

    @RequestMapping(
        path = ["/register"],
        method = [RequestMethod.POST],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE]
    )
    fun register(@RequestParam("name") name: String, @RequestParam("password") password: String): ResponseEntity<String> {
        if (name.isEmpty()) return  ResponseEntity.badRequest().body("Name is too short")
        if (name.length > 20) return ResponseEntity.badRequest().body("Name is too long")
        if (password.isEmpty()) return ResponseEntity.badRequest().body("password is too short")
        if (password.length > 20) return ResponseEntity.badRequest().body("password is too long")

        val usr = User( name, 0, password)
        val a = UserDao()
        if (!a.getAllWhere(Op.build { Users.login eq name}).isEmpty()) {
            return ResponseEntity.badRequest().body("User with this name already exists")
        }
        a.insert(usr)
        return ResponseEntity.ok(name)
    }
}