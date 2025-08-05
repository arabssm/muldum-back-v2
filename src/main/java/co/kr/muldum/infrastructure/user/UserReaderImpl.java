package co.kr.muldum.infrastructure.user;

import co.kr.muldum.domain.user.UserReader;
import co.kr.muldum.domain.user.model.UserInfo;
import co.kr.muldum.domain.user.model.UserType;
import co.kr.muldum.domain.user.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserReaderImpl implements UserReader {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<UserInfo> findByEmail(String email) {

        // 학생 테이블에서 먼저 조회
        try {
            return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                    "SELECT id, profile ->> 'name' as name FROM students WHERE email = ?",
                    (rs, rowNum) -> UserInfo.builder()
                        .userType(UserType.STUDENT)
                        .userId(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .teamId(null)
                        .role(Role.MEMBER)
                        .build(),
                    email
                )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public UserInfo read(Class<?> clazz, Long id) {
        return null;
    }
}
