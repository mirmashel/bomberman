import io.game.Match
import org.junit.Test

class Test {

    @Test
    fun test() {
        val a = Match("1", 2)
        a.addPlayer("aa")
        a.addPlayer("bb")
        a.players.forEach { println(it) }
        a.start()
    }
}