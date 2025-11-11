package co.kr.muldum.domain.item.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public Integer updateNthValue(Integer nth) {
        this.nthValue = nth;
        return this.nthValue;
    }
}
