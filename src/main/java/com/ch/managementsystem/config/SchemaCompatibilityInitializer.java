package com.ch.managementsystem.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

@Component
public class SchemaCompatibilityInitializer implements ApplicationRunner {

    private static final String EMPLOYEE_TABLE = "t_employee";
    private static final String JOB_LEVEL_COLUMN = "job_level_id";
    private static final String ADD_JOB_LEVEL_COLUMN_SQL =
            "ALTER TABLE t_employee ADD COLUMN job_level_id BIGINT COMMENT 'Job level ID'";

    private final JdbcTemplate jdbcTemplate;

    public SchemaCompatibilityInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        ensureEmployeeJobLevelColumn();
    }

    public void ensureEmployeeJobLevelColumn() {
        if (!tableExists(EMPLOYEE_TABLE) || columnExists(EMPLOYEE_TABLE, JOB_LEVEL_COLUMN)) {
            return;
        }
        jdbcTemplate.execute(ADD_JOB_LEVEL_COLUMN_SQL);
    }

    private boolean tableExists(String tableName) {
        return jdbcTemplate.execute((ConnectionCallback<Boolean>) connection -> {
            DatabaseMetaData metaData = connection.getMetaData();
            return matchesTable(metaData, tableName)
                    || matchesTable(metaData, tableName.toUpperCase(Locale.ROOT))
                    || matchesTable(metaData, tableName.toLowerCase(Locale.ROOT));
        });
    }

    private boolean columnExists(String tableName, String columnName) {
        return jdbcTemplate.execute((ConnectionCallback<Boolean>) connection -> {
            DatabaseMetaData metaData = connection.getMetaData();
            return matchesColumn(metaData, tableName, columnName)
                    || matchesColumn(metaData, tableName.toUpperCase(Locale.ROOT), columnName.toUpperCase(Locale.ROOT))
                    || matchesColumn(metaData, tableName.toLowerCase(Locale.ROOT), columnName.toLowerCase(Locale.ROOT));
        });
    }

    private boolean matchesTable(DatabaseMetaData metaData, String tableName) throws SQLException {
        try (ResultSet resultSet = metaData.getTables(null, null, tableName, null)) {
            return resultSet.next();
        }
    }

    private boolean matchesColumn(DatabaseMetaData metaData, String tableName, String columnName) throws SQLException {
        try (ResultSet resultSet = metaData.getColumns(null, null, tableName, columnName)) {
            return resultSet.next();
        }
    }
}
