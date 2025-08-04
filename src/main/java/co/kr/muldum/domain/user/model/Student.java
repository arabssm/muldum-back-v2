package co.kr.muldum.domain.user.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(name = "student_id")
    private String studentId;

    @Column(columnDefinition = "jsonb")
    private String profile;

    @Builder
    public Student(String email, String studentId, String profile) {
        this.email = email;
        this.studentId = studentId;
        this.profile = profile;
    }
}
