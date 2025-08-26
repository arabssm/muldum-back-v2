package co.kr.muldum.presentation.teamspace;

import co.kr.muldum.application.teamspace.TeamSheetImportService;
import co.kr.muldum.presentation.dto.TeamSheetImportRequestDto;
import co.kr.muldum.presentation.dto.TeamSheetImportResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for importing teams and members directly from a Google Sheet link.
 * Only teachers (admins) are authorized to add teams and members through this endpoint.
 * Exposes POST /admin/team/import/sheet for immediate DB upsert.
 */
@RestController
@RequestMapping("/std/team")
@RequiredArgsConstructor
public class TeamSheetImportController {

    private final TeamSheetImportService teamSheetImportService;

    /**
     * For teachers to upsert teams and members by reading the sheet.
     * Exposes POST /std/team/invite for immediate DB upsert.
     */
    @PostMapping("/invite")
    public TeamSheetImportResponseDto importFromSheet(@RequestBody TeamSheetImportRequestDto teamSheetImportRequestDto) {
        return teamSheetImportService.importFromSheet(teamSheetImportRequestDto);
    }
}
