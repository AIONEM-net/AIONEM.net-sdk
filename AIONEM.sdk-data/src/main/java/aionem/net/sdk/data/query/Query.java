package aionem.net.sdk.data.query;

import aionem.net.sdk.core.Env;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.beans.Data;
import aionem.net.sdk.data.config.ConfApp;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;


@Log4j2
@Getter
public class Query {

    protected String table = "``";
    protected final ArrayList<String> tables = new ArrayList<>();
    protected final ArrayList<QueryColumn> columns1 = new ArrayList<>();
    protected final ArrayList<QueryColumn> columns2 = new ArrayList<>();
    protected Data params = new Data();
    private String error = "";
    private Exception exception;
    private boolean isConnected = false;

    protected Query(final String table) {
        if(table != null) {
            this.table = "`"+table+"`";
            this.tables.add(this.table);
        }
    }

    public static QueryInsert insert(final String table) {
        return new QueryInsert(table);
    }

    public static QueryUpdate update(final String table) {
        return new QueryUpdate(table);
    }

    public static QuerySelect select(final String table) {
        return new QuerySelect(table);
    }

    public static QueryDelete delete(final String table) {
        return new QueryDelete(table);
    }

    public Connection getConnection() throws SQLException {
        final Connection connection = getDbConnection();
        try {
            isConnected = connection != null && !connection.isClosed();
            if(!isConnected) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            isConnected = false;
            throw e;
        }
        return connection;
    }

    private static Connection connection = null;
    private static PoolDataSource poolDataSource = null;
    public static Connection getDbConnection() {
        try {

            if(ConfApp.isUsePoolDataSource() && poolDataSource == null) {
                poolDataSource = PoolDataSourceFactory.getPoolDataSource();
                poolDataSource.setConnectionFactoryClassName("com.mysql.cj.jdbc.MysqlDataSource");
                poolDataSource.setURL(ConfApp.getDBConnectionUrl());
                poolDataSource.setUser(ConfApp.getDBUser());
                poolDataSource.setPassword(ConfApp.getDBPassword());
                poolDataSource.setInitialPoolSize(1);
                poolDataSource.setMinPoolSize(1);
                poolDataSource.setMaxPoolSize(1000);
                connection = poolDataSource.getConnection();
            }

            if(connection == null || connection.isClosed()) {
                Class.forName(ConfApp.getDBDriver());
                connection = DriverManager.getConnection(ConfApp.getDBConnectionUrl(), ConfApp.getDBUser(), ConfApp.getDBPassword());
            }

        }catch(final Exception e) {
            log.error("\n"+ e +" :: " + e.getStackTrace()[0] +"\n");
        }
        return connection;
    }

    public static boolean closeConnection() {
        boolean isClosed = true;
        if(connection != null) {
            try {
                if(!connection.isClosed()) {
                    connection.close();
                }
            }catch(final Exception e) {
                log.error("\nERROR: DB CONNECTION - Close ::" + e +"\n");
                isClosed = false;
            }
        }
        return isClosed;
    }

    public void setError(final String error) {
        this.error = error;
    }
    public void setException(final Exception e) {
        if(e != null) {
            this.exception = e;
            if(UtilsText.isEmpty(error)) {
                this.error = e.getMessage();
            }
            if(Env.IS_DEBUG) log.info("\nERROR: " + e +"\n");
        }
    }

    public String getTable(final int tableNo) {
        if(tableNo < 0) return "";
        return tables.get(tableNo);
    }
    public String getTableColumn(final int tableNo, final String column) {
        if(tableNo < 0) return column;
        return tables.get(tableNo) + "." + "`" + column + "`";
    }

    public Query params(final Data data) {
        this.params = data != null ? data : new Data();
        return this;
    }

    protected Object getNullable(final String column, Object value) {
        if(value instanceof Data) {
            value = ((Data) value).getNullable(column);
        }
        return value;
    }

}
