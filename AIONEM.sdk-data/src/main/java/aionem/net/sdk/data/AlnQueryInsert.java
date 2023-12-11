package aionem.net.sdk.data;

import aionem.net.sdk.core.utils.AlnUtilsText;
import aionem.net.sdk.data.api.AlnAuthData;
import aionem.net.sdk.data.api.AlnDaoRes;
import com.google.gson.JsonObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


public class AlnQueryInsert extends AlnQuery {

    protected boolean only = true;
    protected boolean oElse = true;

    public AlnQueryInsert(final String table) {
        super(table);
    }
    public AlnQueryInsert(final AlnAuthData auth, final String table) {
        super(auth, table);
    }


    public AlnQueryInsert params(final AlnData data) {
        super.params(data);
        return this;
    }

    public AlnQueryInsert only(final boolean only) {
        this.only = only;
        this.oElse = oElse && !only;
        return this;
    }
    public AlnQueryInsert onlyElse() {
        this.only = oElse;
        return this;
    }
    public AlnQueryInsert forAll() {
        this.only = true;
        return this;
    }
    public AlnQueryInsert then() {
        return this;
    }

    public AlnQueryInsert put(final String column) {
        put(column, params);
        return this;
    }
    public AlnQueryInsert put(final String column, final Object value) {
        put(column, value, true);
        return this;
    }
    public AlnQueryInsert put(final String column, final AlnData data) {
        put(column, data, column);
        return this;
    }
    public AlnQueryInsert put(final String column, final AlnData data, final String key) {
        put(column, data.get(key), data.has(key));
        return this;
    }
    public AlnQueryInsert put(final AlnData data) {
        for(final String column : data.keySet()) {
            put(column, data.get(column), true);
        }
        return this;
    }
    public AlnQueryInsert put(final JsonObject data) {
        for(final String column : data.keySet()) {
            put(column, data.get(column), true);
        }
        return this;
    }
    public AlnQueryInsert put(final String column, Object value, final boolean condition) {
        value = getNullable(column, value);
        if(column != null && value != null && condition && only) {
            columns1.add(new AlnQueryColumn("`" + column + "`", "'" + AlnUtilsText.toString(value) + "'"));
        }
        return this;
    }

    public String getQuery() {
        query = " INSERT INTO " + table;
        final StringBuilder columns = new StringBuilder(" (");
        final StringBuilder values = new StringBuilder(" VALUES (");
        for(int i = 0; i < columns1.size(); i++) {
            final AlnQueryColumn columnValue = columns1.get(i);
            final String column = columnValue.getColumn();
            final String value = columnValue.getValue();
            columns.append(i > 0 ? ", " : "").append(column);
            values.append(i > 0 ? ", " : "").append(value);
        }
        columns.append(")");
        values.append(")");

        query += columns + values.toString();
        if(!AlnUtilsText.isEmpty(query)) {
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

    public boolean executeInsertSuccess() {
        return executeInsert() > 0;
    }

    public AlnDaoRes executeInsertRes() {
        final AlnDaoRes resInsert = new AlnDaoRes();
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

    public long executeInsert() {
        long key = 0;
        try {

            final PreparedStatement prepareStatement = getConnection(auth).prepareStatement(getQuery(), Statement.RETURN_GENERATED_KEYS);
            final int affectedRows = prepareStatement.executeUpdate();

            if(affectedRows > 0) {
                final ResultSet generatedKeys = prepareStatement.getGeneratedKeys();
                if(generatedKeys.next()) {
                    key = generatedKeys.getInt(1);
                }
                generatedKeys.close();
            }

            prepareStatement.close();

        }catch(Exception e) {
            setException(e);
        }
        return key;
    }

    public long executeInsert(final boolean isBackground) {
        final long[] key = {0};

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                key[0] = executeInsert();
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
