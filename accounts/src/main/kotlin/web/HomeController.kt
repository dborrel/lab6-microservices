package accounts.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import io.swagger.v3.oas.annotations.Hidden;

/**
 * Home page controller.
 *
 * @author Paul Chapman
 */
@Controller
@Hidden // Oculta este endpoint en la documentación Swagger (es solo una página HTML)
class HomeController {
    @RequestMapping("/")
    fun home(): String {
        return "index"
    }
}