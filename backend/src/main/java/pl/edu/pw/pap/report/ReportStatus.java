package pl.edu.pw.pap.report;

public enum ReportStatus {
    ACTIVE("Oczekuje na rozpatrzenie"),
    DISCARDED("Zgłoszenie odrzucone"),
    CONTENT_DELETE("Treść usunięta");

    public final String message;

    ReportStatus(String label) {
        this.message = label;
    }
}
