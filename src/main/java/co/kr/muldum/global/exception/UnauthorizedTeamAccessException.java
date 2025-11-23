package co.kr.muldum.global.exception;

public class UnauthorizedTeamAccessException extends RuntimeException {

    public UnauthorizedTeamAccessException(Long userId, Long teamId) {
        super(buildMessage(userId, teamId));
    }

    private static String buildMessage(Long userId, Long teamId) {
        if (teamId == null) {
            return "팀 정보가 없어 월말평가에 접근할 수 없습니다.";
        }
        return String.format("유저 %d는 팀 %d의 월말평가에 접근할 수 없습니다.", userId, teamId);
    }
}
