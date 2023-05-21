package fri.uniza.sk.pr2;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Controller
public class UserController {
    private final UserService userService;
    private final TaskRepository taskRepository;

    public UserController(UserService userService, TaskRepository taskRepository) {
        this.userService = userService;
        this.taskRepository = taskRepository;
    }

    @GetMapping("/users.html")
    public String showUserList(Model model) {
        List<User> listOfUsers = userService.getAll();
        model.addAttribute("listOfUsers", listOfUsers);
        return "users";
    }

    @GetMapping("/add-user.html")
    public String addUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("title", "Create an Account");
        model.addAttribute("submit", "Create an Account");
        return "add-user";
    }

    @PostMapping("/save-user")
    public String saveUser(User user, RedirectAttributes ra) {
        userService.save(user);
        ra.addFlashAttribute("message", "The account has been saved successfully.");
        return "redirect:/users.html";
    }

    @GetMapping("/edit-user/{id}")
    public String editUser(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        try {
            User user = userService.get(id);
            model.addAttribute("user", user);
            model.addAttribute("title", "Edit an account (Id: " + id + ")");
            model.addAttribute("submit", "Edit an account");
            return "add-user";
        } catch (UserNotFoundException e) {
            ra.addFlashAttribute("message", "The account has been saved successfully.");
            return "redirect:/users.html";
        }
    }

    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            userService.delete(id);
            ra.addFlashAttribute("message", "The account was deleted successfully.");
            return "redirect:/users.html";
        } catch (UserNotFoundException e) {
            return "redirect:/users.html";
        }
    }

    @GetMapping("/login.html")
    public String login(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/afterLogin")
    public String afterLogin(User user, RedirectAttributes ra) {
        try {
            user = userService.login(user.getName(), user.getPassword());
            ra.addFlashAttribute("user.id", user.getId());
            return "redirect:/" + user.getId();
        } catch (UserNotFoundException e) {
            ra.addFlashAttribute("message", "Account with this username or password does not exist.");
            return "redirect:/login.html";
        }
    }

    @GetMapping("/{id}")
    public String showTaskList(@PathVariable("id") Long id, Model model) {
        try {
            User user = userService.get(id);
            List<Task> listOfTasks = taskRepository.findAllByUser(user);
            model.addAttribute("listOfTasks", listOfTasks);
            model.addAttribute("id", user.getId());

            return "index";
        } catch (UserNotFoundException e) {
            return "redirect:/login.html";
        }
    }

    @GetMapping("/add-task.html")
    public String addTask(@RequestParam("userId") Long userId, Model model) throws UserNotFoundException {
        Task task = new Task();
        User user = userService.get(userId);
        task.setUser(user);
        model.addAttribute("task", task);
        model.addAttribute("title", "Create Task");
        model.addAttribute("submit", "Create Task");
        return "add-task";
    }

    @PostMapping("/save-task")
    public String saveTask(Task task, @RequestParam("status") String status,
                           @RequestParam("finishDate") String finishDate, RedirectAttributes ra) {
        task.setFinishDate(finishDate);
        task.setStatus(Status.valueOf(status));
        taskRepository.save(task);
        ra.addFlashAttribute("message", "The task was saved successfully.");
        return "redirect:/" + task.getUser().getId();
    }

}
