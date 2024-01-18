package pl.edu.pw.pap.reports;

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
import pl.edu.pw.pap.comment.report.CommentReportRepository;
import pl.edu.pw.pap.course.CourseRepository;
import pl.edu.pw.pap.report.ReportRequest;
import pl.edu.pw.pap.review.ReviewRepository;
import pl.edu.pw.pap.review.report.ReviewReportRepository;
import pl.edu.pw.pap.security.AuthService;
import pl.edu.pw.pap.teacher.TeacherRepository;
import pl.edu.pw.pap.user.UserRepository;
import pl.edu.pw.pap.utils.DummyData;

import java.util.List;

import static pl.edu.pw.pap.utils.UrlBuilder.buildUrl;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = PapApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReportIntegrationTests {

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
    ReviewReportRepository reviewReportRepository;
    @Autowired
    CommentReportRepository commentReportRepository;
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

    public void checkUnchangedData() {
        // checks if the amount is the same as before and comment and review reports are in the same order
        //
        adminLogin();
        String endpoint = "/api/admin/reports/all";
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        var json = JsonPath.parse(response.getBody());
        List<String> reports = json.read("$._embedded.reports");
        assertEquals(6, reports.size());

        assertEquals("rdeckard", json.read("$._embedded.reports[0].reportingUsername"));
        assertEquals("obelgi w strone prowadzacego", json.read("$._embedded.reports[0].reason"));
        assertEquals("Zbyt duże wymagania do studentów", json.read("$._embedded.reports[0].reportedText"));
        assertEquals("rbatty", json.read("$._embedded.reports[0].reviewerUsername"));
        assertEquals(1, (int) json.read("$._embedded.reports[0].courseId"));
        assertTrue(json.read("$._embedded.reports[0]._links.self.href").toString().endsWith("/api/admin/reports/reviews/1"));
        assertTrue(json.read("$._embedded.reports[0]._links.entity.href").toString().endsWith("/api/courses/1/reviews/rbatty"));
        assertTrue(json.read("$._embedded.reports[0]._links.review.href").toString().endsWith("/api/courses/1/reviews/rbatty"));

        // last reviewReport is the same as original
        assertEquals("user_3", json.read("$._embedded.reports[2].reportingUsername"));
        assertEquals("", json.read("$._embedded.reports[2].reason"));
        assertEquals("W porządku", json.read("$._embedded.reports[2].reportedText"));
        assertEquals("rdeckard", json.read("$._embedded.reports[2].reviewerUsername"));
        assertEquals(3, (int) json.read("$._embedded.reports[2].courseId"));

        // first comment report is the same
        assertEquals("user_3", json.read("$._embedded.reports[3].reportingUsername"));
        assertEquals("brak kultury", json.read("$._embedded.reports[3].reason"));
        assertEquals("czyli co?", json.read("$._embedded.reports[3].reportedText"));
        assertEquals("rdeckard", json.read("$._embedded.reports[3].reviewerUsername"));
        assertEquals(3, (int) json.read("$._embedded.reports[3].courseId"));
        assertTrue(json.read("$._embedded.reports[3]._links.self.href").toString().endsWith("/api/admin/reports/comments/1"));
        assertTrue(json.read("$._embedded.reports[3]._links.entity.href").toString().endsWith("/api/comments/2"));
        assertTrue(json.read("$._embedded.reports[3]._links.review.href").toString().endsWith("/api/courses/3/reviews/rdeckard"));

        // last commentReport is the same
        assertEquals("rbatty", json.read("$._embedded.reports[5].reportingUsername"));
        assertEquals("jajo", json.read("$._embedded.reports[5].reason"));
        assertEquals("czyli co?", json.read("$._embedded.reports[5].reportedText"));
        assertEquals("rdeckard", json.read("$._embedded.reports[5].reviewerUsername"));
        assertEquals(3, (int) json.read("$._embedded.reports[5].courseId"));


        //links
        assertTrue(json.read("$._links.self.href").toString().endsWith(("/api/admin/reports/all")));
    }

    @Test
    public void getAllReportsNormalUser() {

        String endpoint = "/api/admin/reports/all";
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode()); // Unauthorised
        checkUnchangedData();
    }

    @Test
    public void deleteReviewReportNormalUser() {

        String endpoint = "/api/admin/reports/reviews/1";
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());  // Unauthorised
        checkUnchangedData();
    }

    @Test
    public void deleteNonExistentReviewReportNormalUser() {

        String endpoint = "/api/admin/reports/reviews/15";
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());  // Unauthorised
        checkUnchangedData();
    }

    @Test
    public void deleteCommentReportNormalUser() {

        String endpoint = "/api/admin/reports/comments/1";
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode()); // Unauthorised
        checkUnchangedData();
    }

    @Test
    public void deleteNonExistentCommentReportNormalUser() {

        String endpoint = "/api/admin/reports/comments/15";
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode()); // Unauthorised
        checkUnchangedData();
    }

    @Test
    public void getAllReportsAdmin() {
        adminLogin();
        String endpoint = "/api/admin/reports/all";
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        var json = JsonPath.parse(response.getBody());
        List<String> reports = json.read("$._embedded.reports");
        assertEquals(6, reports.size());

        assertEquals("rdeckard", json.read("$._embedded.reports[0].reportingUsername"));
        assertEquals("obelgi w strone prowadzacego", json.read("$._embedded.reports[0].reason"));
        assertEquals("Zbyt duże wymagania do studentów", json.read("$._embedded.reports[0].reportedText"));
        assertEquals("rbatty", json.read("$._embedded.reports[0].reviewerUsername"));
        assertEquals(1, (int) json.read("$._embedded.reports[0].courseId"));
        assertTrue(json.read("$._embedded.reports[0]._links.self.href").toString().endsWith("/api/admin/reports/reviews/1"));
        assertTrue(json.read("$._embedded.reports[0]._links.entity.href").toString().endsWith("/api/courses/1/reviews/rbatty"));
        assertTrue(json.read("$._embedded.reports[0]._links.review.href").toString().endsWith("/api/courses/1/reviews/rbatty"));


        assertEquals("user_3", json.read("$._embedded.reports[3].reportingUsername"));
        assertEquals("brak kultury", json.read("$._embedded.reports[3].reason"));
        assertEquals("czyli co?", json.read("$._embedded.reports[3].reportedText"));

        assertTrue(json.read("$._embedded.reports[3]._links.self.href").toString().endsWith("/api/admin/reports/comments/1"));
        assertTrue(json.read("$._embedded.reports[3]._links.entity.href").toString().endsWith("/api/comments/2"));
        assertTrue(json.read("$._embedded.reports[3]._links.review.href").toString().endsWith("/api/courses/3/reviews/rdeckard"));

        //links
        assertTrue(json.read("$._links.self.href").toString().endsWith(("/api/admin/reports/all")));
    }

    @Test
    public void addReviewReportAdmin() {
        adminLogin();
        String endpoint = "/api/courses/1/reviews/rdeckard/reports";
        var request = new ReportRequest("zle mu patrzy z oczu");
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var returnedReport = JsonPath.parse(response.getBody());
        assertEquals("admin", returnedReport.read("$.reportingUsername"));
        assertEquals("zle mu patrzy z oczu", returnedReport.read("$.reason"));
        assertEquals("Dobrze prowadzony kurs, wymagający nauczyciel", returnedReport.read("$.reportedText"));
        assertEquals("rdeckard", returnedReport.read("$.reviewerUsername"));
        assertEquals(1, (int) returnedReport.read("$.courseId"));
        assertTrue(returnedReport.read("$._links.self.href").toString().endsWith("/api/admin/reports/reviews/4"));
        assertTrue(returnedReport.read("$._links.entity.href").toString().endsWith("/api/courses/1/reviews/rdeckard"));
        assertTrue(returnedReport.read("$._links.review.href").toString().endsWith("/api/courses/1/reviews/rdeckard"));


        endpoint = "/api/admin/reports/all";
        response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        var json = JsonPath.parse(response.getBody());
        List<String> reports = json.read("$._embedded.reports");
        assertEquals(7, reports.size());

        assertEquals("rdeckard", json.read("$._embedded.reports[0].reportingUsername"));
        assertEquals("obelgi w strone prowadzacego", json.read("$._embedded.reports[0].reason"));
        assertEquals("Zbyt duże wymagania do studentów", json.read("$._embedded.reports[0].reportedText"));
        assertEquals("rbatty", json.read("$._embedded.reports[0].reviewerUsername"));
        assertEquals(1, (int) json.read("$._embedded.reports[0].courseId"));
        assertTrue(json.read("$._embedded.reports[0]._links.entity.href").toString().endsWith("/api/courses/1/reviews/rbatty"));
        assertTrue(json.read("$._embedded.reports[0]._links.review.href").toString().endsWith("/api/courses/1/reviews/rbatty"));

        assertEquals("admin", json.read("$._embedded.reports[3].reportingUsername"));
        assertEquals("zle mu patrzy z oczu", json.read("$._embedded.reports[3].reason"));
        assertEquals("Dobrze prowadzony kurs, wymagający nauczyciel", json.read("$._embedded.reports[3].reportedText"));
        assertEquals("rdeckard", json.read("$._embedded.reports[3].reviewerUsername"));
        assertEquals(1, (int) json.read("$._embedded.reports[3].courseId"));
        assertTrue(json.read("$._embedded.reports[3]._links.self.href").toString().endsWith("/api/admin/reports/reviews/4"));
        assertTrue(json.read("$._embedded.reports[3]._links.entity.href").toString().endsWith("/api/courses/1/reviews/rdeckard"));
        assertTrue(json.read("$._embedded.reports[3]._links.review.href").toString().endsWith("/api/courses/1/reviews/rdeckard"));
    }

    @Test
    public void addReviewReportNormalUser() {
        String endpoint = "/api/courses/1/reviews/rdeckard/reports";
        var request = new ReportRequest("zle mu patrzy z oczu");
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        var returnedReport = JsonPath.parse(response.getBody());
        assertEquals("rdeckard", returnedReport.read("$.reportingUsername"));
        assertEquals("zle mu patrzy z oczu", returnedReport.read("$.reason"));
        assertEquals("Dobrze prowadzony kurs, wymagający nauczyciel", returnedReport.read("$.reportedText"));
        assertEquals("rdeckard", returnedReport.read("$.reviewerUsername"));
        assertEquals(1,(int) returnedReport.read("$.courseId"));
        assertTrue(returnedReport.read("$._links.self.href").toString().endsWith("/api/admin/reports/reviews/4"));
        assertTrue(returnedReport.read("$._links.entity.href").toString().endsWith("/api/courses/1/reviews/rdeckard"));
        assertTrue(returnedReport.read("$._links.review.href").toString().endsWith("/api/courses/1/reviews/rdeckard"));


        //check if it got added
        adminLogin();
        endpoint = "/api/admin/reports/all";
        response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        var json = JsonPath.parse(response.getBody());
        List<String> reports = json.read("$._embedded.reports");
        assertEquals(7, reports.size());

        assertEquals("rdeckard", json.read("$._embedded.reports[0].reportingUsername"));
        assertEquals("obelgi w strone prowadzacego", json.read("$._embedded.reports[0].reason"));
        assertEquals("Zbyt duże wymagania do studentów", json.read("$._embedded.reports[0].reportedText"));
        assertEquals("rbatty", json.read("$._embedded.reports[0].reviewerUsername"));
        assertEquals(1, (int) json.read("$._embedded.reports[0].courseId"));
        assertTrue(json.read("$._embedded.reports[0]._links.entity.href").toString().endsWith("/api/courses/1/reviews/rbatty"));
        assertTrue(json.read("$._embedded.reports[0]._links.review.href").toString().endsWith("/api/courses/1/reviews/rbatty"));

        assertEquals("rdeckard", json.read("$._embedded.reports[3].reportingUsername"));
        assertEquals("zle mu patrzy z oczu", json.read("$._embedded.reports[3].reason"));
        assertEquals("Dobrze prowadzony kurs, wymagający nauczyciel", json.read("$._embedded.reports[3].reportedText"));
        assertEquals("rdeckard", json.read("$._embedded.reports[3].reviewerUsername"));
        assertEquals(1, (int) json.read("$._embedded.reports[3].courseId"));
        assertTrue(json.read("$._embedded.reports[3]._links.self.href").toString().endsWith("/api/admin/reports/reviews/4"));
        assertTrue(json.read("$._embedded.reports[3]._links.entity.href").toString().endsWith("/api/courses/1/reviews/rdeckard"));
        assertTrue(json.read("$._embedded.reports[3]._links.review.href").toString().endsWith("/api/courses/1/reviews/rdeckard"));


    }


    @Test
    public void addReviewReportAdminBadCourse() {
        adminLogin();
        String endpoint = "/api/courses/10/reviews/rdeckard/reports";
        var request = new ReportRequest("zle mu patrzy z oczu");
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        checkUnchangedData();
    }

    @Test
    public void addReviewReportAdminBadReview() {
        adminLogin();
        String endpoint = "/api/courses/2/reviews/rdeckard/reports";
        var request = new ReportRequest("zle mu patrzy z oczu");
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        checkUnchangedData();
    }

    @Test
    public void addReviewReportUserBadCourse() {
        String endpoint = "/api/courses/10/reviews/rdeckard/reports";
        var request = new ReportRequest("zle mu patrzy z oczu");
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        checkUnchangedData();
    }

    @Test
    public void addReviewReportUserBadReview() {
        String endpoint = "/api/courses/2/reviews/rdeckard/reports";
        var request = new ReportRequest("zle mu patrzy z oczu");
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        checkUnchangedData();
    }


    @Test
    public void deleteReviewReportAdmin() {
        adminLogin();
        String endpoint = "/api/admin/reports/reviews/2";
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode()); // no content


        endpoint = "/api/admin/reports/all";
        response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        var json = JsonPath.parse(response.getBody());
        List<String> reports = json.read("$._embedded.reports");
        assertEquals(5, reports.size()); // was 6 at the beginning

        assertEquals("rdeckard", json.read("$._embedded.reports[0].reportingUsername"));
        assertEquals("obelgi w strone prowadzacego", json.read("$._embedded.reports[0].reason"));
        assertEquals("Zbyt duże wymagania do studentów", json.read("$._embedded.reports[0].reportedText"));
        assertEquals("rbatty", json.read("$._embedded.reports[0].reviewerUsername"));
        assertEquals(1, (int) json.read("$._embedded.reports[0].courseId"));
        assertTrue(json.read("$._embedded.reports[0]._links.self.href").toString().endsWith("/api/admin/reports/reviews/1"));
        assertTrue(json.read("$._embedded.reports[0]._links.entity.href").toString().endsWith("/api/courses/1/reviews/rbatty"));
        assertTrue(json.read("$._embedded.reports[0]._links.review.href").toString().endsWith("/api/courses/1/reviews/rbatty"));

        assertEquals("user_3", json.read("$._embedded.reports[2].reportingUsername"));
        assertEquals("brak kultury", json.read("$._embedded.reports[2].reason"));
        assertEquals("czyli co?", json.read("$._embedded.reports[2].reportedText"));
        assertEquals("rdeckard", json.read("$._embedded.reports[2].reviewerUsername"));
        assertEquals(3, (int) json.read("$._embedded.reports[2].courseId"));
        assertTrue(json.read("$._embedded.reports[2]._links.self.href").toString().endsWith("/api/admin/reports/comments/1"));
        assertTrue(json.read("$._embedded.reports[2]._links.entity.href").toString().endsWith("/api/comments/2"));
        assertTrue(json.read("$._embedded.reports[2]._links.review.href").toString().endsWith("/api/courses/3/reviews/rdeckard"));

        //links
        assertTrue(json.read("$._links.self.href").toString().endsWith(("/api/admin/reports/all")));
    }

    @Test
    public void deleteReviewReportNonExistentAdmin() {
        adminLogin();
        String endpoint = "/api/admin/reports/reviews/5";
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode()); // no content

        checkUnchangedData();
    }


    @Test
    public void addCommentReportAdmin() {
        adminLogin();
        String endpoint = "/api/comments/1/reports";
        var request = new ReportRequest("zle mu patrzy z oczu");
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        // check return after adding
        var returnedReport = JsonPath.parse(response.getBody());
        assertEquals("admin", returnedReport.read("$.reportingUsername"));
        assertEquals("zle mu patrzy z oczu", returnedReport.read("$.reason"));
        assertEquals("rel", returnedReport.read("$.reportedText"));
        assertEquals("rdeckard", returnedReport.read("$.reviewerUsername"));
        assertEquals(1, (int) returnedReport.read("$.courseId"));
        assertTrue(returnedReport.read("$._links.self.href").toString().endsWith("/api/admin/reports/comments/4"));
        assertTrue(returnedReport.read("$._links.entity.href").toString().endsWith("/api/comments/1"));
        assertTrue(returnedReport.read("$._links.review.href").toString().endsWith("/api/courses/1/reviews/rdeckard"));


        endpoint = "/api/admin/reports/all";
        response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        var json = JsonPath.parse(response.getBody());
        List<String> reports = json.read("$._embedded.reports");
        assertEquals(7, reports.size());

        // check if old is the same
        assertEquals("rdeckard", json.read("$._embedded.reports[0].reportingUsername"));
        assertEquals("obelgi w strone prowadzacego", json.read("$._embedded.reports[0].reason"));
        assertEquals("Zbyt duże wymagania do studentów", json.read("$._embedded.reports[0].reportedText"));
        assertEquals("rbatty", json.read("$._embedded.reports[0].reviewerUsername"));
        assertEquals(1, (int) json.read("$._embedded.reports[0].courseId"));
        assertTrue(json.read("$._embedded.reports[0]._links.entity.href").toString().endsWith("/api/courses/1/reviews/rbatty"));
        assertTrue(json.read("$._embedded.reports[0]._links.review.href").toString().endsWith("/api/courses/1/reviews/rbatty"));

        // check if the new one is present
        assertEquals("admin", json.read("$._embedded.reports[6].reportingUsername"));
        assertEquals("zle mu patrzy z oczu", json.read("$._embedded.reports[6].reason"));
        assertEquals("rel", json.read("$._embedded.reports[6].reportedText"));
        assertEquals("rdeckard", json.read("$._embedded.reports[6].reviewerUsername"));
        assertEquals(1, (int) json.read("$._embedded.reports[6].courseId"));
        assertTrue(json.read("$._embedded.reports[6]._links.self.href").toString().endsWith("/api/admin/reports/comments/4"));
        assertTrue(json.read("$._embedded.reports[6]._links.entity.href").toString().endsWith("/api/comments/1"));
        assertTrue(json.read("$._embedded.reports[6]._links.review.href").toString().endsWith("/api/courses/1/reviews/rdeckard"));

    }

    @Test
    public void addCommentReportNormalUser() {
        String endpoint = "/api/comments/1/reports";
        var request = new ReportRequest("zle mu patrzy z oczu");
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        // check return after adding
        var returnedReport = JsonPath.parse(response.getBody());
        assertEquals("rdeckard", returnedReport.read("$.reportingUsername"));
        assertEquals("zle mu patrzy z oczu", returnedReport.read("$.reason"));
        assertEquals("rel", returnedReport.read("$.reportedText"));
        assertEquals("rdeckard", returnedReport.read("$.reviewerUsername"));
        assertEquals(1, (int) returnedReport.read("$.courseId"));
        assertTrue(returnedReport.read("$._links.self.href").toString().endsWith("/api/admin/reports/comments/4"));
        assertTrue(returnedReport.read("$._links.entity.href").toString().endsWith("/api/comments/1"));
        assertTrue(returnedReport.read("$._links.review.href").toString().endsWith("/api/courses/1/reviews/rdeckard"));


        adminLogin(); // log in as admin to check reports
        endpoint = "/api/admin/reports/all";
        response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        var json = JsonPath.parse(response.getBody());
        List<String> reports = json.read("$._embedded.reports");
        assertEquals(7, reports.size());

        // check if old is the same
        assertEquals("rdeckard", json.read("$._embedded.reports[0].reportingUsername"));
        assertEquals("obelgi w strone prowadzacego", json.read("$._embedded.reports[0].reason"));
        assertEquals("Zbyt duże wymagania do studentów", json.read("$._embedded.reports[0].reportedText"));
        assertEquals("rbatty", json.read("$._embedded.reports[0].reviewerUsername"));
        assertEquals(1, (int) json.read("$._embedded.reports[0].courseId"));
        assertTrue(json.read("$._embedded.reports[0]._links.entity.href").toString().endsWith("/api/courses/1/reviews/rbatty"));
        assertTrue(json.read("$._embedded.reports[0]._links.review.href").toString().endsWith("/api/courses/1/reviews/rbatty"));

        // check if the new one is present
        assertEquals("rdeckard", json.read("$._embedded.reports[6].reportingUsername"));
        assertEquals("zle mu patrzy z oczu", json.read("$._embedded.reports[6].reason"));
        assertEquals("rel", json.read("$._embedded.reports[6].reportedText"));
        assertEquals("rdeckard", json.read("$._embedded.reports[6].reviewerUsername"));
        assertEquals(1, (int) json.read("$._embedded.reports[6].courseId"));
        assertTrue(json.read("$._embedded.reports[6]._links.self.href").toString().endsWith("/api/admin/reports/comments/4"));
        assertTrue(json.read("$._embedded.reports[6]._links.entity.href").toString().endsWith("/api/comments/1"));
        assertTrue(json.read("$._embedded.reports[6]._links.review.href").toString().endsWith("/api/courses/1/reviews/rdeckard"));

    }

    @Test
    public void addInvalidCommentReportAdmin() {
        adminLogin();
        String endpoint = "/api/comments/20/reports";
        var request = new ReportRequest("zle mu patrzy z oczu");
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        checkUnchangedData();

    }

    @Test
    public void addInvalidCommentReportUser() {
        String endpoint = "/api/comments/20/reports";
        var request = new ReportRequest("zle mu patrzy z oczu");
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.POST, new HttpEntity<>(request, headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        checkUnchangedData();
    }

    @Test
    public void deleteCommentReportAdmin() {
        adminLogin();
        String endpoint = "/api/admin/reports/comments/1";
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode()); // no content


        endpoint = "/api/admin/reports/all";
        response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        var json = JsonPath.parse(response.getBody());
        List<String> reports = json.read("$._embedded.reports");
        assertEquals(5, reports.size()); // was 6 at the beginning

        // review report stays the same
        assertEquals("rdeckard", json.read("$._embedded.reports[0].reportingUsername"));
        assertEquals("obelgi w strone prowadzacego", json.read("$._embedded.reports[0].reason"));
        assertEquals("Zbyt duże wymagania do studentów", json.read("$._embedded.reports[0].reportedText"));
        assertEquals("rbatty", json.read("$._embedded.reports[0].reviewerUsername"));
        assertEquals(1, (int) json.read("$._embedded.reports[0].courseId"));
        assertTrue(json.read("$._embedded.reports[0]._links.self.href").toString().endsWith("/api/admin/reports/reviews/1"));
        assertTrue(json.read("$._embedded.reports[0]._links.entity.href").toString().endsWith("/api/courses/1/reviews/rbatty"));
        assertTrue(json.read("$._embedded.reports[0]._links.review.href").toString().endsWith("/api/courses/1/reviews/rbatty"));

        // the comment report with id 2 moves "up" and is now the first one returned
        assertEquals("user_3", json.read("$._embedded.reports[3].reportingUsername"));
        assertEquals("skill issue", json.read("$._embedded.reports[3].reason"));
        assertEquals("trudne serio", json.read("$._embedded.reports[3].reportedText"));
        assertEquals("rbatty", json.read("$._embedded.reports[0].reviewerUsername"));
        assertEquals(1, (int) json.read("$._embedded.reports[0].courseId"));
        assertTrue(json.read("$._embedded.reports[3]._links.self.href").toString().endsWith("/api/admin/reports/comments/2"));
        assertTrue(json.read("$._embedded.reports[3]._links.entity.href").toString().endsWith("/api/comments/4"));
        assertTrue(json.read("$._embedded.reports[3]._links.review.href").toString().endsWith("/api/courses/1/reviews/rbatty"));

        //links
        assertTrue(json.read("$._links.self.href").toString().endsWith(("/api/admin/reports/all")));

    }


    @Test
    public void deleteInvalidCommentReportAdmin() {
        adminLogin();
        String endpoint = "/api/admin/reports/comments/15";
        var response = restTemplate.exchange(buildUrl(endpoint, port),
                HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());
        checkUnchangedData();
    }

    @Test
    public void deleteReportsOfRemovedReview() {
        adminLogin();
        String endpoint = "/api/courses/1/reviews/rbatty";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());

        assertEquals(1, reviewReportRepository.count());
        assertEquals(3, commentReportRepository.count());

        endpoint = "/api/courses/3/reviews/rdeckard";
        response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());

        assertEquals(0, reviewReportRepository.count());
        assertEquals(3, commentReportRepository.count());

    }

    @Test
    public void deleteReportsOfRemovedComment() {

        adminLogin();
        String endpoint = "/api/comments/2";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());

        assertEquals(3, reviewReportRepository.count());
        assertEquals(1, commentReportRepository.count());

        endpoint = "/api/comments/4";
        response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());

        assertEquals(3, reviewReportRepository.count());
        assertEquals(0, commentReportRepository.count());
    }

    @Test
    public void getSingleCommentReportAdmin(){
        adminLogin();
        String endpoint = "/api/admin/reports/comments/2";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());

        assertEquals("user_3", json.read("$.reportingUsername"));
        assertEquals("skill issue", json.read("$.reason"));
        assertEquals("trudne serio", json.read("$.reportedText"));
        assertEquals("rbatty", json.read("$.reviewerUsername"));
        assertEquals(1, (int) json.read("$.courseId"));
        assertTrue(json.read("$._links.self.href").toString().endsWith("/api/admin/reports/comments/2"));
        assertTrue(json.read("$._links.entity.href").toString().endsWith("/api/comments/4"));
        assertTrue(json.read("$._links.review.href").toString().endsWith("/api/courses/1/reviews/rbatty"));
        checkUnchangedData();
    }
    @Test
    public void getSingleNonExistentCommentReportAdmin(){
        adminLogin();
        String endpoint = "/api/admin/reports/comments/20";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        checkUnchangedData();
    }

    @Test
    public void getSingleCommentReportUser(){
        String endpoint = "/api/admin/reports/comments/2";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());
        checkUnchangedData();
    }


    @Test
    public void getSingleReviewReportAdmin(){
        adminLogin();
        String endpoint = "/api/admin/reports/reviews/1";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());


        // review report stays the same
        assertEquals("rdeckard", json.read("$.reportingUsername"));
        assertEquals("obelgi w strone prowadzacego", json.read("$.reason"));
        assertEquals("Zbyt duże wymagania do studentów", json.read("$.reportedText"));
        assertEquals(1, (int) json.read("$.courseId"));
        assertEquals("rbatty", json.read("$.reviewerUsername"));
        assertTrue(json.read("$._links.self.href").toString().endsWith("/api/admin/reports/reviews/1"));
        assertTrue(json.read("$._links.entity.href").toString().endsWith("/api/courses/1/reviews/rbatty"));
        assertTrue(json.read("$._links.review.href").toString().endsWith("/api/courses/1/reviews/rbatty"));
        checkUnchangedData();
    }

    @Test
    public void getSingleReviewReportUser() {
        String endpoint = "/api/admin/reports/reviews/1";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());
    }
    @Test
    public void getSingleInvalidReviewReportUser() {
        String endpoint = "/api/admin/reports/reviews/15";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());
    }


    @Test
    public void getSingleInvalidReviewReportAdmin(){
        adminLogin();
        String endpoint = "/api/admin/reports/reviews/15";
        var response = restTemplate.exchange(buildUrl(endpoint, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        checkUnchangedData();
    }


}