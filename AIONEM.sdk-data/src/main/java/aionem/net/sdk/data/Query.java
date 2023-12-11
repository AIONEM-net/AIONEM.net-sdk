package aionem.net.sdk.data;

import aionem.net.sdk.core.config.Env;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.api.AuthData;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;


@Log4j2
@Getter
public class Query {

    protected final AuthData auth;

    protected String table = "``";
    protected String query = "";
    protected final ArrayList<String> tables = new ArrayList<>();
    protected final ArrayList<QueryColumn> columns1 = new ArrayList<>();
    protected final ArrayList<QueryColumn> columns2 = new ArrayList<>();
    protected Data params = new Data();
    private String error = "";
    private Exception exception;

    public enum QueryDate { NOW("NOW()"), CURDATE("CURDATE()"), ;
        QueryDate(String date) {
        }
    }

    protected Query(final String table) {
        this(new AuthData(), table);
    }
    protected Query(final AuthData auth, final String table) {
        this.auth = auth;
        if(table != null) {
            this.table = "`"+table+"`";
            this.tables.add(this.table);
        }
    }

    public static QueryInsert insert(final String table) {
        return new QueryInsert(table);
    }
    public static QueryInsert insert(final AuthData auth, final String table) {
        return new QueryInsert(auth, table);
    }

    public static QueryUpdate update(final String table) {
        return new QueryUpdate(table);
    }
    public static QueryUpdate update(final AuthData auth, final String table) {
        return new QueryUpdate(auth, table);
    }

    public static QuerySelect select(final String table) {
        return new QuerySelect(table);
    }
    public static QuerySelect select(final AuthData auth, final String table) {
        return new QuerySelect(auth, table);
    }

    public static QueryDelete delete(final String table) {
        return new QueryDelete(table);
    }
    public static QueryDelete delete(final AuthData auth, final String table) {
        return new QueryDelete(auth, table);
    }

    public Connection getConnection() {
        return getConnection(auth);
    }

    private static Connection connection = null;
    private static PoolDataSource poolDataSource = null;
    public static Connection getConnection(final AuthData auth) {
        if(auth == null) {
            throw new NullPointerException("Auth is null");
        }
        try {

            if(auth.isUsePoolDataSource() && poolDataSource == null) {
                poolDataSource = PoolDataSourceFactory.getPoolDataSource();
                poolDataSource.setConnectionFactoryClassName("com.mysql.cj.jdbc.MysqlDataSource");
                poolDataSource.setURL(auth.getDBConnection() + "://"+ auth.getDBHost() +":"+ auth.getDBPort() +"/"+ auth.getDBName());
                poolDataSource.setUser(auth.getDBUser());
                poolDataSource.setPassword(auth.getPassword());
                poolDataSource.setInitialPoolSize(1);
                poolDataSource.setMinPoolSize(1);
                poolDataSource.setMaxPoolSize(1000);
                connection = poolDataSource.getConnection();
            }

            if(connection == null || connection.isClosed()) {
                Class.forName(auth.getDBDriver());
                connection = DriverManager.getConnection(auth.getDBConnection() + "://"+ auth.getDBHost() +":"+ auth.getDBPort() +"/"+ auth.getDBName(), auth.getDBUser(), auth.getDBPassword());
            }

        }catch(Exception e) {
            log.error("\nERROR: DB CONNECTION - Open ::" + e +"\n");
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
            }catch(Exception e) {
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
