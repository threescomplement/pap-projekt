package pl.edu.pw.pap.teacher;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.annotation.DirtiesContext;
import pl.edu.pw.pap.PapApplication;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(classes = PapApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TeacherIntegrationTests {

    @LocalServerPort
    private int port;

    @MockBean
    private JavaMailSender fakeEmailSender;

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    // TODO
    @Test
    public void getTeacherByIdExists() {
        fail();
    }

    @Test
    public void getTeacherByIdNotExists() {
        fail();
    }

    @Test
    public void getTeacherByIdFollowSelfLink() {
        fail();
    }

    @Test
    public void getTeacherByIdFollowCoursesLink() {
        fail();
    }

    @Test
    public void getTeacherByIdFollowAllLink() {
        fail();
    }

    @Test
    public void getAllTeachersDefaultFilter() {
        fail();
    }

    @Test
    public void getAllTeachersFilterByName() {
        fail();
    }

    @Test
    public void getAllTeachersFollowSelfLink() {
        fail();
    }
}
