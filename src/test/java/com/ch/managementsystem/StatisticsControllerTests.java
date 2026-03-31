package com.ch.managementsystem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:statistics_controller_tests;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
class StatisticsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void dashboardShouldReturnEmptyTrendWhenPerformanceDataIsMissing() throws Exception {
        jdbcTemplate.update("delete from t_performance");

        mockMvc.perform(get("/api/statistics/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.monthlyPerformanceTrend.length()").value(0));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void dashboardShouldBuildTrendFromPerformanceRecordsInsteadOfSalaryAverage() throws Exception {
        jdbcTemplate.update("delete from t_performance");
        jdbcTemplate.update(
                """
                insert into t_performance(employee_id, perf_month, base_salary, attendance_days, project_bonus, comments, deleted)
                values (?, ?, ?, ?, ?, ?, 0)
                """,
                1L, "2026-01", new BigDecimal("8000.00"), 20, new BigDecimal("100.00"), "A"
        );
        jdbcTemplate.update(
                """
                insert into t_performance(employee_id, perf_month, base_salary, attendance_days, project_bonus, comments, deleted)
                values (?, ?, ?, ?, ?, ?, 0)
                """,
                1L, "2026-02", new BigDecimal("5000.00"), 10, new BigDecimal("0.00"), "B"
        );

        mockMvc.perform(get("/api/statistics/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.monthlyPerformanceTrend.length()").value(2))
                .andExpect(jsonPath("$.data.monthlyPerformanceTrend[0].name").value("2026-01"))
                .andExpect(jsonPath("$.data.monthlyPerformanceTrend[0].value").value(70.0))
                .andExpect(jsonPath("$.data.monthlyPerformanceTrend[1].name").value("2026-02"))
                .andExpect(jsonPath("$.data.monthlyPerformanceTrend[1].value").value(30.0));
    }
}
