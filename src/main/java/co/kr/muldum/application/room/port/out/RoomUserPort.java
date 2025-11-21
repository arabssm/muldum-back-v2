package co.kr.muldum.application.room.port.out;

import co.kr.muldum.domain.room.model.enums.RoomRole;
import java.util.Optional;

public interface RoomUserPort {
    Optional<Long> findTeamIdByUserId(Long userId);
    Optional<RoomRole> findRoleByUserId(Long userId);
}
