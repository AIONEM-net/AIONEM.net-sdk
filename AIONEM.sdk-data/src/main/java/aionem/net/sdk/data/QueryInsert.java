package aionem.net.sdk.data;

import aionem.net.sdk.core.utils.UtilsText;
import com.google.gson.JsonObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class QueryInsert extends Query {

    protected boolean only = true;
    protected boolean oElse = true;

    public QueryInsert(final String table) {
        super(table);
    }
    public QueryInsert(final DataAuth auth, final String table) {
        super(auth, table);
    }


    public QueryInsert params(final Data data) {
        super.params(data);
        return this;
    }

    public QueryInsert only(final boolean only) {
        this.only = only;
        this.oElse = oElse && !only;
        return this;
    }
    public QueryInsert onlyElse() {
        this.only = oElse;
        return this;
    }
    public QueryInsert forAll() {
        this.only = true;
        return this;
    }
    public QueryInsert then() {
        return this;
    }

    public QueryInsert put(final String column) {
        put(column, params);
        return this;
    }
    public QueryInsert put(final String column, final Object value) {
        put(column, value, true);
        return this;
    }
    public QueryInsert put(final String column, final Data data) {
        put(column, data, column);
        return this;
    }
    public QueryInsert put(final String column, final Data data, final String key) {
        put(column, data.get(key), data.has(key));
        return this;
    }
    public QueryInsert put(final Data data) {
        for(final String column : data.keySet()) {
            put(column, data.get(column), true);
        }
        return this;
    }
    public QueryInsert put(final JsonObject data) {
        for(final String column : data.keySet()) {
            put(column, data.get(column), true);
        }
        return this;
    }
    public QueryInsert put(final String column, Object value, final boolean condition) {
        value = getNullable(column, value);
        if(column != null && value != null && condition && only) {
            columns1.add(new QueryColumn("`" + column + "`", value));
        }
        return this;
    }

    public String getQuery() {
        String query = " INSERT INTO " + table;
        final StringBuilder columns = new StringBuilder(" (");
        final StringBuilder values = new StringBuilder(" VALUES (");
        for(int i = 0; i < columns1.size(); i++) {
            final QueryColumn columnValue = columns1.get(i);
            final String column = columnValue.getColumn();
            final String value = columnValue.getValue();
            columns.append(i > 0 ? ", " : "").append(column);
            values.append(i > 0 ? ", " : "").append(value);
        }
        columns.append(")");
        values.append(")");

        query += columns + values.toString();
        if(!UtilsText.isEmpty(query)) {
            query += "; ";
        }
        return query;
    }

    public String getErrorQuery() {
        return getError() + " :: " + getQuery();
    }

    @Override
    public String toString() {
        return getQuery();
    }

    public boolean executeInsertSuccess() throws SQLException {
        return executeInsert() > 0;
    }

    public DaoRes executeInsertRes() throws SQLException {
        final DaoRes resInsert = new DaoRes();
        final long dataId = executeInsert();
        if(dataId > 0) {
            resInsert.setSuccess(true);
            resInsert.setId(dataId);
        }else {
            resInsert.setError(getError());
            resInsert.setException(getException());
        }
        return resInsert;
    }

    public long executeInsert() throws SQLException {
        long key = 0;

        try {

            final PreparedStatement prepareStatement = getConnection().prepareStatement(getQuery(), Statement.RETURN_GENERATED_KEYS);
            final int affectedRows = prepareStatement.executeUpdate();

            if (affectedRows > 0) {
                final ResultSet generatedKeys = prepareStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    key = generatedKeys.getInt(1);
                }
                generatedKeys.close();
            }

            prepareStatement.close();

        }catch (final SQLException e) {
            setException(e);
            throw e;
        }

        return key;
    }

    public long executeInsert(final boolean isBackground) {
        final long[] key = {0};

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    key[0] = executeInsert();
                } catch (SQLException e) {
                    setException(e);
                }
            }
        };

        if(isBackground) {
            new Thread(runnable).start();
        }else {
            runnable.run();
        }

        return key[0];
    }

}
