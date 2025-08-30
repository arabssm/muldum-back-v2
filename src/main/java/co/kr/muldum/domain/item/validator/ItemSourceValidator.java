package co.kr.muldum.domain.item.validator;

import co.kr.muldum.domain.item.model.enums.ItemSource;

public interface ItemSourceValidator {
    boolean isValidSource(ItemSource itemSource);
}