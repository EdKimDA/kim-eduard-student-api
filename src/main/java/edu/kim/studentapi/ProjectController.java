package edu.kim.studentapi;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectController {
    private final ObjectProvider<BuildProperties> buildProperties;

    public ProjectController(ObjectProvider<BuildProperties> buildProperties) {
        this.buildProperties = buildProperties;
    }

    @GetMapping("/")
    public Map<String, String> info() {
        BuildProperties build = buildProperties.getIfAvailable();
        Map<String, String> result = new LinkedHashMap<>();
        result.put("application", "kim-eduard-student-api");
        result.put("studentName", "Kim Eduard");
        result.put("group", "IT2-2313");
        result.put("studentId", "37324");
        result.put("version", build == null ? "development" : build.getVersion());
        result.put("students", "/api/students");
        return result;
    }
}
