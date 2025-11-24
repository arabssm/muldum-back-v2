package co.kr.muldum.infrastructure.user;

import co.kr.muldum.domain.user.UserReader;
import co.kr.muldum.domain.user.model.UserInfo;
import co.kr.muldum.domain.user.model.UserType;
import co.kr.muldum.domain.user.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserReaderImpl implements UserReader {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<UserInfo> findByEmail(String email) {
        try {
            String query = "SELECT id, name, user_type FROM users WHERE email = ?";
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            query,
                            (rs, rowNum) -> {
                                Long userId = rs.getLong("id");
                                List<Long> teamIds = findTeamIdsByUserId(userId);
                                return UserInfo.builder()
                                        .userId(userId)
                                        .name(rs.getString("name"))
                                        .userType(UserType.valueOf(rs.getString("user_type").toUpperCase()))
                                        .teamIds(teamIds)
                                        .role(Role.MEMBER)
                                        .build();
                            },
                            email));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public UserInfo read(Class<?> clazz, Long id) {
        try {
            String query = "SELECT id, name, email, user_type FROM users WHERE id = ?";
            return jdbcTemplate.queryForObject(
                    query,
                    (rs, rowNum) -> {
                        List<Long> teamIds = findTeamIdsByUserId(id);
                        return UserInfo.builder()
                                .userId(rs.getLong("id"))
                                .name(rs.getString("name"))
                                .userType(UserType.valueOf(rs.getString("user_type").toUpperCase()))
                                .teamIds(teamIds)
                                .role(Role.MEMBER)
                                .build();
                    },
                    id);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("User not found with id: " + id);
        }
    }

    private List<Long> findTeamIdsByUserId(Long userId) {
        String query = "SELECT team_id FROM members WHERE user_id = ?";
        return jdbcTemplate.query(query, (rs, rowNum) -> rs.getLong("team_id"), userId);
    }
}
