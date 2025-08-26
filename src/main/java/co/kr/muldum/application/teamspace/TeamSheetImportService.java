package co.kr.muldum.application.teamspace;

import co.kr.muldum.domain.teamspace.repository.MemberRepository;
import co.kr.muldum.domain.teamspace.repository.TeamRepository;
import co.kr.muldum.domain.user.repository.StudentRepository;
import co.kr.muldum.infrastructure.sheets.GoogleSheetsClient;
import co.kr.muldum.presentation.dto.TeamSheetImportRequestDto;
import co.kr.muldum.presentation.dto.TeamSheetImportResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamSheetImportService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final StudentRepository studentRepository;
    private final GoogleSheetsClient googleSheetsClient;

    public TeamSheetImportResponseDto importFromSheet(TeamSheetImportRequestDto teamSheetImportRequestDto) {
        // 1. 구글 시트 링크에서 sheetId 추출
        String link = teamSheetImportRequestDto.getSheetLink();
        String sheetId = extractSheetId(link);

        // 2. GoogleSheetsClient로 시트 데이터 읽기
        // 시트 이름은 요청에 포함되거나 기본값 사용
        String range = teamSheetImportRequestDto.getRange();
        java.util.List<java.util.List<Object>> rows = googleSheetsClient.readRows(sheetId, (range != null ? range : "A2:D"));
        int totalRows = (rows != null) ? rows.size() : 0;

        // 3. TODO: 데이터 파싱 및 DB 저장 로직 (추후 구현)

        return TeamSheetImportResponseDto.builder()
                .total(totalRows)
                .teamsUpserted(0)
                .membersUpserted(0)
                .studentsUpserted(0)
                .skipped(0)
                .failed(0)
                .build();
    }

    // 구글 시트 링크에서 sheetId 추출
    private String extractSheetId(String link) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("/d/([^/]+)/");
        java.util.regex.Matcher matcher = pattern.matcher(link);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Invalid Google Sheets link: " + link);
    }
}
