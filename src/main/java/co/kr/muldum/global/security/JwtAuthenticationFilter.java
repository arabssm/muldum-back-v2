package co.kr.muldum.global.security;

import co.kr.muldum.global.util.JwtTokenResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenResolver jwtTokenResolver;

    public JwtAuthenticationFilter(JwtTokenResolver jwtTokenResolver) {
        this.jwtTokenResolver = jwtTokenResolver;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String method = request.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }
        
        String path = request.getServletPath();
        log.info("[JwtFilter] Checking path: {}", path);
        
        // actuator와 health를 명시적으로 제외
        boolean skip = path != null && (
            path.startsWith("/actuator") ||
            path.equals("/health") ||
            path.startsWith("/ara/") ||
            path.startsWith("/user/issue")
        );
        if (skip && hasAuthorizationHeader(request)) {
            log.info("[JwtFilter] Authorization header present, forcing filter for path {}", path);
            return false;
        }
        
        log.info("[JwtFilter] Should skip filter for path {}: {}", path, skip);
        return skip;
    }

    private boolean hasAuthorizationHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return authHeader != null && !authHeader.isBlank();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        log.info("[JwtFilter] Processing request: {}", request.getServletPath());

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.isBlank()) {
            log.info("[JwtFilter] No Authorization header, proceeding");
            filterChain.doFilter(request, response);
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            log.warn("[JwtFilter] Invalid Authorization header format");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            String token = jwtTokenResolver.resolveToken(request);
            if (token != null && jwtTokenResolver.validateToken(token)) {
                log.info("[JwtFilter] 유효한 토큰입니다.");
                Authentication authentication = jwtTokenResolver.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
                return;
            } else {
                log.warn("[JwtFilter] Invalid token");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } catch (Exception ex) {
            log.warn("[JwtFilter] 토큰 검증 실패: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
    }
}
