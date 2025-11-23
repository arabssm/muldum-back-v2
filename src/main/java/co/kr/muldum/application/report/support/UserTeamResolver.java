package co.kr.muldum.application.report.support;

import co.kr.muldum.domain.user.model.User;
import co.kr.muldum.domain.user.repository.UserRepository;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import co.kr.muldum.global.exception.UnauthorizedTeamAccessException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserTeamResolver {

    private final UserRepository userRepository;

    public Long resolveTeamId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        Long teamId = extractTeamId(user.getProfile());
        if (teamId == null || teamId <= 0) {
            throw new UnauthorizedTeamAccessException(userId, teamId);
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
