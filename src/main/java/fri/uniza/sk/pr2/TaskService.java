package fri.uniza.sk.pr2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void save(Task task) {
        taskRepository.save(task);
    }

    public Task get(Long id) throws TaskNotFoundException {
        Optional<Task> result = taskRepository.findById(id);
        if(result.isPresent()) {
            return result.get();
        }
        throw new TaskNotFoundException("Could not find any user with ID: " + id);
    }

    public void delete(Long id) throws TaskNotFoundException {
        Long count = taskRepository.countById(id);
        if (count == 0 || count == null) {
            throw new TaskNotFoundException("Could not find any user with ID: " + id);
        }
        taskRepository.deleteById(id);
    }

    public List<Task> getAllByUser(User user) throws TaskNotFoundException {
        List<Task> tasks = taskRepository.findAllByUser(user);
        if (tasks == null) {
            throw new TaskNotFoundException("Could not find any tasks for this user");
        }
        return tasks;
    }

    public List<Task> getAll() {return (List<Task>) taskRepository.findAll();}
}
