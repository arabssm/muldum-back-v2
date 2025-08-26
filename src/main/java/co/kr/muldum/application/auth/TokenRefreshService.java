package co.kr.muldum.application.auth;

import co.kr.muldum.global.dto.TokenRefreshRequestDto;
import co.kr.muldum.global.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenRefreshService {

    private final JwtProvider jwtProvider;

    public RefreshResponse refreshToken(TokenRefreshRequestDto requestDto) {
        String refreshToken = requestDto.getRefreshToken();

        if (!jwtProvider.isValidRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("리프레시 토큰이 유효하지 않습니다.");
        }

        String newAccessToken = jwtProvider.createAccessTokenByRefreshToken(requestDto);

        return new RefreshResponse(newAccessToken);
    }
}
