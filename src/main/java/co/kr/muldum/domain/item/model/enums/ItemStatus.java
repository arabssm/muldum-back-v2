package co.kr.muldum.domain.item.model.enums;

public enum ItemStatus {
    PENDING,
    INTEMP,
    APPROVED,
    REJECTED,
    DELETED;

    public boolean isRejected() {
        return this == REJECTED;
    }
}
