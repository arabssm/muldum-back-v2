package co.kr.muldum.domain.token;

import co.kr.muldum.domain.user.model.UserType;

public  interface TokenRepository {

    public boolean deleteByRefreshToken(UserType userType, Long userId, String refreshToken);
}
