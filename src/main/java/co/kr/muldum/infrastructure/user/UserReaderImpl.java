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
                      (rs, rowNum) -> UserInfo.builder()
                              .userId(rs.getLong("id"))
                              .name(rs.getString("name"))
                              .userType(UserType.valueOf(rs.getString("user_type").toUpperCase()))
                              .teamId(null)
                              .role(Role.MEMBER)
                              .build(),
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
            // PostgreSQL JSON 추출 문법 사용
            String query = "SELECT id, name, email, user_type, profile->>'team_id' as team_id FROM users WHERE id = ?";
            return jdbcTemplate.queryForObject(
                    query,
                    (rs, rowNum) -> {
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
                    },
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("User not found with id: " + id);
        }
    }
}
