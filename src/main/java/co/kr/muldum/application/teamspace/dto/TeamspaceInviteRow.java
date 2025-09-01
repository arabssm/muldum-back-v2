package co.kr.muldum.application.teamspace.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamspaceInviteRow {
    private String team;           // 팀 이름
    private String studentNumber;  // 학번
    private String name;           // 학생 이름
    private String role;           // 역할 (leader, member 등)
}