package aionem.net.sdk.core.data;

import aionem.net.sdk.core.auth.AlnAuthData;
import aionem.net.sdk.core.config.AlnEnv;
import aionem.net.sdk.core.utils.AlnUtilsText;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;


@Log4j2
@Getter
public class AlnQuery {

    protected final AlnAuthData auth;

    protected String table = "``";
    protected String query = "";
    protected final ArrayList<String> tables = new ArrayList<>();
    protected final ArrayList<AlnQueryColumn> columns1 = new ArrayList<>();
    protected final ArrayList<AlnQueryColumn> columns2 = new ArrayList<>();
    protected AlnData data = new AlnData();
    private String error = "";
    private Exception exception;

    protected AlnQuery(final AlnAuthData auth, final String table) {
        this.auth = auth;
        if(table != null) {
            this.table = "`"+table+"`";
            this.tables.add(this.table);
        }
    }

    public static AlnQueryInsert insert(final AlnAuthData auth, final String table) {
        return new AlnQueryInsert(auth, table);
    }

    public static AlnQueryUpdate update(final AlnAuthData auth, final String table) {
        return new AlnQueryUpdate(auth, table);
    }

    public static AlnQuerySelect select(final AlnAuthData auth, final String table) {
        return new AlnQuerySelect(auth, table);
    }

    public static AlnQueryDelete delete(final AlnAuthData auth, final String table) {
        return new AlnQueryDelete(auth, table);
    }

    public Connection getConnection() {
        return getConnection(auth);
    }

    private static Connection connection = null;
    private static PoolDataSource poolDataSource = null;
    public static Connection getConnection(final AlnAuthData auth) {
        if(auth == null) {
            throw new NullPointerException("Auth is null");
        }
        if(auth.getRequest() == null) {
            throw new NullPointerException("Auth request is null");
        }
        try {

            if(false && poolDataSource == null) {
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
            if(AlnUtilsText.isEmpty(error)) {
                this.error = e.getMessage();
            }
            if(AlnEnv.IS_DEBUG) log.info("\nERROR: " + e +"\n");
        }
    }

    public static int getOffset(final int page, final int max) {
        return page <= 0 ? 0 : (page * max) - max;
    }

    public AlnQuery data(final AlnData data) {
        this.data = data != null ? data : new AlnData();
        return this;
    }

    protected Object getNullable(final String column, Object value) {
        if(value instanceof AlnData) {
            value = ((AlnData) value).getNullable(column);
        }
        return value;
    }

}
