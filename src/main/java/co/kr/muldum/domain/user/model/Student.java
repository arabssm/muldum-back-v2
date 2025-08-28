package co.kr.muldum.domain.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.Map;

@Entity
@Getter
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

    public void updateNameIfEmpty(String name) {
        if (this.profile == null) {
            this.profile = new java.util.HashMap<>();
        }
        Object currentName = this.profile.get("name");
        if (currentName == null || currentName.toString().isBlank()) {
            this.profile.put("name", name);
        }
    }

    public void updateProfile(Map<String, Object> profile) {
        this.profile = profile != null ? new java.util.HashMap<>(profile) : null;
    }
}
