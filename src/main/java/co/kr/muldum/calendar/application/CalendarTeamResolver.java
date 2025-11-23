package co.kr.muldum.calendar.application;

import co.kr.muldum.domain.user.model.User;
import co.kr.muldum.domain.user.repository.UserRepository;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CalendarTeamResolver {

    private final UserRepository userRepository;

    public Long resolveTeamId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Long teamId = extractTeamId(user.getProfile());
        if (teamId == null || teamId <= 0) {
            throw new CustomException(ErrorCode.TEAM_ACCESS_DENIED);
        }
        return teamId;
    }

    private Long extractTeamId(Map<String, Object> profile) {
        if (profile == null) {
            return null;
        }

        Object rawValue = profile.get("team_id");
        if (rawValue == null) {
            return null;
        }

        if (rawValue instanceof Number number) {
            return number.longValue();
        }

        if (rawValue instanceof String str && !str.isBlank()) {
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        return null;
    }
}
