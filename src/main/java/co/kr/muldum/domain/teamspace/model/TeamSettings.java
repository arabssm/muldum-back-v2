package co.kr.muldum.domain.teamspace.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamSettings {

    private String theme;
    private boolean notificationsEnabled;
    private String language;
    private int maxMembers;
    private String backgroundImageUrl;
    private String iconImageUrl;
    
}