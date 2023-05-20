package fri.uniza.sk.pr2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAll() {
        return (List<User>) userRepository.findAll();
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public User get(Long id) throws UserNotFoundException {
      Optional<User> result = userRepository.findById(id);
      if(result.isPresent()) {
          return result.get();
      }
      throw new UserNotFoundException("Could not find any user with ID: " + id);
    }

    public void delete(Long id) throws UserNotFoundException {
        Long count = userRepository.countById(id);
        if (count == 0 || count == null) {
            throw new UserNotFoundException("Could not find any user with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    public User login(String name, String password) throws UserNotFoundException {
        User user = userRepository.findByNameAndPassword(name, password);
        if (user == null) {
            throw new UserNotFoundException("Could not find any account with this Username or Password");
        }
        return user;
    }
}
