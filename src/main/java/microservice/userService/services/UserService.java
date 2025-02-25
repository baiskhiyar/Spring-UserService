package microservice.userService.services;
import microservice.userService.helpers.CommonUtils;
import microservice.userService.models.AccessTokenProvider;
import microservice.userService.models.Users;
import microservice.userService.helpers.AccessTokenProviderHelper;
import microservice.userService.helpers.TimeUtility;
import microservice.userService.repository.AccessTokenProviderRepository;
import microservice.userService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import microservice.userService.helpers.ScopesHelper;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccessTokenProviderRepository accessTokenProviderRepository;

    @Autowired
    private ScopesHelper scopesHelper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private AccessTokenProviderHelper accessTokenProviderHelper;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public Users registerUser(Users user) {
        if (checkIfUsernameTaken(user.getUsername())){
            String errMsg = "Username is already taken";
            logger.error(errMsg);
            throw new RuntimeException(errMsg);
        }
        String hashedPassword = generatePasswordHash(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public Users findByUsername(String username) {

        String cacheKey = getUsernameCacheKey(username);
        Users cachedUser = redisService.get(cacheKey, Users.class);
        if (cachedUser == null) {
            Optional<Users> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                cachedUser = user.get();
                redisService.save(cacheKey, cachedUser, 30, TimeUnit.MINUTES);
            }
        }
        return cachedUser;
    }

    public Users findById(int userId){
        String cacheKey = getUserByIdCacheKey(userId);
        Users cachedUser = redisService.get(cacheKey, Users.class);
        if (cachedUser == null){
            Users user = userRepository.findById(userId);
            if (user != null){
                redisService.save(cacheKey, user, 30, TimeUnit.MINUTES);
                cachedUser = user;
            }
        }
        return cachedUser;
    }

    public Users updateUserById(int userId, Users userUpdateData) {
        if (userId == 0){
            String errMsg = "Missing userId in params!";
            logger.error(errMsg);
            throw new RuntimeException(errMsg);
        }
        Users existingUser = userRepository.findById(userId);
        if (existingUser == null){
            throw new RuntimeException("User not found!");
        }
        String newUsername = userUpdateData.getUsername();
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
        if(accessTokenProviderRepository.findByUserId(user.getId()).isPresent())
        {
            Optional<AccessTokenProvider> accessTokenProvider1 = accessTokenProviderRepository.findByUserId(user.getId());
            String token = accessTokenProvider1.get().getAccessToken();
            accessTokenProviderHelper.expireAccessToken(token);
            //accessTokenProviderRepository.deleteByAccessToken(accessTokenProvider1.get().getAccessToken());
        }
        accessTokenProviderRepository.save(accessTokenProvider);
        return accessToken;
    }

    public String logoutUser(String authToken){
        Users user = CommonUtils.getCurrentUser();
        if (user == null){
            throw new RuntimeException("User not found!");
        }
        accessTokenProviderHelper.expireAccessToken(authToken);
        clearUserCache(user);
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

    public String getUsernameCacheKey(String username){
        return "user_by_username_" + username;
    }

    public String getUserByIdCacheKey(int userId){
        return "user_by_id_" + userId;
    }

    public void clearUserCache(Users user){
        String[] cacheKeys = new String[]{getUsernameCacheKey(user.getUsername()), getUserByIdCacheKey(user.getId())};
        for (String cacheKey : cacheKeys){
            redisService.delete(cacheKey);
        }
    }
}

