package web.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.Hidden;

/**
 * Home page controller.
 *
 * @author Paul Chapman
 */
@Controller
@Hidden // Oculta este endpoint en Swagger (es solo HTML, no API REST)
public class HomeController {

    @RequestMapping("/")
    public String home() {
        return "index";
    }

}
