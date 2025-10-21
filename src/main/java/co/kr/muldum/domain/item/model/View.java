package co.kr.muldum.domain.item.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "views")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class View {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "viewer", nullable = false)
    private Long viewer; //teacher_id가 들어감

    @Column(name = "viewed_item_id", nullable = false)
    private Long viewedItemId; // item_id가 들어감

    @Column(name = "watched_at")
    private LocalDateTime watchedAt; // 조회한 시간

}

// viewer이 viewed_item_id를 조회한 시간인 watched_at보다 늦게 updated_at이나 created_at을 가진 경우에 new를 반환할것임
