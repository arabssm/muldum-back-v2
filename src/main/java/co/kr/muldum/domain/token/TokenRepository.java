package co.kr.muldum.domain.token;

import co.kr.muldum.domain.user.model.UserType;

public abstract class TokenRepository {

    public abstract boolean deleteByRefreshToken(UserType userType, Long userId, String refreshToken);
}
