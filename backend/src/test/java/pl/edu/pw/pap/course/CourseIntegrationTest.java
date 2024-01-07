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
import pl.edu.pw.pap.teacher.TeacherRepository;
import pl.edu.pw.pap.user.UserRepository;
import pl.edu.pw.pap.utils.DummyData;

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
    @Autowired
    DummyData data;

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    @BeforeEach
    public void setupDatabase() {
        data.deleteAll();
        data.addDummyData();
        var token = authService.attemptLogin(data.user_1.getUsername(), "password").getAccessToken();
        headers.add("Authorization", "Bearer " + token);
    }


    @Test
    public void getCourseByIdExists() {
        var response = restTemplate.exchange(
                buildUrl("/api/courses/1", port),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        var json = JsonPath.parse(response.getBody());
        assertEquals(1, (int) json.read("$.id"));
        assertEquals(data.course_1.getName(), json.read("$.name"));
        assertEquals(data.course_1.getType(), json.read("$.type"));
        assertEquals(data.course_1.getLevel(), json.read("$.level"));
        assertEquals(data.course_1.getModule(), json.read("$.module"));
        assertEquals(1, (int) json.read("$.teacherId"));
        assertTrue(json.read("$._links.self.href").toString().endsWith("/api/courses/1"));
        assertEquals(5.5, json.read("$.averageEaseRating"), 0.01);
        assertEquals(5.5, json.read("$.averageInterestRating"), 0.01);
        assertEquals(5.5, json.read("$.averageEngagementRating"), 0.01);


    }

    @Test
    public void getCourseByIdNotExists() {
        data.deleteAll();
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
        data.deleteAll();
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