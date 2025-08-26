package co.kr.muldum.domain.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> profile;

    @Column(name = "team_id")
    private Long teamId;

    @Builder
    public Student(String email, Map<String, Object> profile) {
        this.email = email;
        this.profile = profile;
    }

    public String getName() {
        if (profile != null && profile.get("name") instanceof String) {
            return (String) profile.get("name");
        }
        return "";
    }

    public void setName(String name) {
        if (this.profile == null) {
            this.profile = new java.util.HashMap<>();
        }
        this.profile.put("name", name);
    }

    public static Student create(String email, String name) {
        Student student = new Student();
        student.setEmail(email);
        if (name != null && !name.isBlank()) {
            student.setName(name);
        }
        return student;
    }
}
