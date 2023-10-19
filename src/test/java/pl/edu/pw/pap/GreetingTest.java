package pl.edu.pw.pap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GreetingTest {

    @Test
    public void shouldHoldData() {
        var greeting = new Greeting(10, "Hello");
        assertEquals(10, greeting.id());
        assertEquals("Hello", greeting.content());
    }
}