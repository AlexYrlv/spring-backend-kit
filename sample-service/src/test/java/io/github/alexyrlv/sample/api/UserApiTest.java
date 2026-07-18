package io.github.alexyrlv.sample.api;

import io.github.alexyrlv.sample.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Behavioural contract of the user API. Mirrors the reference
 * integration suite: list/create/get/update/delete, 404, 409, 405,
 * validation and health.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class UserApiTest {

    @Autowired
    private MockMvc mockMvc;

    private static String userJson(String username, String email) {
        return """
                {"username":"%s","firstName":"John","lastName":"Doe","email":"%s","dateOfBirth":"1990-01-15"}
                """.formatted(username, email);
    }

    @Test
    void shouldListUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateAndGetUser() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson("alice", "alice@example.com")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("alice"));

        mockMvc.perform(get("/users/alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson("bob", "bob@example.com")))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/users/bob")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson("bob", "bob.new@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("bob.new@example.com"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson("carol", "carol@example.com")))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/users/carol"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/users/carol"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404ForUnknownUser() throws Exception {
        mockMvc.perform(get("/users/nobody"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldReturn409OnDuplicateCreate() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson("dave", "dave@example.com")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson("dave", "dave@example.com")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void shouldReturn405ForWrongMethod() throws Exception {
        mockMvc.perform(patch("/users"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void shouldRejectInvalidEmail() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson("eve", "not-an-email")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnHealthyStatus() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
