package pl.edu.pw.pap.comment.report;

public enum ReportStatus {
    DISCARDED("Report discarded"),
    CONTENT_DELETE("Reported content deleted");

    public final String message;

    private ReportStatus(String label) {
        this.message = label;
    }
}
