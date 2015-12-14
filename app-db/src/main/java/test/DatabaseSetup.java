package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Stateless;
import javax.sql.DataSource;

@DataSourceDefinition(
    // global to circumvent https://java.net/jira/browse/GLASSFISH-21447
    name = "java:global/MyDS",
    className = "org.h2.jdbcx.JdbcDataSource",
    url="jdbc:h2:mem:test"
)
@Stateless
public class DatabaseSetup {
    
    @Resource(lookup="java:global/MyDS")
    private DataSource dataSource;

    public void init() {
        executeUpdate(dataSource, "DROP TABLE IF EXISTS caller");
        executeUpdate(dataSource, "DROP TABLE IF EXISTS caller_groups");
        
        executeUpdate(dataSource, "CREATE TABLE IF NOT EXISTS caller(name VARCHAR(64) PRIMARY KEY, password VARCHAR(64))");
        executeUpdate(dataSource, "CREATE TABLE IF NOT EXISTS caller_groups(caller_name VARCHAR(64), group_name VARCHAR(64))");
        
        executeUpdate(dataSource, "INSERT INTO caller VALUES('reza', 'secret1')");
        executeUpdate(dataSource, "INSERT INTO caller VALUES('alex', 'secret2')");
        executeUpdate(dataSource, "INSERT INTO caller VALUES('arjan', 'secret2')");
        executeUpdate(dataSource, "INSERT INTO caller VALUES('werner', 'secret2')");
        
        executeUpdate(dataSource, "INSERT INTO caller_groups VALUES('reza', 'foo')");
        executeUpdate(dataSource, "INSERT INTO caller_groups VALUES('reza', 'bar')");
        
        executeUpdate(dataSource, "INSERT INTO caller_groups VALUES('alex', 'foo')");
        executeUpdate(dataSource, "INSERT INTO caller_groups VALUES('alex', 'bar')");
        
        executeUpdate(dataSource, "INSERT INTO caller_groups VALUES('arjan', 'foo')");
        executeUpdate(dataSource, "INSERT INTO caller_groups VALUES('werner', 'foo')");
    }
    
    public void destroy() {
        executeUpdate(dataSource, "DROP TABLE IF EXISTS caller");
        executeUpdate(dataSource, "DROP TABLE IF EXISTS caller_groups");
    }
    
    private void executeUpdate(DataSource dataSource, String query) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
           throw new IllegalStateException(e);
        }
    }
    
}
