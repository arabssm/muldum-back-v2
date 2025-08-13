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
        // Skip JWT filtering for preflight, auth endpoints, and actuator
        String method = request.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }
        String uri = request.getRequestURI();
        return uri != null && (
            uri.startsWith("/ara/auth/") ||
            uri.startsWith("/actuator/")
        );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        // If there is no Bearer token, do not attempt auth; proceed down the chain
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String token = jwtTokenResolver.resolveToken(request);
            if (token != null && jwtTokenResolver.validateToken(token)) {
                log.info("[JwtFilter] 유효한 토큰입니다.");
                Authentication authentication = (Authentication) jwtTokenResolver.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.warn("[JwtFilter] 토큰 검증 실패: {}", ex.getMessage());
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
