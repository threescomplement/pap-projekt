package pl.edu.pw.pap.teacher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static pl.edu.pw.pap.common.Constants.ALL;

@Service
@RequiredArgsConstructor
public class TeacherService {
    private final TeacherRepository teacherRepository;

    public Optional<TeacherDTO> getTeacherById(Long id) {
        return teacherRepository.findByIdWithRating(id);
    }

    public List<TeacherDTO> getTeachersMatchingFilters(String name, String language) {
        return teacherRepository.findTeachersMatchingFilters(name, language.equals(ALL) ? null : language);
    }
}