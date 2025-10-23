package co.kr.muldum.infrastructure.user;

import co.kr.muldum.domain.user.UserReader;
import co.kr.muldum.domain.user.model.UserInfo;
import co.kr.muldum.domain.user.model.UserType;
import co.kr.muldum.domain.user.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@RequiredArgsConstructor
public class UserReaderImpl implements UserReader {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<UserInfo> findByEmail(String email) {
        try {
            String query = "SELECT id, name, user_type, profile->>'team_id' as team_id FROM users WHERE email = ?";
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            query,
                            (rs, rowNum) -> mapRowToUserInfo(rs),
                            email
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public UserInfo read(Class<?> clazz, Long id) {
        try {
            String query = "SELECT id, name, email, user_type, profile->>'team_id' as team_id FROM users WHERE id = ?";
            return jdbcTemplate.queryForObject(
                    query,
                    (rs, rowNum) -> mapRowToUserInfo(rs),
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("User not found with id: " + id);
        }
    }

    private UserInfo mapRowToUserInfo(ResultSet rs) throws SQLException {
        String teamIdStr = rs.getString("team_id");
        Long teamId = null;
        if (teamIdStr != null && !teamIdStr.isEmpty() && !"null".equals(teamIdStr)) {
            try {
                teamId = Long.parseLong(teamIdStr);
            } catch (NumberFormatException e) {
                // 파싱 실패 시 null 유지
            }
        }

        return UserInfo.builder()
                .userId(rs.getLong("id"))
                .name(rs.getString("name"))
                .userType(UserType.valueOf(rs.getString("user_type").toUpperCase()))
                .teamId(teamId)
                .role(Role.MEMBER)
                .build();
    }
}
