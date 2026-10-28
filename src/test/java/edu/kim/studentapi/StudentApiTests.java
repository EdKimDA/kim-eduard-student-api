package edu.kim.studentapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StudentApiTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired StudentRepository repository;

    @BeforeEach
    void clearDatabase() {
        repository.deleteAll();
    }

    private Map<String, String> data(String name, String studentId) {
        return Map.of("name", name, "email", "eduard.kim@example.test",
                "groupName", "IT2-2313", "studentId", studentId);
    }

    private long create(String name, String studentId) throws Exception {
        String body = mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(data(name, studentId))))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return json.readTree(body).get("id").asLong();
    }

    @Test
    void rootDisplaysKimEduardIdentity() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentName").value("Kim Eduard"))
                .andExpect(jsonPath("$.studentId").value("37324"));
    }

    @Test
    void createsStudentAndReturnsLocation() throws Exception {
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(data("Kim Eduard", "37324"))))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name").value("Kim Eduard"));
    }

    @Test
    void listsStudents() throws Exception {
        create("Kim Eduard", "37324");
        mvc.perform(get("/api/students"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getsStudentById() throws Exception {
        long id = create("Kim Eduard", "37324");
        mvc.perform(get("/api/students/{id}", id))
                .andExpect(status().isOk()).andExpect(jsonPath("$.studentId").value("37324"));
    }

    @Test
    void updatesStudent() throws Exception {
        long id = create("Kim Eduard", "37324");
        mvc.perform(put("/api/students/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(data("Kim Eduard Updated", "37324"))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Kim Eduard Updated"));
        mvc.perform(get("/api/students/{id}", id))
                .andExpect(jsonPath("$.name").value("Kim Eduard Updated"));
    }

    @Test
    void deletesStudent() throws Exception {
        long id = create("Kim Eduard", "37324");
        mvc.perform(delete("/api/students/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/students/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void missingStudentReturns404() throws Exception {
        mvc.perform(get("/api/students/999999"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void missingStudentUpdateReturns404() throws Exception {
        mvc.perform(put("/api/students/999999").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(data("Kim Eduard", "37324"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void missingStudentDeleteReturns404() throws Exception {
        mvc.perform(delete("/api/students/999999")).andExpect(status().isNotFound());
    }

    @Test
    void blankNameIsRejected() throws Exception {
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(data(" ", "37324"))))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fields.name").exists());
    }

    @Test
    void invalidEmailIsRejected() throws Exception {
        Map<String, String> invalid = Map.of("name", "Kim Eduard", "email", "invalid",
                "groupName", "IT2-2313", "studentId", "37324");
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(invalid))).andExpect(status().isBadRequest());
    }

    @Test
    void duplicateStudentIdReturns409() throws Exception {
        create("Kim Eduard", "37324");
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(data("Another Kim", "37324"))))
                .andExpect(status().isConflict());
    }

    @Test
    void invalidJsonReturns400() throws Exception {
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
                .content("{invalid")).andExpect(status().isBadRequest());
    }

    @Test
    void filtersNamesWithoutCaseSensitivity() throws Exception {
        create("Kim Eduard", "37324");
        create("Another Student", "99999");
        mvc.perform(get("/api/students").param("name", "eDuArD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Kim Eduard"));
    }
}
