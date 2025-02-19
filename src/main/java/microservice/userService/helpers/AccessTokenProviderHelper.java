package microservice.userService.helpers;
import microservice.userService.repository.AccessTokenProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class AccessTokenProviderHelper {

    @Autowired
    private AccessTokenProviderRepository accessTokenProviderRepository;

    @Transactional
    public void expireAccessToken(String accessToken){
        LocalDateTime currentTime = TimeUtility.getCurrentDateTime();
        accessTokenProviderRepository.expireToken(accessToken, currentTime);
    }
}
