package co.kr.muldum.application.user;

import co.kr.muldum.domain.user.UserReader;
import co.kr.muldum.domain.user.model.Student;
import co.kr.muldum.domain.user.model.UserInfo;
import co.kr.muldum.domain.user.repository.StudentRepository;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import co.kr.muldum.global.util.JwtProvider;
import co.kr.muldum.infrastructure.user.oauth.KakaoOAuthClient;
import co.kr.muldum.infrastructure.user.oauth.dto.KakaoUserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final StudentRepository studentRepository;
    private final JwtProvider jwtProvider;
    private final UserReader userReader;

    public LoginResponseDto login(String accessToken) {
        KakaoUserInfoDto userInfoDto = kakaoOAuthClient.getUserInfo(accessToken);

        String email = userInfoDto.getEmail().trim();
        log.info("카카오 로그인 이메일: {}", email);

        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.UNREGISTERED_USER));

        String access = jwtProvider.createAccessToken(student.getId(), "STUDENT");
        String refresh = jwtProvider.createRefreshToken(student.getId(), "STUDENT");
        UserInfo userInfo = userReader.read(Student.class, student.getId());

        return LoginResponseDto.of(userInfo, access, refresh);
    }
}
