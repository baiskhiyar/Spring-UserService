package microservice.userService.controllers;

import jakarta.servlet.http.HttpServletRequest;
import microservice.userService.models.Users;
import microservice.userService.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("spring/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("healthCheck")
    public String getUserName() {
        return "UserService is up and running" ;
    }

    @PostMapping("register")
    public Users registerUser(@RequestBody Users user) {
        return userService.registerUser(user);
    }

    @PutMapping("updateUser")
    @PreAuthorize("hasAnyAuthority('admin', 'employee')")
    public Users updateUser(@RequestBody Users user) {
        return userService.updateUserById(user.getId(), user);
    }

    @GetMapping("/getByUsername/{username}")
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        Optional<Users> user = userService.findByUsername(username);
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("login")
    public String loginUser(@RequestBody Users user) {
        return userService.loginUser(user);
    }

    @DeleteMapping("logout")
    @PreAuthorize("hasAnyAuthority('admin')")
    public  ResponseEntity<?> logoutUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>("Missing or invalid Authorization header", HttpStatus.BAD_REQUEST);
        }
        String authToken = authorizationHeader.substring(7); // Removing "Bearer " from authorizationHeader.
        String response = userService.logoutUser(authToken);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("csrf-token")
    @PreAuthorize("hasAnyAuthority('admin')")
    public CsrfToken getCsrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    }
}
