package com.ch.managementsystem;

import com.ch.managementsystem.config.SchemaCompatibilityInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:schema_compatibility;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
class SchemaCompatibilityInitializerTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SchemaCompatibilityInitializer schemaCompatibilityInitializer;

    @Test
    void initializerShouldRestoreMissingJobLevelColumn() {
        jdbcTemplate.execute("ALTER TABLE t_employee DROP COLUMN job_level_id");

        Integer missingCount = jdbcTemplate.queryForObject(
                "select count(1) from information_schema.columns where lower(table_name) = 't_employee' and lower(column_name) = 'job_level_id'",
                Integer.class
        );
        assertThat(missingCount).isEqualTo(0);

        schemaCompatibilityInitializer.ensureEmployeeJobLevelColumn();

        Integer existingCount = jdbcTemplate.queryForObject(
                "select count(1) from information_schema.columns where lower(table_name) = 't_employee' and lower(column_name) = 'job_level_id'",
                Integer.class
        );
        assertThat(existingCount).isEqualTo(1);
    }
}
