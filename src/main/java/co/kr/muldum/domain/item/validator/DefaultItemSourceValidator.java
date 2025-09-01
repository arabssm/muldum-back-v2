package co.kr.muldum.domain.item.validator;

import co.kr.muldum.domain.item.model.enums.ItemSource;
import org.springframework.stereotype.Component;

@Component
public class DefaultItemSourceValidator implements ItemSourceValidator {

    @Override
    public boolean isValidSource(ItemSource itemSource) {
        return itemSource == ItemSource.DEVICEMART || itemSource == ItemSource.ELEVENMARKET;
    }
}