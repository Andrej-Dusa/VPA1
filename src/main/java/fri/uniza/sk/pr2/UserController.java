package fri.uniza.sk.pr2;

import jakarta.servlet.http.HttpSession;
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
    private final TaskService taskService;

    public UserController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    @GetMapping("/users.html")
    public String showUserList(Model model, HttpSession session) {
        List<User> listOfUsers = userService.getAll();
        User user = (User)session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("listOfUsers", listOfUsers);
        return "users";
    }

    @GetMapping("/add-user.html")
    public String addUser(Model model, HttpSession session) {
        User user1 = (User) session.getAttribute("user");
        model.addAttribute("user", new User());
        model.addAttribute("user1", user1);
        model.addAttribute("title", "Create an Account");
        model.addAttribute("submit", "Create an Account");
        return "add-user";
    }

    @PostMapping("/save-user")
    public String saveUser(User user, RedirectAttributes ra, HttpSession session) {
        userService.save(user);
        ra.addFlashAttribute("message", "The account has been saved successfully.");
        User user1 = (User) session.getAttribute("user");
        if(user1 == null) {
            return "redirect:/login.html";
        }
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
    public String afterLogin(User user, RedirectAttributes ra, HttpSession session) {
        try {
            user = userService.login(user.getName(), user.getPassword());
            session.setAttribute("user", user);
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
            List<Task> listOfTasks = taskService.getAllByUser(user);
            model.addAttribute("listOfTasks", listOfTasks);
            model.addAttribute("user", user);
            return "index";
        } catch (UserNotFoundException e) {
            return "redirect:/login.html";
        } catch (TaskNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/add-task.html")
    public String addTask(@RequestParam("userId") Long userId, Model model) throws UserNotFoundException {
        Task task = new Task();
        User user = userService.get(userId);
        task.setUser(user);
        task.setStatus(Status.OPEN);
        model.addAttribute("task", task);
        model.addAttribute("title", "Create Task");
        model.addAttribute("submit", "Create Task");
        return "add-task";
    }

    @PostMapping("/save-task")
    public String saveTask(Task task, @RequestParam("status") String status,
                           @RequestParam("finishDate") String finishDate, RedirectAttributes ra
                            , HttpSession session) {
        task.setFinishDate(finishDate);
        task.setStatus(Status.valueOf(status));
        taskService.save(task);
        ra.addFlashAttribute("message", "The task was saved successfully.");
        User user = (User) session.getAttribute("user");
        return "redirect:/" + user.getId();
    }

    @GetMapping("/edit-task/{id}")
    public String editTask(@PathVariable("id") Long id, Model model, RedirectAttributes ra
            , HttpSession session) throws TaskNotFoundException {
        try {
            Task task = taskService.get(id);
            model.addAttribute("task", task);
            model.addAttribute("title", "Edit the task (Id: " + id + ")");
            model.addAttribute("submit", "Edit the task");
            ra.addFlashAttribute("message", "The task has been saved successfully.");
            return "/add-task";
        } catch (TaskNotFoundException e) {
            Task task = taskService.get(id);
            User user = (User) session.getAttribute("user");
            return "redirect:/" + user.getId();
        }
    }

    @GetMapping("/delete-task/{id}")
    public String deleteTask(@PathVariable("id") Long id, RedirectAttributes ra, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            taskService.delete(id);
            ra.addFlashAttribute("message", "The task was deleted successfully.");
            return "redirect:/" + user.getId();
        } catch (TaskNotFoundException e) {
            return "redirect:/";
        }
    }

    @GetMapping("/sign-out")
    public String signOut(HttpSession session) {
        session.invalidate();
        return "redirect:/login.html";
    }

    @GetMapping("/assign-task.html")
    public String showTaskList(Model model, HttpSession session) {
        List<Task> listOfTasks = taskService.getAll();
        List<User> listOfUsers = userService.getAll();
        model.addAttribute("listOfUsers", listOfUsers);
        model.addAttribute("listOfTasks", listOfTasks);
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        return "assign-task";
    }

    @GetMapping("/assign-task-to.html")
    public String assignTask(Model model) {
        List<User> listOfUsers = userService.getAll();
        Task task = new Task();
        task.setStatus(Status.OPEN);
        model.addAttribute("task", task);
        model.addAttribute("title", "Assign Task");
        model.addAttribute("submit", "Assign Task");
        model.addAttribute("listOfUsers", listOfUsers);
        return "assign-task-to";
    }

    @GetMapping("/edit-assign-task/{id}")
    public String editAssignTask(@PathVariable("id") Long id, Model model, RedirectAttributes ra
            , HttpSession session) throws TaskNotFoundException {
        try {
            Task task = taskService.get(id);
            List<User> listOfUsers = userService.getAll();
            model.addAttribute("task", task);
            model.addAttribute("listOfUsers", listOfUsers);
            model.addAttribute("title", "Edit assigned the task (Id: " + id + ")");
            model.addAttribute("submit", "Edit assigned the task");
            ra.addFlashAttribute("message", "The task has been saved successfully.");
            return "assign-task-to";
        } catch (TaskNotFoundException e) {
            Task task = taskService.get(id);
            User user = (User) session.getAttribute("user");
            return "redirect:/assign-task.html";
        }
    }

}
