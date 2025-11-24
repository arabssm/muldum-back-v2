package co.kr.muldum.domain.item.dto;

import co.kr.muldum.domain.item.model.enums.TeamType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TeacherItemResponseDto {
    private Integer team_id;
    private String team_name;
    private TeamType type;
    private Long item_id;
    private String product_name;
    private Integer quantity;
    private String price;
    private String productLink;
    private String reason;
    private String status;
    private String deliveryNumber;
    private String deliveryPrice;
    private LocalDateTime deliveryTime;
    private String rejectReason;
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    @JsonProperty("approved_at")
    private LocalDateTime approvedAt;
    @JsonProperty("rejected_at")
    private LocalDateTime rejectedAt;

}
