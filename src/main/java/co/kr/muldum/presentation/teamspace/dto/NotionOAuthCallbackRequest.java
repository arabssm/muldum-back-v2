package co.kr.muldum.presentation.teamspace.dto;

import lombok.Data;

@Data
public class NotionOAuthCallbackRequest {
    private String code;
    private String state;
}
