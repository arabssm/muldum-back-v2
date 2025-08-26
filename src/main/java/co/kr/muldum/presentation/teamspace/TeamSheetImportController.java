package co.kr.muldum.presentation.teamspace;

import co.kr.muldum.application.teamspace.TeamSheetImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for importing teams and members directly from a Google Sheet link.
 * Exposes POST /admin/team/import/sheet according to the spec for immediate DB upsert.
 */
@RestController
@RequestMapping("/admin/team/import")
@RequiredArgsConstructor
public class TeamSheetImportController {

    private final TeamSheetImportService teamSheetImportService;

    /**
     * Triggers import: reads the sheet and upserts teams, students, and members.
     */
    @PostMapping("/sheet")
    public TeamSheetImportResponse importFromSheet(@RequestBody TeamSheetImportRequest request) {
        return teamSheetImportService.importFromSheet(request);
    }
}
