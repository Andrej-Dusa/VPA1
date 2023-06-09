package fri.uniza.sk.pr2;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {

    public List<Task> findAllByUser(User user);
    Long countById(Long id);

}
