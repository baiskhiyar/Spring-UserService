package microservice.userService.controllers;

import microservice.userService.models.Users;
import microservice.userService.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
//@CrossOrigin // This will allow calls from different origin CORS.
// For example frontend is running on port A and backend is in port B.
@RequestMapping("/spring/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/healthCheck")
    public String getUserName() {
        return "UserService is up and running" ;
    }

    @PostMapping("/register") // TODO : starting slash missing. Check this
    public Users registerUser(@RequestBody Users user) {
        return userService.registerUser(user);
    }

    @PutMapping("/updateUser")
    public Users updateUser(@RequestBody Users user) {
        return userService.updateUserById(user.getId(), user);
    }

    @GetMapping("/getByUsername")
    public ResponseEntity<?> getUserByUsername(@RequestParam("username") String username) {
        Optional<Users> user = userService.findByUsername(username);
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody Users user) {
        return userService.loginUser(user);
    }

    @DeleteMapping("/logout")
    public  ResponseEntity<?> logoutUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>("Missing or invalid Authorization header", HttpStatus.BAD_REQUEST);
        }
        String authToken = authorizationHeader.substring(7); // Removing "Bearer " from authorizationHeader.
        String response = userService.logoutUser(authToken);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
