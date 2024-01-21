package pl.edu.pw.pap.report;

public enum ReportStatus {
    DISCARDED("Report discarded"),
    CONTENT_DELETE("Reported content deleted");

    public final String message;

    ReportStatus(String label) {
        this.message = label;
    }
}
