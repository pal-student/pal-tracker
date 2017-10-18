package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository{

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private String sql_select = "SELECT `id`, `project_id`, `user_id`, `date`, `hours` FROM `time_entries`";
    private String sql_insert = "INSERT INTO `time_entries` (`project_id`, `user_id`, `date`, `hours`) VALUES (?, ?, ?, ?)";
    private String sql_update = "UPDATE `time_entries` SET `project_id` = ?, `user_id` = ?, `date` = ?, `hours` = ? WHERE `id` = ?";
    private String sql_delete = "DELETE FROM `time_entries` WHERE `id` = ?";

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private RowMapper<TimeEntry> rowMapper = new RowMapper<TimeEntry>() {
        @Override
        public TimeEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
            TimeEntry entry = new TimeEntry();
            entry.setId(rs.getLong("id"));
            entry.setDate(rs.getDate("date").toLocalDate());
            entry.setHours(rs.getInt("hours"));
            entry.setProjectId(rs.getLong("project_id"));
            entry.setUserId(rs.getLong("user_id"));
            return entry;
        }
    };


    @Override
    public TimeEntry create(TimeEntry timeEntry) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    sql_insert,
                    RETURN_GENERATED_KEYS
            );

            statement.setLong(1, timeEntry.getProjectId());
            statement.setLong(2, timeEntry.getUserId());
            statement.setDate(3, Date.valueOf(timeEntry.getDate()));
            statement.setInt(4, timeEntry.getHours());

            return statement;
        }, keyHolder);

        return find(keyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(Long id) {
        List<TimeEntry> entries = jdbcTemplate.query(sql_select + " where `id`= ?", new Object[]{id}, rowMapper);
        if (entries != null && entries.size() == 1) {
            return entries.get(0);
        }
        return null;
    }

    @Override
    public List<TimeEntry> list() {
        List<TimeEntry> entries = jdbcTemplate.query(sql_select, rowMapper, new Object[]{});
        return entries;
    }

    @Override
    public TimeEntry update(Long id, TimeEntry timeEntry) {

        jdbcTemplate.update(sql_update, new Object[]{timeEntry.getProjectId(), timeEntry.getUserId(), Date.valueOf(timeEntry.getDate()), timeEntry.getHours(), id});

        return find(id);
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update(sql_delete, new Object[]{id});
    }
}