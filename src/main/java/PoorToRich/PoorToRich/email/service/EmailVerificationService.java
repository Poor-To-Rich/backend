package PoorToRich.PoorToRich.email.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final String KEY_PATTERN = "%s:%s";
    private static final Integer CODE_EXPIRATION_MINUTES = 10;

    private final StringRedisTemplate redisTemplate;

    private String getKey(String mail, String purpose) {
        return String.format(KEY_PATTERN, mail, purpose);
    }

    public void saveVerificationCode(String mail, String purpose, String code) {
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        valueOps.set(getKey(mail, purpose), code, CODE_EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }
}
