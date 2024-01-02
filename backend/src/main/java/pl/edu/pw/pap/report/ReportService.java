package pl.edu.pw.pap.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.comment.report.CommentReportRepository;
import pl.edu.pw.pap.review.report.ReviewReportRepository;


@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReviewReportRepository reviewReportRepostiory;
    private final CommentReportRepository commentReportRepository;

}
