package pl.edu.pw.pap.report;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // oodzielne do dodawania, usuwiania, pobierania pojedynczych (nwm czy potrzebne),
    // wspólny do pobierania wszystkich

    @GetMapping("/api/admin/reports/comments/{commentReportId}")
    public ReportDTO getCommentReport(@PathVariable Long commentReportId){
        return null;
    }




}
