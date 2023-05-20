package fri.uniza.sk.pr2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/listOfUsers.html")
    public String showUserList(Model model) {
        List<User> listOfUsers = userService.getAll();
        model.addAttribute("listOfUsers", listOfUsers);
        return "listOfUsers";
    }
}
