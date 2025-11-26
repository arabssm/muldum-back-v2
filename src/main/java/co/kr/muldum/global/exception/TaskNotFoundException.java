package co.kr.muldum.global.exception;

public class TaskNotFoundException extends CustomException {
    public TaskNotFoundException() {
        super(ErrorCode.NOT_FOUND_TASK);
    }
}