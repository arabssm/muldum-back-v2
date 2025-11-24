package co.kr.muldum.domain.item.repository;

import co.kr.muldum.domain.item.model.ItemShippingPolicy;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemShippingPolicyRepository extends JpaRepository<ItemShippingPolicy, Long> {

    Optional<ItemShippingPolicy> findTopByOrderByIdAsc();
}
