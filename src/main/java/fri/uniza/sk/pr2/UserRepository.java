package fri.uniza.sk.pr2;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    public Long countById(Long id);

    public User findByNameAndPassword(String name, String password);
}
