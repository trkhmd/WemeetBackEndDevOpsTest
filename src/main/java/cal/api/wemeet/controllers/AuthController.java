package cal.api.wemeet.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cal.api.wemeet.models.dto.request.UserCreationEntry;
import cal.api.wemeet.models.dto.request.UserLoginEntry;
import cal.api.wemeet.repositories.UserRepository;
import cal.api.wemeet.services.UserService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController  {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;


    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody UserLoginEntry entry) {
        return userService.signin(entry.getEmail(), entry.getPassword());
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserCreationEntry entry) {
        return userService.signup(entry);
    }

    
}
