package pl.edu.pw.pap.importer;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ImporterController {
    private static final Logger log = LoggerFactory.getLogger(ImporterController.class);
    private final ImporterService importerService;

    @PostMapping("/api/admin/importer/data")
    public ResponseEntity<String> importData(@RequestBody List<ImporterRecord> data) {
        importerService.importData(data);
        log.info("Import complete");
        return ResponseEntity.ok("Import complete");
    }
}
