package pl.edu.pw.pap.importer;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.course.Course;
import pl.edu.pw.pap.course.CourseRepository;
import pl.edu.pw.pap.teacher.Teacher;
import pl.edu.pw.pap.teacher.TeacherRepository;

import java.util.List;

import static java.lang.Math.min;

@Service
@RequiredArgsConstructor
public class ImporterService {
    private static final Logger log = LoggerFactory.getLogger(ImporterService.class);
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;

    public void importData(List<ImporterRecord> records) throws ImporterException {
        var teachers = extractTeachers(records);
        teacherRepository.saveAll(teachers);

        var courses = records.stream()
                .map(r -> courseFromRecord(r, teachers))
                .toList();
        courseRepository.saveAll(courses);
        log.info("Import complete, saved " + teachers.size() + " teachers and " + courses.size() + " courses");
    }

    private Course courseFromRecord(ImporterRecord record, List<Teacher> teachers) {
        return new Course(
                record.title().substring(0, min(record.title().length(), 200)),
                record.language(),
                record.type(),
                record.level(),
                record.module(),
                getTeacherByName(teachers, record.teacher())
        );
    }

    private Teacher getTeacherByName(List<Teacher> teachers, String name) {
        return teachers.stream()
                .filter(t -> t.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new ImporterException("Import failed"));
    }

    private List<Teacher> extractTeachers(List<ImporterRecord> records) {
        return records.stream()
                .map(ImporterRecord::teacher)
                .map(Teacher::new)
                .distinct()
                .toList();
    }
}
