package com.ch.managementsystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:job_level_controller;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
class JobLevelControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void adminShouldListJobLevelsOrderedByRank() throws Exception {
        mockMvc.perform(get("/api/job-levels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].levelCode").value("P1"))
                .andExpect(jsonPath("$.data[1].levelCode").value("P2"));
    }

    @Test
    @WithMockUser(username = "employee", roles = {"employee"})
    void employeeShouldAccessJobLevelListForChangeApplication() throws Exception {
        mockMvc.perform(get("/api/job-levels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    @Test
    @WithMockUser(username = "hr", roles = {"hr"})
    void hrShouldCreateJobLevel() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "levelCode", "P4",
                "levelName", "Lead Specialist",
                "levelRank", 4,
                "salaryMin", new BigDecimal("18000.00"),
                "salaryMax", new BigDecimal("25000.00"),
                "status", 1,
                "remark", "Created from controller test"
        ));

        mockMvc.perform(post("/api/job-levels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));

        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from t_job_level where level_code = 'P4' and deleted = 0",
                Integer.class
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    @WithMockUser(username = "hr", roles = {"hr"})
    void hrShouldUpdateJobLevel() throws Exception {
        Long id = jdbcTemplate.queryForObject(
                "select id from t_job_level where level_code = 'P2' and deleted = 0",
                Long.class
        );

        String body = objectMapper.writeValueAsString(Map.of(
                "id", id,
                "levelCode", "P2",
                "levelName", "Specialist Plus",
                "levelRank", 2,
                "salaryMin", new BigDecimal("9000.00"),
                "salaryMax", new BigDecimal("13000.00"),
                "status", 1,
                "remark", "Updated from controller test"
        ));

        mockMvc.perform(put("/api/job-levels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));

        String levelName = jdbcTemplate.queryForObject(
                "select level_name from t_job_level where id = ?",
                String.class,
                id
        );
        assertThat(levelName).isEqualTo("Specialist Plus");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void adminShouldDeleteJobLevel() throws Exception {
        jdbcTemplate.update(
                "insert into t_job_level(level_code, level_name, level_rank, salary_min, salary_max, status, deleted) values(?,?,?,?,?,?,0)",
                "P9", "Delete Candidate", 9, new BigDecimal("30000.00"), new BigDecimal("35000.00"), 1
        );
        Long id = jdbcTemplate.queryForObject(
                "select id from t_job_level where level_code = 'P9' and deleted = 0",
                Long.class
        );

        mockMvc.perform(delete("/api/job-levels/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));

        Integer deleted = jdbcTemplate.queryForObject(
                "select deleted from t_job_level where id = ?",
                Integer.class,
                id
        );
        assertThat(deleted).isEqualTo(1);
    }
}
