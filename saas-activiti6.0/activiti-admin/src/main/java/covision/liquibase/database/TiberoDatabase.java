package covision.liquibase.database;

import liquibase.CatalogAndSchema;
import liquibase.database.DatabaseConnection;
import liquibase.database.core.OracleDatabase;
import liquibase.exception.DatabaseException;
import liquibase.statement.DatabaseFunction;
import liquibase.structure.DatabaseObject;

/**
 * Covision. Tibero implementation.
 * OracleDatabase 를 상속받을 경우 liquibase 가 Dictionary 조회를 시도하는데, Oracle 구조가 상이하여 내용 복제하여 단독생성함.
 * OracleDatabase 를 상속할 경우 기본적인 JPA 동작에는 문제가 없으나 ChangeLog 관련 Snapshot 기능중 오류발생.  
 * @author hgsong
 */
public class TiberoDatabase extends OracleDatabase {
	public static final String PRODUCT_NAME = "tibero";
	
    public TiberoDatabase() {
        super();
    }

    @Override
    public int getPriority() {
        return PRIORITY_DEFAULT;
    }

    @Override
    public void setConnection(DatabaseConnection conn) {
        super.setConnection(conn);
    }

    @Override
    public String getShortName() {
        return "tibero";
    }

    @Override
    protected String getDefaultDatabaseProductName() {
        return "Oracle";
    }

    @Override
    public Integer getDefaultPort() {
        return 8629;
    }

    @Override
    public String getJdbcCatalogName(CatalogAndSchema schema) {
        return null;
    }

    @Override
    public String getJdbcSchemaName(CatalogAndSchema schema) {
        return super.getJdbcSchemaName(schema);
    }

    @Override
    public String generatePrimaryKeyName(String tableName) {
        return super.generatePrimaryKeyName(tableName);
    }

    @Override
    public boolean supportsInitiallyDeferrableColumns() {
        return true;
    }

    @Override
    public boolean isReservedWord(String objectName) {
        return super.isReservedWord(objectName);
    }

    @Override
    public boolean supportsSequences() {
        return true;
    }

    /**
     * Oracle supports catalogs in liquibase terms
     *
     * @return
     */
    @Override
    public boolean supportsSchemas() {
        return false;
    }

    @Override
    public boolean isCorrectDatabaseImplementation(DatabaseConnection conn) throws DatabaseException {
        return PRODUCT_NAME.equalsIgnoreCase(conn.getDatabaseProductName());
    }

    @Override
    public String getDefaultDriver(String url) {
        if (url.startsWith("jdbc:tibero")) {
            return "com.tmax.tibero.jdbc.TbDriver";
        }
        return null;
    }

    @Override
    public String getDefaultCatalogName() {//NOPMD
        return super.getDefaultCatalogName() == null ? null : super.getDefaultCatalogName().toUpperCase();
    }

    @Override
    public String getDateLiteral(String isoDate) {
        return super.getDateLiteral(isoDate);
    }

    @Override
    public boolean isSystemObject(DatabaseObject example) {
        return super.isSystemObject(example);
    }

    @Override
    public boolean supportsTablespaces() {
        return true;
    }

    @Override
    public boolean supportsAutoIncrement() {
        return false;
    }

    @Override
    public boolean supportsRestrictForeignKeys() {
        return false;
    }

    @Override
    public int getDataTypeMaxParameters(String dataTypeName) {
        return super.getDataTypeMaxParameters(dataTypeName);
    }

    @Override
    public boolean jdbcCallsCatalogsSchemas() {
        return true;
    }

    @Override
    public String generateDatabaseFunctionValue(DatabaseFunction databaseFunction) {
        return super.generateDatabaseFunctionValue(databaseFunction);
    }
}
