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
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.startsWith("/ara/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtTokenResolver.resolveToken(request);
        if (token != null && jwtTokenResolver.validateToken(token)) {
            log.info("[JwtFilter] 유효한 토큰입니다.");
            SecurityContextHolder.getContext().setAuthentication(
                    (Authentication) jwtTokenResolver.getAuthentication(token)
            );
        }

        filterChain.doFilter(request, response);
    }
}
