package pl.edu.pw.pap.review;

import com.jayway.jsonpath.JsonPath;
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
import pl.edu.pw.pap.security.AuthService;
import pl.edu.pw.pap.user.User;
import pl.edu.pw.pap.user.UserRepository;
import pl.edu.pw.pap.teacher.Teacher;
import pl.edu.pw.pap.teacher.TeacherRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static pl.edu.pw.pap.utils.UrlBuilder.buildUrl;

@SpringBootTest(classes = PapApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReviewIntegrationTests {

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
    private static final User USER_3 = new User("user_3", "user3@example.com", "$2a$12$vyx87ILAKlC2hkoh80nbMe0iXubtm/vgclOS22/Mj8BqToMyPDhb2", "ROLE_USER", true);
    private static final User ADMIN = new User("admin", "admin@example.com", "$2a$12$vyx87ILAKlC2hkoh80nbMe0iXubtm/vgclOS22/Mj8BqToMyPDhb2", "ROLE_ADMIN", true);
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

        userRepository.saveAll(List.of(USER_1, USER_2, USER_3, ADMIN));
        var token = authService.attemptLogin("user_1", "password").getAccessToken();
        headers.add("Authorization", "Bearer " + token);
    }

    public void adminLogin() {
        headers = new HttpHeaders();
        var token = authService.attemptLogin("admin", "password").getAccessToken();
        headers.add("Authorization", "Bearer " + token);
    }

    @Test
    public void getReviewByCourseAndUserIdExists() {
        addDummyData();
        String endpoint = "/api/courses/1/reviews/user_1";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());

        assertEquals("Dobrze prowadzony kurs, wymagający nauczyciel", json.read("$.opinion"));
        assertEquals(8, (int) json.read("$.overallRating"));
        assertEquals("user_1", json.read("$.authorUsername"));
        assertTrue(json.read("$.created").toString().endsWith("+00:00"));

        // check links
        assertTrue(json.read("$._links.self.href").toString().endsWith("/api/courses/1/reviews/user_1"));
        assertTrue(json.read("$._links.user.href").toString().endsWith("/api/users/user_1"));
        assertTrue(json.read("$._links.comments.href").toString().endsWith("/api/courses/1/reviews/user_1/comments"));
        assertTrue(json.read("$._links.course.href").toString().endsWith("/api/courses/1"));

    }

    @Test
    public void getReviewByCourseAndUserIdCourseNotExists() {
        addDummyData();
        String endpoint = "/api/courses/420/reviews/user_1";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    public void getReviewByCourseAndUserIdUserNotExists() {
        addDummyData();
        String endpoint = "/api/courses/1/reviews/iDoNotExist";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    public void getReviewNotExists() {
        addDummyData();
        String endpoint = "/api/courses/4/reviews/user_1";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    public void getReviewsByCourseIdMultiple() {
        addDummyData();
        String endpoint = "/api/courses/1/reviews";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        var json = JsonPath.parse(response.getBody());
        List<String> reviews = json.read("$._embedded.reviews");
        assertEquals(2, reviews.size());
        assertTrue(reviews.toString().contains("\"opinion\":\"Zbyt duże wymagania do studentów\""));
        assertTrue(reviews.toString().contains("\"opinion\":\"Dobrze prowadzony kurs, wymagający nauczyciel\""));
        assertTrue(json.read("$._links.self.href").toString().endsWith(("api/courses/1/reviews")));
        assertTrue(json.read("$._links.course.href").toString().endsWith(("api/courses/1")));

    }

    @Test
    public void getReviewsByCourseIdSingle() {
        addDummyData();
        String endpoint = "/api/courses/3/reviews";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        var json = JsonPath.parse(response.getBody());
        assertEquals("W porządku", json.read("$._embedded.reviews[0].opinion"));
        assertTrue(json.read("$._links.self.href").toString().endsWith(("api/courses/3/reviews")));
        assertTrue(json.read("$._links.course.href").toString().endsWith(("api/courses/3")));
    }

    @Test
    public void getReviewsByCourseIdEmpty() {
        addDummyData();
        String endpoint = "/api/courses/2/reviews";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        var json = JsonPath.parse(response.getBody());
        List<String> reviews = json.read("$._embedded.reviews");
        assertTrue(reviews.isEmpty());
        assertTrue(json.read("$._links.self.href").toString().endsWith(("api/courses/2/reviews")));
        assertTrue(json.read("$._links.course.href").toString().endsWith(("api/courses/2")));
    }

    @Test
    public void getReviewsByUsernameMultiple() {
        addDummyData();
        String endpoint = "/api/reviews/user_1";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        List<String> reviews = json.read("$._embedded.reviews");
        assertEquals("user_1", json.read("$._embedded.reviews[0].authorUsername"));
        assertEquals("user_1", json.read("$._embedded.reviews[1].authorUsername"));
        assertEquals(2, reviews.size());
        // hacky but works regardless of order
        assertTrue(reviews.toString().contains("\"opinion\":\"W porządku\""));
        assertTrue(reviews.toString().contains("\"opinion\":\"Dobrze prowadzony kurs, wymagający nauczyciel\""));
    }

    @Test
    public void getReviewsByUsernameEmpty() {
        addDummyData();
        String endpoint = "/api/reviews/user_3";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        List<String> reviews = json.read("$._embedded.reviews");
        assertTrue(reviews.isEmpty());
    }

    // POST TESTS

    @Test
    public void addNewReview() {
        addDummyData();
        String endpoint = "/api/courses/2/reviews";
        var request = new AddReviewRequest("test_opinion", 6);
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        // check if added
        endpoint = "/api/courses/2/reviews/user_1";
        response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        var json = JsonPath.parse(response.getBody());
        assertEquals("test_opinion", json.read("$.opinion"));
        assertEquals(6, (int) json.read("$.overallRating"));
    }

    @Test
    public void addDuplicateReview() {
        addDummyData();
        String endpoint = "/api/courses/1/reviews/";
        var request = new AddReviewRequest("test_opinion", 6);
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode()); // 400 BAD REQUEST
    }

    // DELETE TESTS
    @Test
    public void deleteReviewExists() {
        addDummyData();
        String endpoint = "/api/courses/1/reviews/user_1";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());

        // check if review truly deleted
        response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    public void deleteReviewNotExists() {
        addDummyData();
        String endpoint = "/api/courses/2/reviews/user_3";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());
    }

    @Test
    public void deleteReviewByAdmin() {
        addDummyData();
        adminLogin();
        String endpoint = "/api/courses/1/reviews/user_1";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());

        // check if review truly deleted
        response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    public void deleteReviewDifferentUser() {
        addDummyData();
        adminLogin();
        String endpoint = "/api/courses/1/reviews/user_2";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(403), response.getStatusCode()); // FORBIDDEN

        // check if review was not deleted
        response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

}