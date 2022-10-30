package cal.api.wemeet.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cal.api.wemeet.models.dto.request.UserCreationEntry;
import cal.api.wemeet.models.dto.request.UserLoginEntry;
import cal.api.wemeet.models.dto.response.JwtResponse;
import cal.api.wemeet.models.dto.response.SimpleResponse;
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
        try {
            userService.authentification(entry.getEmail(), entry.getPassword());
        } 
        catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new SimpleResponse("User is disabled"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new SimpleResponse("Email or password is incorrect"));
        }
        JwtResponse jwt = userService.getJsonWebTokenForUser(entry.getEmail());
        return ResponseEntity.ok(jwt);

    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserCreationEntry entry) {
        // verification si le mail existe deja
        if (userService.checkEmailExists(entry.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SimpleResponse("Email is already used"));
        }
        // creation du user
        userService.creatUser(userService.getUserFromUserEntry(entry));
        return ResponseEntity.status(HttpStatus.CREATED).body(new SimpleResponse("User created successfully!"));
    }

    
}
