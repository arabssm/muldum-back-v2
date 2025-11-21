package co.kr.muldum.infrastructure.room;

import co.kr.muldum.application.room.port.out.RoomUserPort;
import co.kr.muldum.domain.room.model.enums.RoomRole;
import co.kr.muldum.domain.user.model.User;
import co.kr.muldum.domain.user.repository.UserRepository;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomUserRepositoryImpl implements RoomUserPort {

    private final UserRepository userRepository;

    @Override
    public Optional<Long> findTeamIdByUserId(Long userId) {
        return userRepository.findById(userId)
                .map(User::getProfile)
                .map(this::extractTeamId);
    }

    @Override
    public Optional<RoomRole> findRoleByUserId(Long userId) {
        return userRepository.findById(userId)
                .map(User::getUserType)
                .map(RoomRole::fromUserType);
    }

    private Long extractTeamId(Map<String, Object> profile) {
        if (profile == null) {
            return null;
        }
        Object value = profile.get("team_id");
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String str && !str.isBlank()) {
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }
}
