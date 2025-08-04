package co.kr.muldum.infrastructure.token;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public RefreshTokenRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String refreshToken, Long userId, long duration, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(refreshToken, String.valueOf(userId), duration, timeUnit);
    }

    public Optional<Long> findUserIdByToken(String refreshToken) {
        String userIdStr = redisTemplate.opsForValue().get(refreshToken);
        if (userIdStr == null) return Optional.empty();
        return Optional.of(Long.valueOf(userIdStr));
    }

    public void delete(String refreshToken) {
        redisTemplate.delete(refreshToken);
    }

    public boolean existsByToken(String token) {
        return redisTemplate.hasKey(token);
    }
}
