package pl.edu.pw.pap.comment;

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
import pl.edu.pw.pap.course.CourseRepository;
import pl.edu.pw.pap.review.ReviewRepository;
import pl.edu.pw.pap.security.AuthService;
import pl.edu.pw.pap.teacher.TeacherRepository;
import pl.edu.pw.pap.user.UserRepository;
import pl.edu.pw.pap.utils.DummyData;

import java.util.LinkedHashMap;
import java.util.List;

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

    void adminLogin() {
        headers = new HttpHeaders();
        var token = authService.attemptLogin("admin", "password").getAccessToken();
        headers.add("Authorization", "Bearer " + token);
    }

    // get "/api/courses/{courseId}/reviews/{username}/comments"
    @Test
    public void getCommentsForReviewNotExists() {
        var response = restTemplate.exchange(buildUrl("/api/courses/1/reviews/user_3/comments", port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        assertEquals("[]", json.read("$._embedded.comments").toString());

    }

    @Test
    public void getCommentsForReviewSingleExists() {
        var response = restTemplate.exchange(buildUrl("/api/courses/1/reviews/rdeckard/comments", port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        assertEquals(1, (int) json.read("$._embedded.comments[0].id"));
        assertEquals("rel", json.read("$._embedded.comments[0].text"));
        assertEquals("user_3", json.read("$._embedded.comments[0].authorUsername"));
        assertTrue(json.read("$._embedded.comments[0].created").toString().endsWith("+00:00"));

        // check links
        assertTrue(json.read("$._links.self.href").toString().endsWith("/api/courses/1/reviews/rdeckard/comments"));
        assertTrue(json.read("$._embedded.comments[0]._links.self.href").toString().endsWith("/api/comments/1"));
        assertTrue(json.read("$._embedded.comments[0]._links.review.href").toString().endsWith("/api/courses/1/reviews/user_3"));
        assertTrue(json.read("$._embedded.comments[0]._links.user.href").toString().endsWith("/api/users/user_3"));
    }

    @Test
    public void getCommentsForReviewMultipleExist() {
        var response = restTemplate.exchange(buildUrl("/api/courses/1/reviews/rbatty/comments", port),
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
        data.deleteAll();
        var response = restTemplate.exchange(buildUrl("/api/comments/1", port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    public void getCommentByIdExists() {
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
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    public void getCommentsByUsernameSingleExists() {
        var response = restTemplate.exchange(buildUrl("/api/users/rdeckard/comments", port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());

        assertEquals("przesada", json.read("$._embedded.comments[0].text"));
        assertEquals("rdeckard", json.read("$._embedded.comments[0].authorUsername"));
        assertNotNull(json.read("$._embedded.comments[0].created"));
        String userLink = json.read("$._links.user.href");
        assertTrue(userLink.endsWith("api/users/rdeckard"));
    }

    @Test
    public void getCommentsByUsernameMultipleExist() {
        var response = restTemplate.exchange(buildUrl("/api/users/user_3/comments", port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        List<LinkedHashMap<String, String>> comments = json.read("$._embedded.comments");
        assertEquals(2, comments.size());

        int pos = 0;
        assertEquals(1, comments.get(pos).get("id"));
        assertEquals("user_3", comments.get(pos).get("authorUsername"));
        assertEquals("rel", comments.get(pos).get("text"));
        assertTrue(comments.get(pos).containsKey("created"));

        pos = 1;
        assertEquals(5, comments.get(pos).get("id"));
        assertEquals("user_3", comments.get(pos).get("authorUsername"));
        assertEquals("oj tak", comments.get(pos).get("text"));
        assertTrue(comments.get(pos).containsKey("created"));
    }

    // post "/api/courses/{courseId}/reviews/{reviewerUsername}/comments"

    @Test
    public void addCommentCourseNotExists() {
        var request = new AddCommentRequest("ok");
        var response = restTemplate.exchange(buildUrl("/api/courses/420/reviews/user_1/comments", port),
                HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    public void addCommentReviewNotExists() {
        String endpoint = "/api/courses/4/reviews/user_1/comments";
        var request = new AddCommentRequest("ok");
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());

    }

    @Test
    public void addFirstCommentForReview() {
        String endpoint = "/api/courses/4/reviews/rbatty/comments";
        var request = new AddCommentRequest("ok");
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var commentJson = JsonPath.parse(response.getBody());

        // check links
        assertTrue(commentJson.read("$._links.self.href").toString().endsWith("/api/comments/6"));
        assertTrue(commentJson.read("$._links.user.href").toString().endsWith("/api/users/rdeckard"));
        assertTrue(commentJson.read("$._links.review.href").toString().endsWith("/api/courses/4/reviews/rbatty"));


        response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        LinkedHashMap<String, String> com = json.read("$._embedded.comments[0]");
        assertEquals("ok", com.get("text"));
        assertEquals("rdeckard", com.get("authorUsername"));
        assertTrue(com.containsKey("id"));
        assertTrue(com.containsKey("created"));

    }

    @Test
    public void addSecondCommentForReview() {
        String endpoint = "/api/courses/3/reviews/rdeckard/comments";
        var request = new AddCommentRequest("ok");
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var commentJson = JsonPath.parse(response.getBody());

        // check links
        assertTrue(commentJson.read("$._links.self.href").toString().endsWith("/api/comments/6"));
        assertTrue(commentJson.read("$._links.user.href").toString().endsWith("/api/users/rdeckard"));
        assertTrue(commentJson.read("$._links.review.href").toString().endsWith("/api/courses/3/reviews/rdeckard"));


        response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        LinkedHashMap<String, String> com = json.read("$._embedded.comments[1]");
        assertEquals("ok", com.get("text"));
        assertEquals("rdeckard", com.get("authorUsername"));
        assertTrue(com.containsKey("id"));
        assertTrue(com.containsKey("created"));
    }

    // delete "/api/comments/{commentId}"
    @Test
    public void deleteCommentNotExists() {
        var response = restTemplate.exchange(buildUrl("/api/comments/420", port),
                HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());
    }

    @Test
    public void deleteCommentDifferentUser() {
        String endpoint = "/api/comments/1"; // by user_3
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(403), response.getStatusCode());
        // make sure comment was not deleted
        response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    public void deleteCommentByAuthor() {
        String endpoint = "/api/comments/3"; // by user_1
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());

        response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    public void deleteCommentByAdmin() {
        adminLogin();
        String endpoint = "/api/comments/1";
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());

        response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }


    @Test
    public void updateCommentByAuthor() {
        String endpoint = "/api/comments/3"; // by user 1 rdeckard
        var request = new UpdateCommentRequest("nowy tekscik");
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.PUT, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var returnedComment = JsonPath.parse(response.getBody());
        assertEquals("nowy tekscik", returnedComment.read("text"));
        assertEquals("rdeckard", returnedComment.read("authorUsername"));
        assertEquals(true, returnedComment.read("edited"));

        // check links
        assertTrue(returnedComment.read("$._links.self.href").toString().endsWith("/api/comments/3"));
        assertTrue(returnedComment.read("$._links.user.href").toString().endsWith("/api/users/rdeckard"));
        assertTrue(returnedComment.read("$._links.review.href").toString().endsWith("/api/courses/1/reviews/rbatty"));


        response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var commentJson = JsonPath.parse(response.getBody());

        assertEquals("nowy tekscik", commentJson.read("text"));
        assertEquals("rdeckard", commentJson.read("authorUsername"));


        // check links
        assertTrue(commentJson.read("$._links.self.href").toString().endsWith("/api/comments/3"));
        assertTrue(commentJson.read("$._links.user.href").toString().endsWith("/api/users/rdeckard"));
        assertTrue(commentJson.read("$._links.review.href").toString().endsWith("/api/courses/1/reviews/rbatty"));


    }

    @Test
    public void updateCommentByOtherUser() {
        String endpoint = "/api/comments/1"; // by user 3 user_3
        var request = new UpdateCommentRequest("nowy tekscik");
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.PUT, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(403), response.getStatusCode());

        response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var commentJson = JsonPath.parse(response.getBody());

        // make sure nothing was changed
        assertEquals("rel", commentJson.read("text"));
        assertEquals("user_3", commentJson.read("authorUsername"));
        assertEquals(false, commentJson.read("edited"));


    }


    @Test
    public void updateCommentByAdmin() {
        adminLogin();

        String endpoint = "/api/comments/1"; // by user 3 user_3
        var request = new UpdateCommentRequest("nowy tekscik");
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.PUT, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(403), response.getStatusCode());

        response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var commentJson = JsonPath.parse(response.getBody());

        // make sure nothing was changed
        assertEquals("rel", commentJson.read("text"));
        assertEquals("user_3", commentJson.read("authorUsername"));
        assertEquals(false, commentJson.read("edited"));


    }

    @Test
    public void updateCommentNotExist() {
        String endpoint = "/api/comments/6"; // doesnt exist
        var request = new UpdateCommentRequest("nowy tekscik");

        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.PUT, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    public void updateCommentNotExistByAdmin() {
        adminLogin();
        String endpoint = "/api/comments/6"; // doesnt exist
        var request = new UpdateCommentRequest("nowy tekscik");

        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.PUT, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }


}


