package co.kr.muldum.global.util;

import co.kr.muldum.domain.token.model.RefreshToken;
import co.kr.muldum.domain.token.repository.TokenRepository;
import co.kr.muldum.domain.user.model.UserType;
import co.kr.muldum.global.dto.TokenRefreshRequestDto;
import co.kr.muldum.global.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret-key}")
    private String secretKey;

    private final TokenRepository tokenRepository;

    private final long accessTokenExpiration = 1000 * 60 * 60;
    private final long teacherAccessTokenExpiration = 1000 * 60 * 60 * 24 * 14;// 1시간
    private final long refreshTokenExpiration = 1000 * 60 * 60 * 24 * 14; // 2주

    public String createAccessToken(Long userId, String userType) {
      if ("teacher".equalsIgnoreCase(userType)) {
        return Jwts.builder()
                .setSubject("AccessToken")
                .claim("userId", userId)
                .claim("userType", userType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + teacherAccessTokenExpiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
      } else {
        return Jwts.builder()
                .setSubject("AccessToken")
                .claim("userId", userId)
                .claim("userType", userType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
      }
    }

    public String createRefreshToken(Long userId, UserType userType) {

        String refreshToken = Jwts.builder()
                .setSubject("RefreshToken")
                .claim("userId", userId)
                .claim("userType", userType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        tokenRepository.save(new RefreshToken(refreshToken, userId, userType));

        return refreshToken;
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

      Long userId = claims.get("userId", Long.class);
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

    public String createAccessTokenByRefreshToken(TokenRefreshRequestDto refreshToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(refreshToken.getRefreshToken())
                .getBody();

        Long userId = claims.get("userId", Long.class);
        String userType = claims.get("userType", String.class);

        return createAccessToken(userId, userType);
    }
}
