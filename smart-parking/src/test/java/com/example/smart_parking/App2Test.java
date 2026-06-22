package com.example.smart_parking;

import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Test;

public class App2Test {

    private App app;

    @Before
    public void setup() {
        app = new App();
    }

    @Test
    public void testFailing() {
        assertThat(app).isNull();
    }

}