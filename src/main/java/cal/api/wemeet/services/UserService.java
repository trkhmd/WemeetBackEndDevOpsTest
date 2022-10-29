package cal.api.wemeet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cal.api.wemeet.models.User;
import cal.api.wemeet.models.dto.request.UserCreationEntry;
import cal.api.wemeet.models.dto.response.JwtResponse;
import cal.api.wemeet.models.dto.response.SimpleResponse;
import cal.api.wemeet.repositories.UserRepository;
import cal.api.wemeet.security.jwt.JwtTokenUtil;

@Service
public class UserService {
    
    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public ResponseEntity<?> signin(String email, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        }
        catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new SimpleResponse(HttpStatus.FORBIDDEN,"User is disabled"));
        }
        catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new SimpleResponse(HttpStatus.FORBIDDEN,"Email or password is incorrect"));
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    public ResponseEntity<?> signup(UserCreationEntry entry) {
        // verification si le mail existe deja
        if (checkEmailExists(entry.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new SimpleResponse(HttpStatus.BAD_REQUEST,"Email is already used"));
        }
        // encode le mot de passe
        entry.setPassword(passwordEncoder.encode(entry.getPassword()));
        User user = getUserFromUserEntry(entry);
        // Create new user's account
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new SimpleResponse(HttpStatus.CREATED,"User created successfully!"));
    }

    private boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    private User getUserFromUserEntry(UserCreationEntry entry) {
        User user = new User();
        user.setEmail(entry.getEmail());
        user.setPassword(entry.getPassword());
        user.setFirstName(entry.getFirstName());
        user.setLastName(entry.getLastName());
        user.setCity(entry.getCity());
        return user;
    }

    public User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByEmail(authenticatedUser.getUsername()); 
        return user;
    }
    

}
