package pl.edu.pw.pap.comment;

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
import pl.edu.pw.pap.review.AddReviewRequest;
import pl.edu.pw.pap.review.Review;
import pl.edu.pw.pap.review.ReviewRepository;
import pl.edu.pw.pap.security.AuthService;
import pl.edu.pw.pap.teacher.Teacher;
import pl.edu.pw.pap.teacher.TeacherRepository;
import pl.edu.pw.pap.user.User;
import pl.edu.pw.pap.user.UserRepository;

import java.util.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static pl.edu.pw.pap.utils.UrlBuilder.buildUrl;

@SpringBootTest(classes = PapApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CommentIntegrationTest {

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
    CommentRepository commentRepository;
    @Autowired
    AuthService authService;

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    private static final User USER_1 = new User("user_1", "user@example.com", "$2a$12$vyx87ILAKlC2hkoh80nbMe0iXubtm/vgclOS22/Mj8BqToMyPDhb2", "ROLE_USER", true); // password
    private static final User USER_2 = new User("user_2", "user2@example.com", "$2a$12$vyx87ILAKlC2hkoh80nbMe0iXubtm/vgclOS22/Mj8BqToMyPDhb2", "ROLE_USER", true); // password
    private static final User USER_3 = new User("user_3", "user3@example.com", "$2a$12$vyx87ILAKlC2hkoh80nbMe0iXubtm/vgclOS22/Mj8BqToMyPDhb2", "ROLE_USER", true); // password
    private static final User USER_4 = new User("user_4", "use4@example.com", "$2a$12$vyx87ILAKlC2hkoh80nbMe0iXubtm/vgclOS22/Mj8BqToMyPDhb2", "ROLE_USER", true); // password
    private static final User ADMIN = new User("admin", "user2@example.com", "$2a$12$vyx87ILAKlC2hkoh80nbMe0iXubtm/vgclOS22/Mj8BqToMyPDhb2", "ROLE_ADMIN", true); // password

    private static final Teacher TEACHER_1 = new Teacher("mgr. Jan Kowalski");
    private static final Teacher TEACHER_2 = new Teacher("mgr. Ann Nowak");

    private static final Course COURSE_1 = new Course("Angielski w biznesie", "Angielski", "Biznesowy", "B2+", null, TEACHER_1);
    private static final Course COURSE_2 = new Course("Język angielski poziom C1", "Angielski", "Ogólny", "C1", "M15", TEACHER_1);
    private static final Course COURSE_3 = new Course("Język niemiecki, poziom A2", "Niemiecki", "Akademicki", "A2", "M6", TEACHER_2);
    private static final Course COURSE_4 = new Course("Język włoski dla początkujących", "Włoski", "Akademicki", "A1", "M1", TEACHER_2);

    private static final Review REVIEW_11 = new Review(USER_1, COURSE_1, "Dobrze prowadzony kurs, wymagający nauczyciel", 8);
    private static final Review REVIEW_21 = new Review(USER_2, COURSE_1, "Zbyt duże wymagania do studentów", 3);
    private static final Review REVIEW_24 = new Review(USER_2, COURSE_4, "Świetne wprowadzenie do języka", 10);
    private static final Review REVIEW_13 = new Review(USER_1, COURSE_3, "W porządku", 6);

    private static final Comment COMMENT_11_3 = new Comment("rel", REVIEW_11, USER_3);
    private static final Comment COMMENT_13_2 = new Comment("czyli co?", REVIEW_13, USER_2);
    private static final Comment COMMENT_21_1 = new Comment("przesada", REVIEW_21, USER_1);
    private static final Comment COMMENT_21_2 = new Comment("trudne serio", REVIEW_21, USER_2);
    private static final Comment COMMENT_21_3 = new Comment("oj tak", REVIEW_21, USER_3);
    // private static final Comment COMMENT_24_3 = new Comment("zgadzam się", REVIEW_24, USER_3);

    private void addDummyData() {
        teacherRepository.saveAll(List.of(TEACHER_1, TEACHER_2));
        courseRepository.saveAll(List.of(COURSE_1, COURSE_2, COURSE_3, COURSE_4));
        reviewRepository.saveAll(List.of(REVIEW_11, REVIEW_21, REVIEW_24, REVIEW_13));
        commentRepository.saveAll(List.of(COMMENT_11_3, COMMENT_21_1, COMMENT_13_2, COMMENT_21_2, COMMENT_21_3));
    }


    @BeforeEach
    public void setupDatabase() {
        userRepository.deleteAll();
        courseRepository.deleteAll();
        reviewRepository.deleteAll();
        teacherRepository.deleteAll();

        userRepository.saveAll(List.of(USER_1, USER_2, USER_3, USER_4, ADMIN));
        var token = authService.attemptLogin("user_1", "password").getAccessToken();
        headers.add("Authorization", "Bearer " + token);
    }

    void adminLogin() {
        headers = new HttpHeaders();
        var token = authService.attemptLogin("admin", "password").getAccessToken();
        headers.add("Authorization", "Bearer " + token);
    }

    // get "/api/courses/{courseId}/reviews/{username}/comments"
    @Test
    public void getCommentsForReviewNotExists() {
        addDummyData();
        var response = restTemplate.exchange(buildUrl("/api/courses/1/reviews/user_3/comments", port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        assertEquals("[]", json.read("$._embedded.comments").toString());

    }
    @Test
    public void getCommentsForReviewSingleExists() {
        addDummyData();
        var response = restTemplate.exchange(buildUrl("/api/courses/1/reviews/user_1/comments", port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        assertEquals(1, (int) json.read("$._embedded.comments[0].id"));
        assertEquals("rel", json.read("$._embedded.comments[0].text"));
        assertEquals("user_3", json.read("$._embedded.comments[0].authorUsername"));
        assertTrue(json.read("$._embedded.comments[0].created").toString().endsWith("+00:00"));

        // check links
        assertTrue(json.read("$._links.self.href").toString().endsWith("/api/courses/1/reviews/user_1/comments"));
        assertTrue(json.read("$._embedded.comments[0]._links.self.href").toString().endsWith("/api/comments/1"));
        assertTrue(json.read("$._embedded.comments[0]._links.review.href").toString().endsWith("/api/courses/1/reviews/user_3"));
        assertTrue(json.read("$._embedded.comments[0]._links.user.href").toString().endsWith("/api/users/user_3"));
    }
    @Test
    public void getCommentsForReviewMultipleExist() {
        addDummyData();
        var response = restTemplate.exchange(buildUrl("/api/courses/1/reviews/user_2/comments", port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        List<String> comments = json.read("$._embedded.comments");
        assertEquals(3, comments.size());
        String comString = comments.toString();
        assertTrue(comString.contains("\"text\":\"przesada\""));
        assertTrue(comString.contains("\"text\":\"oj tak\""));
        assertTrue(comString.contains("\"text\":\"trudne serio\""));
    }


    // get "/api/comments/{commentId}"
    @Test
    public void getCommentByIdNotExists() {
        var response = restTemplate.exchange(buildUrl("/api/comments/1", port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }
    @Test
    public void getCommentByIdExists() {
        addDummyData();
        var response = restTemplate.exchange(buildUrl("/api/comments/1", port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        LinkedHashMap<String, String> com = json.read("$");
        assertEquals(1, com.get("id"));
        assertEquals("rel", com.get("text"));
        assertEquals("user_3", com.get("authorUsername"));
        assertTrue(com.containsKey("created"));
    }

    // get "/api/users/{username}/comments"
    @Test
    public void getCommentsByUsernameNotExists() {
        var response = restTemplate.exchange(buildUrl("/api/users/nope/comments", port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        // TODO: what is the behaviour we want? fail or succeed empty?
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        fail();
    }

    @Test
    public void getCommentsByUsernameSingleExists() {
        // COMMENT_21_1 no. 3
        addDummyData();
        var response = restTemplate.exchange(buildUrl("/api/users/user_1/comments", port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        LinkedHashMap<String, String> com = json.read("$._embedded.comments[0]");
        assertEquals(2, com.get("id"));
        assertEquals("przesada", com.get("text"));
        assertEquals("user_1", com.get("authorUsername"));
        assertTrue(com.containsKey("created"));
        String userLink = json.read("$._links.user.href");
        assertTrue(userLink.endsWith("api/users/user_1"));
    }
    @Test
    public void getCommentsByUsernameMultipleExist() {
        addDummyData();
        var response = restTemplate.exchange(buildUrl("/api/users/user_3/comments", port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        List<LinkedHashMap<String, String>> comments = json.read("$._embedded.comments");
        assertEquals(2, comments.size());

        int pos =  0;
        assertEquals(1, comments.get(pos).get("id"));
        assertEquals("user_3", comments.get(pos).get("authorUsername"));
        assertEquals("rel", comments.get(pos).get("text"));
        assertTrue(comments.get(pos).containsKey("created"));

        pos =  1;
        assertEquals(5, comments.get(pos).get("id"));
        assertEquals("user_3", comments.get(pos).get("authorUsername"));
        assertEquals("oj tak", comments.get(pos).get("text"));
        assertTrue(comments.get(pos).containsKey("created"));
    }

    // post "/api/courses/{courseId}/reviews/{reviewerUsername}/comments"

    @Test
    public void addCommentCourseNotExists() {
        addDummyData();
        var request = new AddCommentRequest("ok");
        var response = restTemplate.exchange(buildUrl("/api/courses/420/reviews/user_1/comments", port),
                HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }
    @Test
    public void addCommentReviewNotExists() {
        addDummyData();
        var request = new AddCommentRequest("ok");
        var response = restTemplate.exchange(buildUrl("/api/courses/4/reviews/user_1/comments", port),
                HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());

    }
    @Test
    public void addFirstCommentForReview() {
        addDummyData();
        String endpoint = "/api/courses/4/reviews/user_2/comments";
        var request = new AddCommentRequest("ok");
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        LinkedHashMap<String, String> com = json.read("$._embedded.comments[0]");
        assertEquals("ok", com.get("text"));
        assertEquals("user_1", com.get("authorUsername"));
        assertTrue(com.containsKey("id"));
        assertTrue(com.containsKey("created"));
    }
    @Test
    public void addSecondCommentForReview() {
        addDummyData();
        String endpoint = "/api/courses/3/reviews/user_1/comments";
        var request = new AddCommentRequest("ok");
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        LinkedHashMap<String, String> com = json.read("$._embedded.comments[1]");
        assertEquals("ok", com.get("text"));
        assertEquals("user_1", com.get("authorUsername"));
        assertTrue(com.containsKey("id"));
        assertTrue(com.containsKey("created"));
    }

    // delete "/api/comments/{commentId}"
    @Test
    public void deleteCommentNotExists() {
        addDummyData();
        var response = restTemplate.exchange(buildUrl("/api/comments/420", port),
                HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());
    }
    @Test
    public void deleteCommentDifferentUser() {;
        addDummyData();
        String endpoint = "/api/comments/1"; // by user_3
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        // TODO: what do we expect?
        assertEquals(HttpStatusCode.valueOf(403), response.getStatusCode());
        fail();
    }
    @Test
    public void deleteCommentExistsGoodUser() {
        addDummyData();
        String endpoint = "/api/comments/2";
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());

        response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }
    @Test
    public void deleteCommentByAdmin() {
        addDummyData();
        adminLogin();
        String endpoint = "/api/comments/1";
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());

        response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }
}


