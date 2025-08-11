package co.kr.muldum.application.calender.service;

import co.kr.muldum.application.calender.dto.CalenderResponseDto;
import co.kr.muldum.application.calender.dto.CreateCalenderRequestDto;
import co.kr.muldum.domain.calender.model.Calender;
import co.kr.muldum.domain.calender.repository.CalenderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CalenderService {

    private final CalenderRepository calenderRepository;

    @Transactional
    public CalenderResponseDto createCalender(
            Long teamId,
            CreateCalenderRequestDto createCalenderRequestDto
    ) {
        Calender calender = Calender.builder()
                .teamId(teamId)
                .year(createCalenderRequestDto.getYear())
                .date(createCalenderRequestDto.getDate())
                .title(createCalenderRequestDto.getTitle())
                .content(createCalenderRequestDto.getContent())
                .build();

        Calender savedCalender = calenderRepository.save(calender);

        CalenderResponseDto calenderResponse = new CalenderResponseDto(
                savedCalender.getId(),
                savedCalender.getTeamId(),
                savedCalender.getYear(),
                savedCalender.getDate(),
                savedCalender.getTitle(),
                savedCalender.getContent()
        );
        return calenderResponse;
    }
}
