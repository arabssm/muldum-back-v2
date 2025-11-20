package co.kr.muldum.domain.item.model;

import co.kr.muldum.domain.item.dto.req.ItemMinPriceRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "nth_statuses")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NthStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nth_value", nullable = false)
    private Integer nthValue = 0;

    private String projectType;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<ItemMinPriceRequest> guide;

    private String deadlineDate;

    private Long teacherId;

    private LocalDateTime createdAt;

    public void updateNthValue(
            Integer nth,
            String projectType,
            List<ItemMinPriceRequest> guide,
            String deadlineDate,
            Long teacherId
    ) {
        this.nthValue = nth;
        this.projectType = projectType;
        this.guide = guide;
        this.deadlineDate = deadlineDate;
        this.teacherId = teacherId;
        this.createdAt = LocalDateTime.now();
    }
}
