package pl.edu.pw.pap.course;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.hamcrest.core.StringEndsWith;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.pw.pap.teacher.Teacher;
import pl.edu.pw.pap.utils.WithMockUser;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CourseControllerTest {
    @Autowired
    private MockMvc api;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseService courseService;

    @Test
    @WithMockUser
    public void getCourseByIdExists() throws Exception {
        var course = new CourseDTO(1L, "Angielski w biznesie", "Angielski", "Biznesowy", "B2+", null, 5.5, 1L);

        Mockito.doReturn(Optional.of(course)).when(courseService).getById(1L);

        api.perform(get("/api/courses/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(course.getId()))
                .andExpect(jsonPath("$.name").value(course.getName()))
                .andExpect(jsonPath("$.language").value(course.getLanguage()))
                .andExpect(jsonPath("$.type").value(course.getType()))
                .andExpect(jsonPath("$.level").value(course.getLevel()))
                .andExpect(jsonPath("$.module").value(course.getModule()))
                .andExpect(jsonPath("$.averageRating").value(course.getAverageRating()))
                .andExpect(jsonPath("$.teacherId").doesNotExist());

        // TODO Find way to check links
    }

    @Test
    @WithMockUser
    public void getTeacherCoursesNormal() throws Exception {
        var course_1 = new CourseDTO(1L, "Angielski w biznesie", "Angielski", "Biznesowy", "B2+", null, 5.5, 1L);
        var course_2 = new CourseDTO(2L, "Język angielski poziom C1", "Angielski", "Ogólny", "C1", "M15", 7.0, 1L );
        var teacher = new Teacher("Ann Nowak");
        Mockito.doReturn(List.of(course_1, course_2)).when(courseService).getTeacherCourses(1L);

        var response = api.perform(get("/api/courses/teachers/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.courses[0].id").value(course_1.getId()))
                .andExpect(jsonPath("$._embedded.courses[0].name").value(course_1.getName()))
                .andExpect(jsonPath("$._embedded.courses[0].language").value(course_1.getLanguage()))
                .andExpect(jsonPath("$._embedded.courses[0].type").value(course_1.getType()))
                .andExpect(jsonPath("$._embedded.courses[0].level").value(course_1.getLevel()))
                .andExpect(jsonPath("$._embedded.courses[0].module").value(course_1.getModule()))
                .andExpect(jsonPath("$._embedded.courses[0].averageRating").value(course_1.getAverageRating()))
                .andExpect(jsonPath("$._embedded.courses[0].teacherId").doesNotExist())
                .andExpect(jsonPath("$._embedded.courses[1].id").value(course_2.getId()))
                .andExpect(jsonPath("$._embedded.courses[1].name").value(course_2.getName()))
                .andExpect(jsonPath("$._embedded.courses[1].language").value(course_2.getLanguage()))
                .andExpect(jsonPath("$._embedded.courses[1].type").value(course_2.getType()))
                .andExpect(jsonPath("$._embedded.courses[1].level").value(course_2.getLevel()))
                .andExpect(jsonPath("$._embedded.courses[1].module").value(course_2.getModule()))
                .andExpect(jsonPath("$._embedded.courses[1].averageRating").value(course_2.getAverageRating()))
                .andExpect(jsonPath("$._embedded.courses[1].teacherId").doesNotExist());


    }



    @Test
    @WithMockUser
    public void getTeacherCoursesEmpty() throws Exception {
        Mockito.doReturn(List.of()).when(courseService).getTeacherCourses(1L);

        var response = api.perform(get("/api/courses/teachers/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.courses").exists())
                .andExpect(jsonPath("$._embedded.courses[0]").doesNotExist());



    }


}