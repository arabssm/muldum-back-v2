package co.kr.muldum.domain.calender.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "calenders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Calender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long teamId;

    @Column(nullable = false)
    private int year; // 예: 2025

    /**
     * MMdd 정수 (예: 1219)
     */
    @Column(nullable = false)
    private int date;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}