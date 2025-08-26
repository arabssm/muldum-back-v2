package co.kr.muldum.application.auth;

import co.kr.muldum.global.dto.TokenRefreshRequestDto;
import co.kr.muldum.global.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenRefreshService {

    private final JwtProvider jwtProvider;

    public RefreshResponse refreshToken(TokenRefreshRequestDto refreshToken) {
        return new RefreshResponse(jwtProvider.createAccessTokenByRefreshToken(refreshToken));
    }
}
