package com.ch.managementsystem;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ManagementSystemApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void adminLoginShouldSuccess() throws Exception {
        String content = login("admin", "123456");
        JsonNode node = objectMapper.readTree(content);
        assertThat(node.path("code").asInt()).isEqualTo(200);
        assertThat(node.path("data").path("token").asText()).isNotBlank();
    }

    @Test
    void dashboardApiShouldAccessibleAfterAdminLogin() throws Exception {
        String loginContent = login("admin", "123456");
        String token = objectMapper.readTree(loginContent).path("data").path("token").asText();
        String dashboardContent = mockMvc.perform(get("/api/statistics/dashboard")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode node = objectMapper.readTree(dashboardContent);
        assertThat(node.path("code").asInt()).isEqualTo(200);
        assertThat(node.path("data").path("totalEmployees").asLong()).isGreaterThan(0L);
    }

    @Test
    void employeeProfileShouldContainDashboardFields() throws Exception {
        String loginContent = login("employee", "123456");
        String token = objectMapper.readTree(loginContent).path("data").path("token").asText();

        String profileContent = mockMvc.perform(get("/api/employees/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(profileContent);
        JsonNode data = root.path("data");
        assertThat(root.path("code").asInt()).isEqualTo(200);
        assertThat(data.path("name").asText()).isNotBlank();
        assertThat(data.has("employeeId")).isTrue();
        assertThat(data.has("phone")).isTrue();
        assertThat(data.has("hireDate")).isTrue();
        assertThat(data.has("leaveDate")).isTrue();
        assertThat(data.has("attendanceSummary")).isTrue();
        assertThat(data.has("performanceHistory")).isTrue();
        assertThat(data.has("leaveRecords")).isTrue();
        assertThat(data.has("projectContributionScore")).isTrue();
        assertThat(data.has("projectContributions")).isTrue();
        assertThat(data.has("jobLevelName")).isTrue();
        assertThat(data.path("jobLevelName").asText()).isNotBlank();
        assertThat(data.has("recentChanges")).isTrue();
        assertThat(data.path("recentChanges").isArray()).isTrue();
    }

    private String login(String username, String password) throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("captcha", "1234");
        String loginBody = """
                {
                  "username":"%s",
                  "password":"%s",
                  "captcha":"1234"
                }
                """.formatted(username, password);
        return mockMvc.perform(post("/api/auth/login")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}
