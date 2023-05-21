package fri.uniza.sk.pr2;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskService extends CrudRepository<Task, Long> {
    public List<Task> findAllByUser(User user);
}
