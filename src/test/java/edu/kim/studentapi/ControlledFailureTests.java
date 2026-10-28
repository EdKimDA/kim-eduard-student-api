package edu.kim.studentapi;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ControlledFailureTests {
    @Test
    void authorNameIsCorrect() {
        // Correct expectation after diagnosing the controlled CI failure.
        assertThat("Kim Eduard").isEqualTo("Kim Eduard");
    }
}
