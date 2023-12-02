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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
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

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    private static final User USER_1 = new User("user_1", "user@example.com", "$2a$12$vyx87ILAKlC2hkoh80nbMe0iXubtm/vgclOS22/Mj8BqToMyPDhb2", "ROLE_USER", true); // password
    private static final User USER_2 = new User("user_2", "user2@example.com", "$2a$12$vyx87ILAKlC2hkoh80nbMe0iXubtm/vgclOS22/Mj8BqToMyPDhb2", "ROLE_USER", true); // password
    private static final Teacher TEACHER_1 = new Teacher("mgr. Jan Kowalski");
    private static final Teacher TEACHER_2 = new Teacher("mgr. Ann Nowak");

    private static final Course COURSE_1 = new Course("Angielski w biznesie", "Angielski", "Biznesowy", "B2+", null, TEACHER_1);
    private static final Course COURSE_2 = new Course("Język angielski poziom C1", "Angielski", "Ogólny", "C1", "M15", TEACHER_1);
    private static final Course COURSE_3 = new Course("Język niemiecki, poziom A2", "Niemiecki", "Akademicki", "A2", "M6", TEACHER_2);
    private static final Course COURSE_4 = new Course("Język włoski dla początkujących", "Włoski", "Akademicki", "A1", "M1", TEACHER_2);

    private static final Review REVIEW_1 = new Review(USER_1, COURSE_1, "Dobrze prowadzony kurs, wymagający nauczyciel", 8);
    private static final Review REVIEW_2 = new Review(USER_2, COURSE_1, "Zbyt duże wymagania do studentów", 3);
    private static final Review REVIEW_3 = new Review(USER_2, COURSE_4, "Świetne wprowadzenie do języka", 10);
    private static final Review REVIEW_4 = new Review(USER_1, COURSE_3, "W porządku", 6);

    private void addDummyData() {
        teacherRepository.saveAll(List.of(TEACHER_1, TEACHER_2));
        courseRepository.saveAll(List.of(COURSE_1, COURSE_2, COURSE_3, COURSE_4));
        reviewRepository.saveAll(List.of(REVIEW_1, REVIEW_2, REVIEW_3, REVIEW_4));
    }


    @BeforeEach
    public void setupDatabase() {
        userRepository.deleteAll();
        courseRepository.deleteAll();
        reviewRepository.deleteAll();
        teacherRepository.deleteAll();

        userRepository.saveAll(List.of(USER_1, USER_2));
        var token = authService.attemptLogin("user_1", "password").getAccessToken();
        headers.add("Authorization", "Bearer " + token);
    }

    // TODO
    @Test
    public void getTeacherByIdExists() {
        addDummyData();

        var response = restTemplate.exchange(
                buildUrl("/api/teachers/1", port),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        assertEquals(1, (int) json.read("$.id"));
        assertEquals(TEACHER_1.getName(), json.read("$.name"));
        assertEquals(5.5, (double) (json.read("$.averageRating")));
        assertTrue(json.read("$._links.self.href").toString().endsWith("/api/teachers/1"));
        assertTrue(json.read("$._links.courses.href").toString().contains("/api/courses?name=&language=all&module=all&type=all&level=all&teacherName=mgr.%20Jan%20Kowalski"));
        assertTrue(json.read("$._links.all.href").toString().endsWith("/api/teachers?name=&language=all"));

    }

    @Test
    public void getTeacherByIdNotExists() {
        var response = restTemplate.exchange(
                buildUrl("/api/teachers/1", port),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    public void getTeacherByIdFollowSelfLink() {
        addDummyData();

        var response = restTemplate.exchange(
                buildUrl("/api/teachers/1", port),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        var selfLink = json.read("$._links.self.href").toString();

        var selfResponse = restTemplate.exchange(
                selfLink,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(200), selfResponse.getStatusCode());
        assertEquals(response.getBody(), selfResponse.getBody());
    }

    @Test
    public void getAllTeachersDefaultFilter() {
        addDummyData();

        var response = restTemplate.exchange(
                buildUrl("/api/teachers", port),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        assertEquals(2, ((JSONArray) json.read("$._embedded.teachers")).size());
        assertTrue(json.read("$._embedded.teachers[?(@.id == 1)].name").toString().contains(TEACHER_1.getName()));
        assertTrue(json.read("$._embedded.teachers[?(@.id == 2)].name").toString().contains(TEACHER_2.getName()));
    }

    @Test
    public void getAllTeachersEmptyResult() {
        var response = restTemplate.exchange(
                buildUrl("/api/teachers", port),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        assertEquals(0, ((JSONArray) json.read("$._embedded.teachers")).size());
    }

    @Test
    public void getAllTeachersFilterByLanguage() {
        addDummyData();

        var response = restTemplate.exchange(
                buildUrl("/api/teachers?language=Włoski", port),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        assertEquals(1, ((JSONArray) json.read("$._embedded.teachers")).size());
        assertTrue(json.read("$._embedded.teachers[0].name").toString().contains(TEACHER_2.getName()));
    }

    @Test
    public void getAllTeachersFilterByName() {
        addDummyData();

        var response = restTemplate.exchange(
                buildUrl("/api/teachers?name=jan", port),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        assertEquals(1, ((JSONArray) json.read("$._embedded.teachers")).size());
        assertTrue(json.read("$._embedded.teachers[0].name").toString().contains(TEACHER_1.getName()));
    }

    @Test
    public void getAllTeachersFilterByNameAndLanguage() {
        addDummyData();

        var response = restTemplate.exchange(
                buildUrl("/api/teachers?name=jan&language=Angielski", port),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        assertEquals(1, ((JSONArray) json.read("$._embedded.teachers")).size());
        assertTrue(json.read("$._embedded.teachers[0].name").toString().contains(TEACHER_1.getName()));
    }
}
