package pl.edu.pw.pap;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PapApplicationTests {

    @Test
    void shouldDoArithmetic() {
        assertEquals(4, 2 + 2);
    }

}
