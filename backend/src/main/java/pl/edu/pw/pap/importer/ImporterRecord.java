package pl.edu.pw.pap.importer;

public record ImporterRecord(
        String usos_code,
        String symbol,
        String language,
        String level,
        String module,
        String type,
        String title,
        String teacher,
        String time
        ) {
}
