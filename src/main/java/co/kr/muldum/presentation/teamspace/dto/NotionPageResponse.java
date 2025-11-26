package co.kr.muldum.presentation.teamspace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotionPageResponse {
    private String title;
    private String content;
    private boolean success;
}
