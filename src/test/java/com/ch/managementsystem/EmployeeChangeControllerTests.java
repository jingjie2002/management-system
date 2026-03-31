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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:employee_change_controller;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
class EmployeeChangeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "employee", roles = {"employee"})
    void employeeShouldCreatePendingChangeRequest() throws Exception {
        Long targetJobLevelId = getJobLevelId("P3");

        String body = objectMapper.writeValueAsString(Map.of(
                "changeType", "TRANSFER",
                "reason", "Apply for transfer to sales enablement",
                "effectiveDate", LocalDate.of(2026, 4, 15).toString(),
                "afterDeptId", 4L,
                "afterPosition", "Sales Enablement Specialist",
                "afterJobLevelId", targetJobLevelId,
                "afterSalary", new BigDecimal("8200.00")
        ));

        mockMvc.perform(post("/api/employee-changes/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.changeType").value("TRANSFER"))
                .andExpect(jsonPath("$.data.applyMode").value("EMPLOYEE_APPLY"))
                .andExpect(jsonPath("$.data.status").value("PENDING_APPROVAL"))
                .andExpect(jsonPath("$.data.employeeId").value(getEmployeeIdByJobNumber("IT002")))
                .andExpect(jsonPath("$.data.beforeDeptId").value(3))
                .andExpect(jsonPath("$.data.afterDeptId").value(4))
                .andExpect(jsonPath("$.data.logs.length()").value(1));

        Integer changeCount = jdbcTemplate.queryForObject(
                "select count(1) from t_employee_change where employee_id = ? and reason = ? and status = ? and deleted = 0",
                Integer.class,
                getEmployeeIdByJobNumber("IT002"),
                "Apply for transfer to sales enablement",
                "PENDING_APPROVAL"
        );
        assertThat(changeCount).isEqualTo(1);
    }

    @Test
    @WithMockUser(username = "employee", roles = {"employee"})
    void employeeShouldNotApproveChange() throws Exception {
        Long changeId = insertChange("PENDING_APPROVAL", "Employee cannot approve this");

        String body = objectMapper.writeValueAsString(Map.of(
                "approved", true,
                "remark", "Trying to approve without permission"
        ));

        mockMvc.perform(post("/api/employee-changes/{id}/approve", changeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "hr", roles = {"hr"})
    void hrShouldApproveEmployeeChangeRequest() throws Exception {
        Long changeId = insertChange("PENDING_APPROVAL", "Approve by hr");

        String body = objectMapper.writeValueAsString(Map.of(
                "approved", true,
                "remark", "Transfer approved by HR"
        ));

        mockMvc.perform(post("/api/employee-changes/{id}/approve", changeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(changeId))
                .andExpect(jsonPath("$.data.status").value("APPROVED_PENDING_EFFECTIVE"))
                .andExpect(jsonPath("$.data.logs.length()").value(2));

        Map<String, Object> row = jdbcTemplate.queryForMap(
                "select status, approver_user_id, approve_remark from t_employee_change where id = ?",
                changeId
        );
        assertThat(row.get("status")).isEqualTo("APPROVED_PENDING_EFFECTIVE");
        assertThat(((Number) row.get("approver_user_id")).longValue()).isEqualTo(getUserId("hr"));
        assertThat(row.get("approve_remark")).isEqualTo("Transfer approved by HR");
    }

    @Test
    @WithMockUser(username = "hr", roles = {"hr"})
    void hrShouldRejectEmployeeChangeRequest() throws Exception {
        Long changeId = insertChange("PENDING_APPROVAL", "Reject by hr");

        String body = objectMapper.writeValueAsString(Map.of(
                "approved", false,
                "remark", "Requested target department is frozen"
        ));

        mockMvc.perform(post("/api/employee-changes/{id}/approve", changeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("REJECTED"))
                .andExpect(jsonPath("$.data.logs.length()").value(2));

        String statusValue = jdbcTemplate.queryForObject(
                "select status from t_employee_change where id = ?",
                String.class,
                changeId
        );
        assertThat(statusValue).isEqualTo("REJECTED");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void adminShouldApproveEmployeeChangeRequest() throws Exception {
        Long changeId = insertChange("PENDING_APPROVAL", "Approve by admin");

        String body = objectMapper.writeValueAsString(Map.of(
                "approved", true,
                "remark", "Transfer approved by admin"
        ));

        mockMvc.perform(post("/api/employee-changes/{id}/approve", changeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(changeId))
                .andExpect(jsonPath("$.data.status").value("APPROVED_PENDING_EFFECTIVE"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void adminShouldExecuteApprovedChangeAndUpdateEmployeeMaster() throws Exception {
        Long employeeId = getEmployeeIdByJobNumber("IT002");
        Long changeId = insertApprovedChange("Execute by admin");

        String body = objectMapper.writeValueAsString(Map.of(
                "remark", "Executed after handover completed"
        ));

        mockMvc.perform(post("/api/employee-changes/{id}/execute", changeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("EXECUTED"))
                .andExpect(jsonPath("$.data.logs.length()").value(3));

        Map<String, Object> employeeRow = jdbcTemplate.queryForMap(
                "select dept_id, position, job_level_id, base_salary from t_employee where id = ?",
                employeeId
        );
        assertThat(((Number) employeeRow.get("dept_id")).longValue()).isEqualTo(4L);
        assertThat(employeeRow.get("position")).isEqualTo("Sales Enablement Specialist");
        assertThat(((Number) employeeRow.get("job_level_id")).longValue()).isEqualTo(getJobLevelId("P3"));
        assertThat(employeeRow.get("base_salary")).isEqualTo(new BigDecimal("8200.00"));
    }

    @Test
    @WithMockUser(username = "hr", roles = {"hr"})
    void hrShouldCreateDirectChangePendingEffective() throws Exception {
        Long employeeId = getEmployeeIdByJobNumber("IT002");

        String body = objectMapper.writeValueAsString(Map.of(
                "employeeId", employeeId,
                "changeType", "TRANSFER",
                "reason", "Directly handle transfer from HR",
                "effectiveDate", LocalDate.of(2026, 4, 20).toString(),
                "afterDeptId", 4L,
                "afterPosition", "Sales Enablement Specialist",
                "afterJobLevelId", getJobLevelId("P3"),
                "afterSalary", new BigDecimal("8200.00")
        ));

        mockMvc.perform(post("/api/employee-changes/direct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.applyMode").value("HR_DIRECT"))
                .andExpect(jsonPath("$.data.status").value("APPROVED_PENDING_EFFECTIVE"))
                .andExpect(jsonPath("$.data.employeeId").value(employeeId))
                .andExpect(jsonPath("$.data.logs.length()").value(2));

        Map<String, Object> employeeRow = jdbcTemplate.queryForMap(
                "select dept_id, position from t_employee where id = ?",
                employeeId
        );
        assertThat(((Number) employeeRow.get("dept_id")).longValue()).isEqualTo(3L);
        assertThat(employeeRow.get("position")).isEqualTo("Tester");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void adminShouldCreateDirectChangePendingEffective() throws Exception {
        Long employeeId = getEmployeeIdByJobNumber("IT002");

        String body = objectMapper.writeValueAsString(Map.of(
                "employeeId", employeeId,
                "changeType", "TRANSFER",
                "reason", "Directly handle transfer from admin",
                "effectiveDate", LocalDate.of(2026, 4, 22).toString(),
                "afterDeptId", 4L,
                "afterPosition", "Sales Enablement Specialist",
                "afterJobLevelId", getJobLevelId("P3"),
                "afterSalary", new BigDecimal("8200.00")
        ));

        mockMvc.perform(post("/api/employee-changes/direct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.applyMode").value("HR_DIRECT"))
                .andExpect(jsonPath("$.data.status").value("APPROVED_PENDING_EFFECTIVE"))
                .andExpect(jsonPath("$.data.employeeId").value(employeeId));
    }

    @Test
    @WithMockUser(username = "employee", roles = {"employee"})
    void employeeDemoAccountShouldMapToActiveEmployeeRecord() throws Exception {
        Integer activeLinkCount = jdbcTemplate.queryForObject(
                """
                select count(1)
                from t_employee e
                join sys_user u on e.user_id = u.id
                where u.username = ?
                  and e.status = 1
                  and e.leave_date is null
                  and e.deleted = 0
                """,
                Integer.class,
                "employee"
        );

        assertThat(activeLinkCount).isEqualTo(1);

        mockMvc.perform(get("/api/employees/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.jobNumber").value("IT002"))
                .andExpect(jsonPath("$.data.status").value(1));
    }

    @Test
    @WithMockUser(username = "employee", roles = {"employee"})
    void employeeShouldListOwnChangeRecords() throws Exception {
        Long employeeId = getEmployeeIdByJobNumber("IT002");
        insertChange("PENDING_APPROVAL", "Visible in employee list");

        mockMvc.perform(get("/api/employee-changes/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(greaterThan(0)))
                .andExpect(jsonPath("$.data[0].employeeId").value(employeeId));
    }

    @Test
    @WithMockUser(username = "hr", roles = {"hr"})
    void hrShouldListAllChangeRecords() throws Exception {
        insertChange("PENDING_APPROVAL", "Visible in hr list");

        mockMvc.perform(get("/api/employee-changes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(greaterThan(0)));
    }

    private Long insertChange(String status, String reason) {
        Long employeeId = getEmployeeIdByJobNumber("IT002");
        jdbcTemplate.update(
                """
                insert into t_employee_change (
                    employee_id, change_type, apply_mode, status, reason, effective_date,
                    before_dept_id, after_dept_id, before_position, after_position,
                    before_job_level_id, after_job_level_id, before_salary, after_salary,
                    applicant_user_id, create_time, update_time, deleted
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
                """,
                employeeId,
                "TRANSFER",
                "EMPLOYEE_APPLY",
                status,
                reason,
                LocalDate.of(2026, 4, 18),
                3L,
                4L,
                "Tester",
                "Sales Enablement Specialist",
                getJobLevelId("P2"),
                getJobLevelId("P3"),
                new BigDecimal("7000.00"),
                new BigDecimal("8200.00"),
                getUserId("employee")
        );

        Long changeId = jdbcTemplate.queryForObject(
                "select id from t_employee_change where reason = ? order by id desc limit 1",
                Long.class,
                reason
        );

        jdbcTemplate.update(
                "insert into t_employee_change_log(change_id, action_type, operator_user_id, operator_name, content, create_time) values(?, ?, ?, ?, ?, CURRENT_TIMESTAMP)",
                changeId,
                "CREATED",
                getUserId("employee"),
                "Jane Smith",
                "Employee submitted transfer request"
        );
        return changeId;
    }

    private Long insertApprovedChange(String reason) {
        Long changeId = insertChange("APPROVED_PENDING_EFFECTIVE", reason);
        jdbcTemplate.update(
                "update t_employee_change set approver_user_id = ?, approve_remark = ?, approve_time = CURRENT_TIMESTAMP where id = ?",
                getUserId("hr"),
                "Approved before execute",
                changeId
        );
        jdbcTemplate.update(
                "insert into t_employee_change_log(change_id, action_type, operator_user_id, operator_name, content, create_time) values(?, ?, ?, ?, ?, CURRENT_TIMESTAMP)",
                changeId,
                "APPROVED",
                getUserId("hr"),
                "HR Manager",
                "HR approved before execute"
        );
        return changeId;
    }

    private Long getEmployeeIdByJobNumber(String jobNumber) {
        return jdbcTemplate.queryForObject(
                "select id from t_employee where job_number = ? and deleted = 0",
                Long.class,
                jobNumber
        );
    }

    private Long getJobLevelId(String levelCode) {
        return jdbcTemplate.queryForObject(
                "select id from t_job_level where level_code = ? and deleted = 0",
                Long.class,
                levelCode
        );
    }

    private Long getUserId(String username) {
        return jdbcTemplate.queryForObject(
                "select id from sys_user where username = ? and deleted = 0",
                Long.class,
                username
        );
    }
}
