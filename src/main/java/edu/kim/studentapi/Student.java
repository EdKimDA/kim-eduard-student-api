package edu.kim.studentapi;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 200)
    private String email;

    @Column(nullable = false, length = 80)
    private String groupName;

    @Column(nullable = false, unique = true, length = 80)
    private String studentId;

    protected Student() {
    }

    public Student(StudentRequest request) {
        apply(request);
    }

    public void apply(StudentRequest request) {
        name = request.name().trim();
        email = request.email().trim();
        groupName = request.groupName().trim();
        studentId = request.studentId().trim();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getGroupName() { return groupName; }
    public String getStudentId() { return studentId; }
}
