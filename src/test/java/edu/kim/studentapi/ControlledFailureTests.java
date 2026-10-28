package edu.kim.studentapi;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ControlledFailureTests {
    @Test
    void authorNameIsCorrect() {
        // Deliberately wrong expectation for the required CI failure demonstration.
        assertThat("Kim Eduard").isEqualTo("Incorrect Author");
    }
}
