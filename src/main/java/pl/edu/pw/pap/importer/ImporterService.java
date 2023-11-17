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
        var teachers = records.stream()
                .map(ImporterRecord::teacher)
                .map(Teacher::new)
                .toList();

        teacherRepository.saveAll(teachers);

        var courses = records.stream()
                .map(r -> {
                    var teacher = teachers.stream()
                            .filter(t -> t.getName().equals(r.teacher()))
                            .findFirst()
                            .get(); // TODO throw proper exception
                    return new Course(r.title().substring(0, min(r.title().length(), 200)), r.language(), r.type(), r.level(), r.module(), teacher);
                }).toList();
        courseRepository.saveAll(courses);
    }
}
