package co.kr.muldum.presentation.teamspace.dto;

import java.util.List;

public record TeamspaceInviteRequestDto(
        List<EmailRequest> emails
) {
    public record EmailRequest(String email) {}
}
