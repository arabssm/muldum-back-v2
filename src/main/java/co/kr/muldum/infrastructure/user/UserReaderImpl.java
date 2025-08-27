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
        return tryFindUser(email, "students", UserType.STUDENT)
                .or(() -> tryFindUser(email, "teachers", UserType.TEACHER))
                .or(() -> tryFindUser(email, "mentors", UserType.MENTOR));
    }

    private Optional<UserInfo> tryFindUser(String email, String tableName, UserType userType) {
        try {
            String query = String.format("SELECT id, profile ->> 'name' as name FROM %s WHERE email = ?", tableName);
            return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                    query,
                    (rs, rowNum) -> UserInfo.builder()
                        .userType(userType)
                        .userId(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .teamId(null)
                        .role(Role.MEMBER)
                        .build(),
                    email
                )
            );
        } catch (Exception e) {
          // 학생에서 못 찾으면 선생님에서 찾기
          try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT id, profile ->> 'name' as name FROM teachers WHERE email = ?",
                            (rs, rowNum) -> UserInfo.builder()
                                    .userType(UserType.TEACHER)
                                    .userId(rs.getLong("id"))
                                    .name(rs.getString("name"))
                                    .teamId(null)
                                    .role(Role.MEMBER)
                                    .build(),
                            email
                    )
            );
          } catch (Exception ex) {
            return Optional.empty();
          }
        }
    }

    @Override
    public UserInfo read(Class<?> clazz, Long id) {
        try {
            String tableName = clazz.getSimpleName().toLowerCase() + "s";
            String query = String.format("SELECT id, email, profile ->> 'name' as name, team_id FROM %s WHERE id = ?", tableName);
            
            return jdbcTemplate.queryForObject(
                query,
                (rs, rowNum) -> UserInfo.builder()
                    .userId(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .teamId(rs.getObject("team_id", Long.class))
                    .userType(determineUserType(clazz))
                    .role(Role.MEMBER)
                    .build(),
                id
            );
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("User not found with id: " + id);
        }
    }
    
    private UserType determineUserType(Class<?> clazz) {
        String className = clazz.getSimpleName().toLowerCase();
        switch (className) {
            case "student":
                return UserType.STUDENT;
            case "teacher":
                return UserType.TEACHER;
            case "mentor":
                return UserType.MENTOR;
            default:
                return UserType.STUDENT;
        }
    }
}
