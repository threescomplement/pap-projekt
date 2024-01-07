package pl.edu.pw.pap.course;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.pw.pap.utils.WithMockUser;


import java.util.List;

import java.util.Optional;
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
        var course = new CourseDTO(1L, "Angielski w biznesie", "Angielski", "Biznesowy", "B2+", null, 5.5, 8.0, 4.3, 1L);

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
                .andExpect(jsonPath("$.averageEaseRating").value(course.getAverageEaseRating()))
                .andExpect(jsonPath("$.averageInterestRating").value(course.getAverageInterestRating()))
                .andExpect(jsonPath("$.averageEngagementRating").value(course.getAverageEngagementRating()))
                .andExpect(jsonPath("$.teacherId").value(course.getTeacherId()));

        // TODO Find way to check links
    }
}