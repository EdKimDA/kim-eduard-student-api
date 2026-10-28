package edu.kim.studentapi;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(long id) {
        super("Student " + id + " does not exist");
    }
}
