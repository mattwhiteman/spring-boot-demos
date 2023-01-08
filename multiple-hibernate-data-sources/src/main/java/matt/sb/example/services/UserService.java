package matt.sb.example.services;

import matt.sb.example.entities.primary.UserRecord;
import matt.sb.example.repositories.primary.UserRecordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserService {
    private final UserRecordRepository userRepository;
    public UserService(UserRecordRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserRecord getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
    }
}
