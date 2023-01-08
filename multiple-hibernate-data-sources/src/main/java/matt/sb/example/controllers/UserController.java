package matt.sb.example.controllers;

import matt.sb.example.entities.primary.UserRecord;
import matt.sb.example.services.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public UserRecord getUserRecord(@RequestParam Integer id) {
        return userService.getUserById(id);
    }
}
