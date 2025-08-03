package co.kr.muldum.global.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtTokenResolver {

    private final JwtProvider jwtProvider;

    public JwtTokenResolver(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        return jwtProvider.validateToken(token);
    }

    public Object getAuthentication(String token) {
        return jwtProvider.getAuthentication(token);
    }
}
