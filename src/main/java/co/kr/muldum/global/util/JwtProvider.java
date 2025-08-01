package co.kr.muldum.global.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    private final String secretKey;

    public JwtProvider() {
        this.secretKey = System.getenv("JWT_SECRET_KEY");
        if (this.secretKey == null || this.secretKey.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET_KEY 환경 변수가 설정되지 않았습니다.");
        }
    }

    private final long accessTokenExpiration = 1000 * 60 * 60; // 1시간
    private final long refreshTokenExpiration = 1000 * 60 * 60 * 24 * 14; // 2주

    public String createAccessToken(Long userId, String userType) {
        return Jwts.builder()
                .setSubject("AccessToken")
                .claim("userId", userId)
                .claim("userType", userType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createRefreshToken(Long userId, String userType) {
        return Jwts.builder()
                .setSubject("RefreshToken")
                .claim("userId", userId)
                .claim("userType", userType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}
