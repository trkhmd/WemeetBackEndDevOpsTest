package cal.api.wemeet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cal.api.wemeet.models.User;
import cal.api.wemeet.models.dto.request.UserCreationEntry;
import cal.api.wemeet.models.dto.response.JwtResponse;
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

    public void authentification(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }

    public JwtResponse getJsonWebTokenForUser(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String token = jwtTokenUtil.generateToken(userDetails);
        return new JwtResponse(token);
    }

    // Create new user account
    public User creatUser(User user) {
        return userRepository.save(user);
    }

    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public User getUserFromUserEntry(UserCreationEntry entry) {
        User user = new User();
        user.setEmail(entry.getEmail());
        user.setPassword(passwordEncoder.encode(entry.getPassword()));
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
