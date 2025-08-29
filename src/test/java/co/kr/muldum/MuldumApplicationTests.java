package co.kr.muldum;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
@org.springframework.test.context.ActiveProfiles("test")
@SpringBootTest
class MuldumApplicationTests {


    @org.springframework.boot.test.context.TestConfiguration
    static class MockConfig {
        @org.springframework.context.annotation.Bean
        org.springframework.data.redis.core.RedisTemplate<String, String> redisTemplate() {
            return org.mockito.Mockito.mock(org.springframework.data.redis.core.RedisTemplate.class);
        }
        @org.springframework.context.annotation.Bean
        co.kr.muldum.global.util.JwtProvider jwtProvider() {
            return org.mockito.Mockito.mock(co.kr.muldum.global.util.JwtProvider.class);
        }
    }

	@Test
	void contextLoads() {
	}

}
