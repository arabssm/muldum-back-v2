package co.kr.muldum.domain.user;

import co.kr.muldum.domain.user.model.UserInfo;

import java.util.Optional;

public interface UserReader {
    Optional<UserInfo> findByEmail(String email);

    UserInfo read(Class<?> clazz, Long id);
}
