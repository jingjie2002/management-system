package com.ch.managementsystem;

import org.springframework.core.io.ClassPathResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:transfer_center_flow;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
class TransferCenterFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void jobLevelListShouldReturnSeedData() throws Exception {
        mockMvc.perform(get("/api/job-levels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(greaterThan(0)));
    }

    @Test
    void seededEmployeesShouldIncludeJobLevelLinkage() {
        String levelCode = jdbcTemplate.queryForObject(
                "select jl.level_code from t_employee e join t_job_level jl on e.job_level_id = jl.id where e.job_number = ?",
                String.class,
                "HR001"
        );

        assertThat(levelCode).isEqualTo("P2");
    }

    @Test
    void schemaShouldNotHardcodeAutoIncrementIdsForTransferSeeds() throws Exception {
        String schema = new ClassPathResource("schema.sql")
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(schema).doesNotContain("WHERE employee_id = 5");
        assertThat(schema).doesNotContain("SELECT 5, 'JOB_LEVEL_ADJUST'");
        assertThat(schema).doesNotContain("WHERE c.employee_id = 5");
        assertThat(schema).doesNotContain("UPDATE t_employee SET job_level_id = 2");
        assertThat(schema).doesNotContain("UPDATE t_employee SET job_level_id = 3");
        assertThat(schema).contains("level_code = 'P2'");
        assertThat(schema).contains("level_code = 'P3'");
    }
}
