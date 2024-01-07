package pl.edu.pw.pap.teacher;

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
import pl.edu.pw.pap.course.Course;
import pl.edu.pw.pap.course.CourseRepository;
import pl.edu.pw.pap.review.Review;
import pl.edu.pw.pap.review.ReviewRepository;
import pl.edu.pw.pap.security.AuthService;
import pl.edu.pw.pap.user.User;
import pl.edu.pw.pap.user.UserRepository;
import pl.edu.pw.pap.utils.DummyData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.edu.pw.pap.utils.UrlBuilder.buildUrl;

@SpringBootTest(classes = PapApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TeacherIntegrationTests {

    @LocalServerPort
    private int port;

    @MockBean
    private JavaMailSender fakeEmailSender;

    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AuthService authService;
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
    public void getTeacherByIdExists() {
        var response = restTemplate.exchange(buildUrl("/api/teachers/1", port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        assertEquals(1, (int) json.read("$.id"));
        assertEquals(data.teacher_1.getName(), json.read("$.name"));
        assertEquals(5.5, (double) (json.read("$.averageRating")), 0.001);
        assertEquals(2, (int) json.read("$.numberOfRatings"));
        assertTrue(json.read("$._links.self.href").toString().endsWith("/api/teachers/1"));
        assertTrue(json.read("$._links.courses.href").toString().contains("/api/courses?name=&language=all&module=all&type=all&level=all&teacherName=mgr.%20Jan%20Kowalski"));
        assertTrue(json.read("$._links.all.href").toString().endsWith("/api/teachers?name=&language=all"));
        assertTrue(json.read("$._links.reviews.href").toString().endsWith("/api/teachers/1/reviews"));


    }

    @Test
    public void getTeacherByIdNotExists() {
        data.deleteAll();
        var response = restTemplate.exchange(buildUrl("/api/teachers/1", port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    public void getTeacherByIdFollowSelfLink() {
        var response = restTemplate.exchange(buildUrl("/api/teachers/1", port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        var selfLink = json.read("$._links.self.href").toString();

        var selfResponse = restTemplate.exchange(selfLink, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), selfResponse.getStatusCode());
        assertEquals(response.getBody(), selfResponse.getBody());
    }

    @Test
    public void getAllTeachersDefaultFilter() {
        var response = restTemplate.exchange(buildUrl("/api/teachers", port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        assertEquals(3, (int) json.read("$._embedded.teachers.size()"));
        assertTrue(json.read("$._embedded.teachers[?(@.id == 1)].name").toString().contains(data.teacher_1.getName()));
        assertTrue(json.read("$._embedded.teachers[?(@.id == 2)].name").toString().contains(data.teacher_2.getName()));
        assertTrue(json.read("$._embedded.teachers[?(@.id == 3)].name").toString().contains(data.teacher_3.getName()));
    }

    @Test
    public void getAllTeachersEmptyResult() {
        data.deleteAll();
        var response = restTemplate.exchange(buildUrl("/api/teachers", port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        assertEquals(0, (int) json.read("$._embedded.teachers.size()"));
    }

    @Test
    public void getAllTeachersFilterByLanguage() {
        var response = restTemplate.exchange(buildUrl("/api/teachers?language=Włoski", port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        assertEquals(1, (int) json.read("$._embedded.teachers.size()"));
        assertTrue(json.read("$._embedded.teachers[0].name").toString().contains(data.teacher_2.getName()));
    }

    @Test
    public void getAllTeachersFilterByName() {
        var response = restTemplate.exchange(buildUrl("/api/teachers?name=jan", port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        assertEquals(1, (int) json.read("$._embedded.teachers.size()"));
        assertTrue(json.read("$._embedded.teachers[0].name").toString().contains(data.teacher_1.getName()));
    }

    @Test
    public void getAllTeachersFilterByNameAndLanguage() {
        var response = restTemplate.exchange(buildUrl("/api/teachers?name=jan&language=Angielski", port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        assertEquals(1, (int) json.read("$._embedded.teachers.size()"));
        assertTrue(json.read("$._embedded.teachers[0].name").toString().contains(data.teacher_1.getName()));
    }

    @Test
    public void noReviewsForTeacher() {
        var response = restTemplate.exchange(buildUrl("/api/teachers/3", port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        var json = JsonPath.parse(response.getBody());
        assertEquals(0, (int) json.read("$.numberOfRatings"));
    }

    @Test
    public void getTeachersCourses() {
        var response = restTemplate.exchange(buildUrl("/api/teachers/1/courses", port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        assertEquals(2, (int) json.read("$._embedded.courses.size()"));
        assertTrue(json.read("$._embedded.courses[?(@.id == 1)].name").toString().contains(data.course_1.getName()));
        assertTrue(json.read("$._embedded.courses[?(@.id == 2)].name").toString().contains(data.course_2.getName()));
    }

    @Test
    public void getTeacherCoursesEmpty() {
        var response = restTemplate.exchange(
                buildUrl("/api/teachers/3/courses", port),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        assertEquals(0, (int) json.read("$._embedded.courses.size()"));
    }

    @Test
    public void getTeacherCoursesFollowTeacherLink() {
        var response = restTemplate.exchange(
                buildUrl("/api/teachers/1/courses", port),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());

        JSONArray courses = json.read("$._embedded.courses");
        assertEquals(2, courses.size());

        var teacherLink = json.read("$._links.teacher.href").toString();

        var response2 = restTemplate.exchange(
                teacherLink,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(200), response2.getStatusCode());
        var jsonTeacher = JsonPath.parse(response2.getBody());
        assertEquals(teacherLink, jsonTeacher.read("$._links.self.href").toString());
    }
}
