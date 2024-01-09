package pl.edu.pw.pap.review;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.annotation.DirtiesContext;
import pl.edu.pw.pap.PapApplication;
import pl.edu.pw.pap.course.CourseRepository;
import pl.edu.pw.pap.security.AuthService;
import pl.edu.pw.pap.teacher.TeacherRepository;
import pl.edu.pw.pap.user.UserRepository;
import pl.edu.pw.pap.utils.DummyData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    public void adminLogin() {
        headers = new HttpHeaders();
        var token = authService.attemptLogin(data.admin_1.getUsername(), "password").getAccessToken();
        headers.add("Authorization", "Bearer " + token);
    }

    @Test
    public void getReviewByCourseAndUserIdExists() {
        String endpoint = "/api/courses/1/reviews/rdeckard";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());

        assertEquals("Dobrze prowadzony kurs, wymagający nauczyciel", json.read("$.opinion"));
        assertEquals(8, (int) json.read("$.easeRating"));
        assertEquals(7, (int) json.read("$.interestRating"));
        assertEquals(6, (int) json.read("$.engagementRating"));

        assertEquals("rdeckard", json.read("$.authorUsername"));
        assertTrue(json.read("$.created").toString().endsWith("+00:00"));

        // check links
        assertTrue(json.read("$._links.self.href").toString().endsWith("/api/courses/1/reviews/rdeckard"));
        assertTrue(json.read("$._links.user.href").toString().endsWith("/api/users/rdeckard"));
        assertTrue(json.read("$._links.comments.href").toString().endsWith("/api/courses/1/reviews/rdeckard/comments"));
        assertTrue(json.read("$._links.course.href").toString().endsWith("/api/courses/1"));

    }

    @Test
    public void getReviewByCourseAndUserIdCourseNotExists() {
        String endpoint = "/api/courses/420/reviews/rdeckard";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    public void getReviewByCourseAndUserIdUserNotExists() {
        String endpoint = "/api/courses/1/reviews/iDoNotExist";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    public void getReviewNotExists() {
        String endpoint = "/api/courses/4/reviews/rdeckard";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    public void getReviewsByCourseIdMultiple() {
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
        String endpoint = "/api/reviews/rdeckard";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        List<String> reviews = json.read("$._embedded.reviews");
        assertEquals("rdeckard", json.read("$._embedded.reviews[0].authorUsername"));
        assertEquals("rdeckard", json.read("$._embedded.reviews[1].authorUsername"));
        assertEquals(2, reviews.size());
        // hacky but works regardless of order
        assertTrue(reviews.toString().contains("\"opinion\":\"W porządku\""));
        assertTrue(reviews.toString().contains("\"opinion\":\"Dobrze prowadzony kurs, wymagający nauczyciel\""));
    }

    @Test
    public void getReviewsByUsernameEmpty() {
        String endpoint = "/api/reviews/user_3";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        List<String> reviews = json.read("$._embedded.reviews");
        assertTrue(reviews.isEmpty());
    }


    @Test
    public void getTeacherReviews() {
        String endpoint = "/api/teachers/1/reviews";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        List<String> reviews = json.read("$._embedded.reviews");
        assertEquals(2, reviews.size());
        assertTrue(json.read("$._embedded.reviews[?(@.authorUsername == 'rdeckard')].opinion").toString().contains("Dobrze prowadzony kurs, wymagający nauczyciel"));
        assertTrue(json.read("$._embedded.reviews[?(@.authorUsername == 'rbatty')].opinion").toString().contains("Zbyt duże wymagania do studentów"));

        //links
        assertTrue(json.read("$._links.self.href").toString().endsWith(("/api/teachers/1/reviews")));
        assertTrue(json.read("$._links.teacher.href").toString().endsWith(("/api/teachers/1")));
    }


    @Test
    public void getNonExistentTeacherReviews() {
        String endpoint = "/api/teachers/4/reviews";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }


    // POST TESTS

    @Test
    public void addNewReview() {
        String endpoint = "/api/courses/2/reviews";
        var request = new AddReviewRequest("test_opinion", 6, 7, 8);
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        // check if added
        endpoint = "/api/courses/2/reviews/rdeckard";
        response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        var json = JsonPath.parse(response.getBody());
        assertEquals("test_opinion", json.read("$.opinion"));
        assertEquals(6, (int) json.read("$.easeRating"));
        assertEquals(7, (int) json.read("$.interestRating"));
        assertEquals(8, (int) json.read("$.engagementRating"));
    }

    @Test
    public void addDuplicateReview() {
        String endpoint = "/api/courses/1/reviews";
        var request = new AddReviewRequest("test_opinion", 6, 7, 8);
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // 400 BAD REQUEST
    }

    // DELETE TESTS
    @Test
    public void deleteReviewExists() {
        String endpoint = "/api/courses/1/reviews/rdeckard";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());

        // check if review truly deleted
        response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    public void deleteReviewNotExists() {
        String endpoint = "/api/courses/2/reviews/rdeckard";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());
    }

    @Test
    public void deleteReviewByAdmin() {
        adminLogin();
        String endpoint = "/api/courses/1/reviews/rdeckard";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());

        // check if review truly deleted
        response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    public void deleteReviewDifferentUser() {
        String endpoint = "/api/courses/1/reviews/rbatty";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(403), response.getStatusCode()); // FORBIDDEN

        // check if review was not deleted
        response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }


    @Test
    public void updateReviewByAuthor() {
        String endpoint = "/api/courses/1/reviews/rdeckard"; // by user 1 rdeckard
        var getResponse = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), getResponse.getStatusCode());
        var json = JsonPath.parse(getResponse.getBody());
        var oldDate = json.read("$.created");


        var request = new EditReviewRequest("sysy knyszy crazyfrog", 5, 6, 7);
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.PUT, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var returnedReview = JsonPath.parse(response.getBody());
        assertEquals("sysy knyszy crazyfrog", returnedReview.read("opinion"));
        assertEquals("rdeckard", returnedReview.read("authorUsername"));
        assertEquals(true, returnedReview.read("edited"));
        assertEquals(5, (int) returnedReview.read("easeRating"));
        assertEquals(6, (int) returnedReview.read("interestRating"));
        assertEquals(7, (int) returnedReview.read("engagementRating"));
        assertEquals(oldDate, returnedReview.read("created"));

        // check links
        assertTrue(returnedReview.read("$._links.self.href").toString().endsWith("/api/courses/1/reviews/rdeckard"));
        assertTrue(returnedReview.read("$._links.user.href").toString().endsWith("/api/users/rdeckard"));
        assertTrue(returnedReview.read("$._links.comments.href").toString().endsWith("/api/courses/1/reviews/rdeckard/comments"));
        assertTrue(returnedReview.read("$._links.course.href").toString().endsWith("/api/courses/1"));

        response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var reviewJson = JsonPath.parse(response.getBody());

        assertEquals("sysy knyszy crazyfrog", reviewJson.read("opinion"));
        assertEquals("rdeckard", reviewJson.read("authorUsername"));
        assertEquals(true, reviewJson.read("edited"));
        assertEquals(5, (int) returnedReview.read("easeRating"));
        assertEquals(6, (int) returnedReview.read("interestRating"));
        assertEquals(7, (int) returnedReview.read("engagementRating"));

        // check links
        assertTrue(reviewJson.read("$._links.self.href").toString().endsWith("/api/courses/1/reviews/rdeckard"));
        assertTrue(reviewJson.read("$._links.user.href").toString().endsWith("/api/users/rdeckard"));
        assertTrue(reviewJson.read("$._links.comments.href").toString().endsWith("/api/courses/1/reviews/rdeckard/comments"));
        assertTrue(reviewJson.read("$._links.course.href").toString().endsWith("/api/courses/1"));
        assertEquals(oldDate, returnedReview.read("created"));


    }

    @Test
    public void editReviewByOtherUser() {
        String endpoint = "/api/courses/1/reviews/rbatty"; //
        var request = new EditReviewRequest("sysy knyszy crazyfrog", 1, 2, 2);
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.PUT, new HttpEntity<>(request, headers), String.class);
        // Forbidden
        assertEquals(HttpStatusCode.valueOf(403), response.getStatusCode());

        response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var reviewJson = JsonPath.parse(response.getBody());

        // make sure nothing was changed
        assertEquals("Zbyt duże wymagania do studentów", reviewJson.read("opinion"));
        assertEquals("rbatty", reviewJson.read("authorUsername"));
        assertEquals(false, reviewJson.read("edited"));
        assertEquals(3, (int) reviewJson.read("easeRating"));
        assertEquals(4, (int) reviewJson.read("interestRating"));
        assertEquals(5, (int) reviewJson.read("engagementRating"));

        // check links
        assertTrue(reviewJson.read("$._links.self.href").toString().endsWith("/api/courses/1/reviews/rbatty"));
        assertTrue(reviewJson.read("$._links.user.href").toString().endsWith("/api/users/rbatty"));
        assertTrue(reviewJson.read("$._links.comments.href").toString().endsWith("/api/courses/1/reviews/rbatty/comments"));
        assertTrue(reviewJson.read("$._links.course.href").toString().endsWith("/api/courses/1"));

    }


    @Test
    public void editReviewByAdmin() {
        adminLogin();

        String endpoint = "/api/courses/1/reviews/rbatty"; //
        var request = new EditReviewRequest("sysy knyszy crazyfrog", 1, 2, 3);
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.PUT, new HttpEntity<>(request, headers), String.class);
        // Forbidden
        assertEquals(HttpStatusCode.valueOf(403), response.getStatusCode());

        response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var reviewJson = JsonPath.parse(response.getBody());

        // make sure nothing was changed
        assertEquals("Zbyt duże wymagania do studentów", reviewJson.read("opinion"));
        assertEquals("rbatty", reviewJson.read("authorUsername"));
        assertEquals(false, reviewJson.read("edited"));
        assertEquals(3, (int) reviewJson.read("easeRating"));
        assertEquals(4, (int) reviewJson.read("interestRating"));
        assertEquals(5, (int) reviewJson.read("engagementRating"));


        // check links
        assertTrue(reviewJson.read("$._links.self.href").toString().endsWith("/api/courses/1/reviews/rbatty"));
        assertTrue(reviewJson.read("$._links.user.href").toString().endsWith("/api/users/rbatty"));
        assertTrue(reviewJson.read("$._links.comments.href").toString().endsWith("/api/courses/1/reviews/rbatty/comments"));
        assertTrue(reviewJson.read("$._links.course.href").toString().endsWith("/api/courses/1"));


    }

    @Test
    public void editReviewCourseNotExist() {
        String endpoint = "/api/courses/6/reviews/rdeckard"; // doesnt exist
        var request = new EditReviewRequest("sysy knyszy crazyfrog", 1, 1, 1);

        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.PUT, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }


    @Test
    public void editReviewUserNotExist() {
        String endpoint = "/api/courses/1/reviews/sysomat"; // doesnt exist
        var request = new EditReviewRequest("sysy knyszy crazyfrog", 1, 2, 3);

        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.PUT, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }


    @Test
    public void editReviewCourseNotExistByAdmin() {
        adminLogin();
        String endpoint = "/api/courses/5/reviews/rbatty"; // doesnt exist
        var request = new EditReviewRequest("sysy knyszy crazyfrog", 1, 2, 4);

        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.PUT, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }


}