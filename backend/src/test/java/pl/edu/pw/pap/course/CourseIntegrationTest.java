package pl.edu.pw.pap.course;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.annotation.DirtiesContext;
import pl.edu.pw.pap.PapApplication;
import pl.edu.pw.pap.security.AuthService;
import pl.edu.pw.pap.teacher.Teacher;
import pl.edu.pw.pap.teacher.TeacherRepository;
import pl.edu.pw.pap.user.User;
import pl.edu.pw.pap.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.edu.pw.pap.utils.UrlBuilder.buildUrl;

@SpringBootTest(classes = PapApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CourseIntegrationTest {
    @LocalServerPort
    private int port;

    @MockBean
    private JavaMailSender fakeEmailSender;

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    private static final User USER = new User("user", "user@example.com", "$2a$12$vyx87ILAKlC2hkoh80nbMe0iXubtm/vgclOS22/Mj8BqToMyPDhb2", "ROLE_USER", true); // password
    private static final Teacher TEACHER_1 = new Teacher("mgr. Jan Kowalski");
    private static final Teacher TEACHER_2 = new Teacher("mgr. Ann Nowak");

    private static final Course COURSE_1 = new Course("Angielski w biznesie", "Angielski", "Biznesowy", "B2+", null, TEACHER_1);
    private static final Course COURSE_2 = new Course("Język angielski poziom C1", "Angielski", "Ogólny", "C1", "M15", TEACHER_1);
    private static final Course COURSE_3 = new Course("Język niemiecki, poziom A2", "Niemiecki", "Akademicki", "A2", "M6", TEACHER_2);
    private static final Course COURSE_4 = new Course("Język włoski dla początkujących", "Włoski", "Akademicki", "A1", "M1", TEACHER_2);


    @BeforeEach
    public void setupUser() {
        userRepository.deleteAll();
        userRepository.save(USER);
        var token = authService.attemptLogin("user", "password").getAccessToken();
        headers.add("Authorization", "Bearer " + token);
    }

    @BeforeEach
    public void setupCourses() {
        teacherRepository.deleteAll();
        courseRepository.deleteAll();
    }


    @Test
    public void getCourseByIdExists() {
        teacherRepository.save(TEACHER_1);
        courseRepository.save(COURSE_1);

        var response = restTemplate.exchange(
                buildUrl("/api/courses/1", port),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        var json = JsonPath.parse(response.getBody());
        assertEquals(1, (int) json.read("$.id"));
        assertEquals(COURSE_1.getName(), json.read("$.name"));
        assertEquals(COURSE_1.getType(), json.read("$.type"));
        assertEquals(COURSE_1.getLevel(), json.read("$.level"));
        assertEquals(COURSE_1.getModule(), json.read("$.module"));
        assertTrue(json.read("$._links.self.href").toString().endsWith("/api/courses/1"));
    }

    @Test
    public void getCourseByIdNotExists() {
        var response = restTemplate.exchange(
                buildUrl("/api/courses/1", port),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    public void navigateSelfLinkFromSingleCourse() {
        teacherRepository.save(TEACHER_1);
        courseRepository.save(COURSE_1);

        var response = restTemplate.exchange(
                buildUrl("/api/courses/1", port),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        // Follow self link
        var json = JsonPath.parse(response.getBody());
        var selfLink = json.read("$._links.self.href").toString();
        var response_2 = restTemplate.exchange(
                selfLink,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        assertEquals(response.getBody(), response_2.getBody());
    }

    @Test
    public void getAllCourses() {
        teacherRepository.saveAll(List.of(TEACHER_1, TEACHER_2));
        var cs = courseRepository.saveAll(List.of(COURSE_1, COURSE_2, COURSE_3, COURSE_4));

        var response = restTemplate.exchange(
                buildUrl("/api/courses", port),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());

        JSONArray courses = json.read("$._embedded.courses");
        assertEquals(4, courses.size());
    }

    @Test
    public void getAllCoursesFollowLinkToSingleCourse() {
        teacherRepository.saveAll(List.of(TEACHER_1, TEACHER_2));
        courseRepository.saveAll(List.of(COURSE_1, COURSE_2, COURSE_3, COURSE_4));

        var response = restTemplate.exchange(
                buildUrl("/api/courses", port),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        var singleCourseSelfLink = json.read("$._embedded.courses[0]._links.self.href").toString();

        var responseSingle = restTemplate.exchange(
                singleCourseSelfLink,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var jsonCourse = JsonPath.parse(responseSingle.getBody());
        assertEquals(singleCourseSelfLink, jsonCourse.read("$._links.self.href").toString());
    }

    @Test
    public void getAllCoursesEmpty() {
//         TODO find a way to include empty list in JSON even if it is empty
        var response = restTemplate.exchange(
                buildUrl("/api/courses", port),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());

        JSONArray courses = json.read("$._embedded.courses");
        assertEquals(0, courses.size());
    }
}