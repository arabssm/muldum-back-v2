package co.kr.muldum.domain.item.model;

import co.kr.muldum.domain.item.dto.req.ItemGuide;
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
@Table(name = "nth_status_histories")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NthStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nth_value", nullable = false)
    private Integer nthValue;

    private String projectType;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<ItemGuide> guide;

    private String deadlineDate;

    private Long teacherId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
