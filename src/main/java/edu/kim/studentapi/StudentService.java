package edu.kim.studentapi;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StudentService {
    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public List<Student> list() {
        return repository.findAll(Sort.by("id"));
    }

    public Student get(long id) {
        return repository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    @Transactional
    public Student create(StudentRequest request) {
        return repository.saveAndFlush(new Student(request));
    }

    @Transactional
    public Student update(long id, StudentRequest request) {
        Student student = get(id);
        student.apply(request);
        return repository.saveAndFlush(student);
    }

    @Transactional
    public void delete(long id) {
        repository.delete(get(id));
        repository.flush();
    }
}
