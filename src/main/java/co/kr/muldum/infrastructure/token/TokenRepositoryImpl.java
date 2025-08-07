package co.kr.muldum.infrastructure.token;

import co.kr.muldum.domain.token.TokenRepository;
import co.kr.muldum.domain.user.model.UserType;
import org.springframework.stereotype.Repository;
import org.springframework.data.redis.core.RedisTemplate;

@Repository
public class TokenRepositoryImpl implements TokenRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public TokenRepositoryImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean deleteByRefreshToken(UserType userType, Long userId, String refreshToken) {
        String key = userType.name() + ":" + userId + ":refresh";
        Boolean result = redisTemplate.delete(key);
        return Boolean.TRUE.equals(result);
    }
}
