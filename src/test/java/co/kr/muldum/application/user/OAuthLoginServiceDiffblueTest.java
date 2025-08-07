package co.kr.muldum.application.user;

import static org.mockito.Mockito.mock;

import co.kr.muldum.infrastructure.user.UserReaderImpl;
import co.kr.muldum.infrastructure.user.oauth.GoogleOAuthClient;
//import com.diffblue.cover.annotations.ManagedByDiffblue;
//import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

class OAuthLoginServiceDiffblueTest {
    /**
     * Test {@link OAuthLoginService#loginWithGoogle(String)}.
     *
     * <p>Method under test: {@link OAuthLoginService#loginWithGoogle(String)}
     */
    @Test
    @DisplayName("Test loginWithGoogle(String)")
    @Disabled("TODO: Complete this test")
    @Tag("ContributionFromDiffblue")
//    @ManagedByDiffblue
//    @MethodsUnderTest({
//            "co.kr.muldum.application.user.LoginResponseDto OAuthLoginService.loginWithGoogle(String)"
//    })
    void testLoginWithGoogle() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.IllegalStateException: JWT_SECRET_KEY 환경 변수가 설정되지 않았습니다.
        //       at co.kr.muldum.global.util.JwtProvider.<init>(JwtProvider.java:22)
        //       at co.kr.muldum.application.user.OAuthLoginService.<init>(OAuthLoginService.java:26)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        GoogleOAuthClient googleOAuthClient = new GoogleOAuthClient();
        UserReaderImpl userReader = new UserReaderImpl(mock(JdbcTemplate.class));

        // Act
        new OAuthLoginService(googleOAuthClient, userReader, new RedisTemplate<>())
                .loginWithGoogle("ABC123");
    }
}
