package gameservice

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping (
    path = ["/game"]
)
class ServiceController {
    @RequestMapping (
        path = ["/create"],
        method = [RequestMethod.POST]
    )
    fun create() {

    }

    @RequestMapping (
        path = ["/start"],
        method = [RequestMethod.POST]
    )
    fun start(@RequestParam("gameId") id: String) {

    }
}