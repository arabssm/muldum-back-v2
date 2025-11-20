package co.kr.muldum.domain.item.model;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "item_guide")
public class ItemGuide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long teacherId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String projectType;

    protected ItemGuide() { }

    private ItemGuide(Long teacherId, String content, String projectType) {
        this.teacherId = teacherId;
        this.content = content;
        this.projectType = projectType;
    }

    public static ItemGuide create(Long teacherId, String content, String projectType) {
        return new ItemGuide(teacherId, content, projectType);
    }

    public void update(String content, String projectType) {
        this.content = content;
        this.projectType = projectType;
    }

}
