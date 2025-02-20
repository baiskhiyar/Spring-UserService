package microservice.userService.services;
import microservice.userService.config.AccessTokenProviderHelper;
import microservice.userService.helpers.TimeUtility;
import microservice.userService.models.AccessTokenProvider;
import microservice.userService.models.Users;
import microservice.userService.repository.AccessTokenProviderRepository;
import microservice.userService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import microservice.userService.helpers.ScopesHelper;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccessTokenProviderRepository accessTokenProviderRepository;

    @Autowired
    private ScopesHelper scopesHelper;

    @Autowired
    private AccessTokenProviderHelper accessTokenProviderHelper;

    public Users registerUser(Users user) {
        if (checkIfUsernameTaken(user.getUsername())){
            throw new RuntimeException("Username already taken");
        }
        String hashedPassword = generatePasswordHash(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public Optional<Users> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Users updateUserById(int userId, Users userUpdateData) {
        if (userId == 0){
            throw new RuntimeException("Missing userId in params!");
        }
//      Checking if user exists with the given id.
        Users existingUser = userRepository.findById(userId);
        if (existingUser == null){
            throw new RuntimeException("User not found!");
        }
        String newUsername = userUpdateData.getUsername();
//      Checking if username is taken.
//      TODO : Need to check username taken on product/level.
        if (!existingUser.getUsername().equals(newUsername) && checkIfUsernameTaken(newUsername)){
            throw new RuntimeException("Username already taken");
        }
        String hashedPassword = generatePasswordHash(userUpdateData.getPassword());
        userUpdateData.setPassword(hashedPassword);
        existingUser.setPassword(userUpdateData.getPassword());
        existingUser.setFirstName(userUpdateData.getFirstName());
        existingUser.setLastName(userUpdateData.getLastName());
        existingUser.setEmail(userUpdateData.getEmail());
        existingUser.setActive(userUpdateData.getActive());
        return userRepository.save(existingUser);
    }

    public boolean checkIfUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public String loginUser(Users user){
        if(user.getUsername() == null || user.getPassword() == null){
            throw new RuntimeException("Username or password is missing!");
        }
        Users existingUser = userRepository.findByUsername(user.getUsername()).orElseThrow(() -> new RuntimeException("User not found!"));
        if (!validatePassword(user.getPassword(), existingUser.getPassword())){
            throw new RuntimeException("Invalid username or password!");
        }
        if (!existingUser.getActive()){
            throw new RuntimeException("User is not active!");
        }
        return createAccessTokenForUser(existingUser);
    }

    public String createAccessTokenForUser(Users user){
        String[] userScopes = scopesHelper.getScopesForUser(user.getId());
        if (userScopes.length == 0){
            throw new RuntimeException("User has no scopes!");
        }
        String accessToken = UUID.randomUUID().toString();
        ZonedDateTime expiresAt = TimeUtility.addDeltaToTime(
                TimeUtility.getCurrentDateTime(), "month", 3
        );
        AccessTokenProvider accessTokenProvider = new AccessTokenProvider();
        accessTokenProvider.setUserId(user.getId());
        accessTokenProvider.setAccessToken(accessToken);
        accessTokenProvider.setAvailableScopes(String.join(",", userScopes));
//      Converting to localDateTimeZone and setting it to expiresAt
        accessTokenProvider.setExpiresAt(expiresAt.toLocalDateTime());
        accessTokenProviderRepository.save(accessTokenProvider);
        return accessToken;
    }

    public String logoutUser(String authToken){
        accessTokenProviderHelper.expireAccessToken(authToken);
        return "Logged out successfully";
    }

    public String generatePasswordHash(String password){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    public boolean validatePassword(String password, String hashedPassword){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(password, hashedPassword);
    }
}

