package co.kr.muldum.application.auth;

import co.kr.muldum.global.dto.TokenRefreshRequestDto;
import co.kr.muldum.global.exception.InvalidRefreshTokenException;
import co.kr.muldum.global.util.JwtProvider;
import co.kr.muldum.infrastructure.token.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenRefreshService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    public String refreshAccessToken(TokenRefreshRequestDto request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtProvider.isValidRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        if (!refreshTokenRepository.existsByToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        return jwtProvider.createAccessTokenByRefreshToken(refreshToken);
    }
}
