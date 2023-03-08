package covision.liquibase.database;

import liquibase.database.DatabaseConnection;
import liquibase.database.core.MySQLDatabase;
import liquibase.exception.DatabaseException;

/**
 * Covision. Tibero implementation. not supported mariadb jdbc driver 2.x on liquibase 3.1.0
 * @author hgsong
 */
public class MariaDBDatabase extends MySQLDatabase {
    public static final String PRODUCT_NAME = "MariaDB";

    @Override
    public String getShortName() {
        return "mariadb";
    }

    @Override
    protected String getDefaultDatabaseProductName() {
        return MariaDBDatabase.PRODUCT_NAME;
    }

    @Override
    public String getDefaultDriver(String url) {
        if (url.startsWith("jdbc:mariadb")) {
            return "org.mariadb.jdbc.Driver";
        }
        return null;
    }

    @Override
    public boolean isCorrectDatabaseImplementation(DatabaseConnection conn) throws DatabaseException {
        return PRODUCT_NAME.equalsIgnoreCase(conn.getDatabaseProductName());
    }
}
