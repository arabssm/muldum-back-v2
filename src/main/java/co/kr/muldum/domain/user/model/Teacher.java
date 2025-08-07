package co.kr.muldum.domain.user.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Teacher {
    private Long id;
    private String name;
    private String email;
}
