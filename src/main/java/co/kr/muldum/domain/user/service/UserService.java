package co.kr.muldum.domain.user.service;

import co.kr.muldum.domain.user.dto.UserIssueResponseDto;
import co.kr.muldum.domain.user.dto.UserResponseDto;
import co.kr.muldum.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserIssueResponseDto fixNullTeamIds() {
        log.info("team_id가 null인 유저들을 0으로 업데이트 시작");

        try {
            int updatedCount = userRepository.updateNullTeamIdToZero();

            log.info("team_id 업데이트 완료 - 업데이트된 유저 수: {}", updatedCount);

            return UserIssueResponseDto.builder()
                    .message("team_id가 null인 유저들을 0으로 업데이트했습니다.")
                    .updatedCount(updatedCount)
                    .build();

        } catch (Exception e) {
            log.error("team_id 업데이트 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("team_id 업데이트 실패", e);
        }
    }

    public UserIssueResponseDto fixNullProfiles() {
        log.info("profile이 null인 유저들을 기본 프로필로 업데이트 시작");

        try {
            // 네이티브 쿼리 사용
            int updatedCount = userRepository.updateNullProfileToDefault();

            log.info("profile 업데이트 완료 - 업데이트된 유저 수: {}", updatedCount);

            return UserIssueResponseDto.builder()
                    .message("profile이 null인 유저들을 기본 프로필로 업데이트했습니다.")
                    .updatedCount(updatedCount)
                    .build();

        } catch (Exception e) {
            log.error("profile 업데이트 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("profile 업데이트 실패", e);
        }
    }

    public UserResponseDto getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(UserResponseDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. id=" + userId));
    }
}