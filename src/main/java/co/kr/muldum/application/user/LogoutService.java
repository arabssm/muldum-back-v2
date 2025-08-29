package co.kr.muldum.application.user;

import co.kr.muldum.domain.token.repository.TokenRepository;
import co.kr.muldum.global.exception.TokenNotFoundException;
import co.kr.muldum.presentation.dto.LogoutRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final TokenRepository tokenRepository;

    public void logout(LogoutRequestDto request) {
        boolean deleted = tokenRepository.deleteByToken(
                request
                        .getRefreshToken()
        );

        if (!deleted) {
            throw new TokenNotFoundException("유효한 리프레시 토큰이 없습니다.");
        }
    }
}
