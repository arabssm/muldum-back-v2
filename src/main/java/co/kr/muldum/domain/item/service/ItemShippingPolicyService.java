package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.req.ShippingPolicyRequest;
import co.kr.muldum.domain.item.dto.res.ShippingPolicyResponse;
import co.kr.muldum.domain.item.model.ItemShippingPolicy;
import co.kr.muldum.domain.item.repository.ItemShippingPolicyRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemShippingPolicyService {

    private final ItemShippingPolicyRepository itemShippingPolicyRepository;

    @Transactional
    public ShippingPolicyResponse saveOrUpdatePolicy(ShippingPolicyRequest request) {
        ItemShippingPolicy policy = itemShippingPolicyRepository.findTopByOrderByIdAsc()
                .map(existing -> {
                    existing.updatePolicy(
                            request.atLeastShippingMoney(),
                            request.youCantApplyForIgenship()
                    );
                    return existing;
                })
                .orElseGet(() -> ItemShippingPolicy.builder()
                        .atLeastShippingMoney(request.atLeastShippingMoney())
                        .youCantApplyForIgenship(request.youCantApplyForIgenship())
                        .build()
                );

        ItemShippingPolicy saved = itemShippingPolicyRepository.save(policy);
        return toResponse(saved);
    }

    public Optional<ShippingPolicyResponse> getPolicy() {
        return itemShippingPolicyRepository.findTopByOrderByIdAsc()
                .map(this::toResponse);
    }

    private ShippingPolicyResponse toResponse(ItemShippingPolicy policy) {
        return ShippingPolicyResponse.builder()
                .id(policy.getId())
                .atLeastShippingMoney(policy.getAtLeastShippingMoney())
                .youCantApplyForIgenship(policy.isYouCantApplyForIgenship())
                .updatedAt(policy.getUpdatedAt())
                .build();
    }
}
