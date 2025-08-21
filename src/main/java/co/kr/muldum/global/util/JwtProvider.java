package co.kr.muldum.global.util;

import co.kr.muldum.global.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.JwtException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtProvider {

    private final String secretKey;

    public JwtProvider(@Value("${jwt.secret-key}") String secretKey) {
        this.secretKey = secretKey;
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
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Long userId, String userType) {
        return Jwts.builder()
                .setSubject("RefreshToken")
                .claim("userId", userId)
                .claim("userType", userType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
      try {
        Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token);
        return true;
      } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
        log.info("[JwtProvider] 토큰 검증 실패: {}", e.getMessage());
        return false;
      }
    }

    public Authentication getAuthentication(String token) {
      Claims claims = Jwts.parserBuilder()
              .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
              .build()
              .parseClaimsJws(token)
              .getBody();

      Long userId = Long.valueOf(claims.get("userId").toString());
      String userType = claims.get("userType").toString();

      List<GrantedAuthority> authorities = new ArrayList<>();
      if ("teacher".equalsIgnoreCase(userType)) {
        authorities.add(new SimpleGrantedAuthority("ROLE_TEACHER"));
      } else if ("student".equalsIgnoreCase(userType)) {
        authorities.add(new SimpleGrantedAuthority("ROLE_STUDENT"));
      }

      CustomUserDetails userDetails = new CustomUserDetails(userId, userType);

      return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }

    public long getRefreshTokenExpirationMillis() {
        return refreshTokenExpiration;
    }

    public boolean isValidRefreshToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String createAccessTokenByRefreshToken(String refreshToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

        Long userId = Long.valueOf(claims.get("userId", String.class));
        String userType = claims.get("userType", String.class);

        return createAccessToken(userId, userType);
    }
}
