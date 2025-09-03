package co.kr.muldum.domain.teamspace.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "teamspaces")
@SecondaryTable(
        name = "teams",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "id")
)
@Getter
@Setter
@NoArgsConstructor
public class TeamSpace {

    @Id
    private Long id;

    @Column(table = "teams", name = "name", nullable = false)
    private String teamName;

    @Enumerated(EnumType.STRING)
    @Column(table = "teams", name = "type", nullable = false)
    private TeamType teamType;

    private String content;
    private String logoPath;
    private String bannerPath;
}
