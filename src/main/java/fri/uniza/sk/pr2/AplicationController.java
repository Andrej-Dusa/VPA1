package fri.uniza.sk.pr2;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AplicationController {
    @GetMapping("/index.html")
    public String home() {
        return "index";
    }

    @GetMapping("/users-profile.html")
    public String user() {
        return "users-profile";
    }
}
