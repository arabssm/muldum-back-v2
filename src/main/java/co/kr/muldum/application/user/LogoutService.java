package co.kr.muldum.application.user;

import co.kr.muldum.domain.token.repository.TokenRepository;
import co.kr.muldum.global.exception.TokenNotFoundException;
import co.kr.muldum.presentation.dto.LogoutRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final TokenRepository tokenRepository;

    @Transactional
    public void logout(LogoutRequestDto request) {
        String token = request.getRefreshToken();
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("refreshToken 값이 비어 있을 수 없습니다.");
        }

        tokenRepository.deleteByRefreshToken(token)
            .orElseThrow(() -> new TokenNotFoundException("유효한 리프레시 토큰이 없습니다."));
    }
}
