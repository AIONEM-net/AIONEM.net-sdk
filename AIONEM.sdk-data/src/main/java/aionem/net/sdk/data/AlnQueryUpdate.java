package aionem.net.sdk.data;

import aionem.net.sdk.core.utils.AlnUtilsText;
import aionem.net.sdk.data.api.AlnAuthData;
import aionem.net.sdk.data.api.AlnDaoRes;
import com.google.gson.JsonObject;

import java.sql.Statement;


public class AlnQueryUpdate extends AlnQueryCondition {

    public AlnQueryUpdate(final String table) {
        super(table);
    }
    public AlnQueryUpdate(final AlnAuthData auth, final String table) {
        super(auth, table);
    }


    public AlnQueryUpdate data(final AlnData data) {
        super.data(data);
        return this;
    }

    public AlnQueryUpdate set(final String column) {
        set(0, column);
        return this;
    }

    public AlnQueryUpdate set(final int tableNo, final String column) {
        set(tableNo, column, data, true);
        return this;
    }

    public AlnQueryUpdate set(final String column, final Object value) {
        set(0, column, value);
        return this;
    }

    public AlnQueryUpdate set(final int tableNo, final String column, final Object value) {
        set(tableNo, column, value, true);
        return this;
    }

    public AlnQueryUpdate set(final JsonObject data) {
        set(0, data);
        return this;
    }

    public AlnQueryUpdate set(final int tableNo, final JsonObject data) {
        for(String column : data.keySet()) {
            set(tableNo, column, data.get(column), true);
        }
        return this;
    }

    public AlnQueryUpdate set(final String column, final AlnData data) {
        set(0, column, data);
        return this;
    }

    public AlnQueryUpdate set(final int tableNo, final String column, final AlnData data) {
        set(tableNo, column, data, column);
        return this;
    }

    public AlnQueryUpdate set(final String column, final AlnData data, final String key) {
        set(0, column, data.get(key), data.has(key));
        return this;
    }

    public AlnQueryUpdate set(final int tableNo, final String column, final AlnData data, final String key) {
        set(tableNo, column, data.get(key), data.has(key));
        return this;
    }

    public AlnQueryUpdate set(final String column, final Object value, final boolean condition) {
        set(0, column, value, condition);
        return this;
    }

    public AlnQueryUpdate set(final int tableNo, final String column, Object value, final boolean condition) {
        value = getNullable(column, value);
        if(column != null && value != null && condition) {
            columns2.add(new AlnQueryColumn(tables.get(tableNo) + "." + "`" + column + "`", "='" + AlnUtilsText.toString(value) + "'"));
        }
        return this;
    }

    public AlnQueryUpdate only(final boolean only) {
        super.only(only);
        return this;
    }

    public AlnQueryUpdate onlyElse() {
        super.onlyElse();
        return this;
    }

    public AlnQueryUpdate forAll() {
        super.forAll();
        return this;
    }

    public AlnQueryUpdate then() {
        super.then();
        return this;
    }

    public AlnQueryUpdate join(final String table) {
        super.join(table);
        return this;
    }

    public AlnQueryUpdate on(final String column1, final String column2) {
        super.on(column1, column2);
        return this;
    }

    public AlnQueryUpdate on(final int tableNo, final String column1, final String column2) {
        super.on(tableNo, column1, column2);
        return this;
    }

    public AlnQueryUpdate where(final String column) {
        super.where(column);
        return this;
    }

    public AlnQueryUpdate where(final String column, final Object value) {
        super.where(column, value);
        return this;
    }

    public AlnQueryUpdate where(final int tableNo, final String column) {
        super.where(tableNo, column);
        return this;
    }

    public AlnQueryUpdate where(final int tableNo, final String column, final Object value) {
        super.where(tableNo, column, value);
        return this;
    }

    public AlnQueryUpdate where(final String column, final String logic, final Object value) {
        super.where(0, column, logic, value);
        return this;
    }

    public AlnQueryUpdate where(final int tableNo, final String column, final String logic, final Object value) {
        super.where(tableNo, column, logic, value);
        return this;
    }

    public AlnQueryUpdate and(final String column) {
        super.and(column);
        return this;
    }

    public AlnQueryUpdate and(final String column, final Object value) {
        super.and(column, value);
        return this;
    }

    public AlnQueryUpdate and(final String column, final String logic, final Object value) {
        super.and(0, column, logic, value);
        return this;
    }

    public AlnQueryUpdate and(final int tableNo, final String column, final Object value) {
        super.and(tableNo, column, value);
        return this;
    }

    public AlnQueryUpdate and(final int tableNo, final String column, final String logic, final Object value) {
        super.and(tableNo, column, logic, value);
        return this;
    }

    public AlnQueryUpdate or(final String column) {
        super.or(column);
        return this;
    }

    public AlnQueryUpdate or(final String column, final Object value) {
        super.or(column, value);
        return this;
    }

    public AlnQueryUpdate or(final int tableNo, final String column, final Object value) {
        super.or(tableNo, column, value);
        return this;
    }

    public AlnQueryUpdate or(final int tableNo, final String column, final Object value, final boolean condition) {
        super.or(tableNo, column, value, condition);
        return this;
    }

    public AlnQueryUpdate or(final int tableNo, final String column, final String logic, final Object value) {
        super.or(tableNo, column, logic, value);
        return this;
    }

    public AlnQueryUpdate like(final String[] columns, final Object value) {
        super.like(columns, value);
        return this;
    }

    public AlnQueryUpdate like(final String[] columns, final Object value, final boolean condition) {
        super.like(columns, value, condition);
        return this;
    }

    public AlnQueryUpdate andLike(final String column, final Object value) {
        super.andLike(column, value);
        return this;
    }

    public AlnQueryUpdate andLike(final String column, final Object value, final boolean condition) {
        super.andLike(column, value, condition);
        return this;
    }

    public AlnQueryUpdate andLike(final int tableNo, final String column, final Object value) {
        super.andLike(tableNo, column, value);
        return this;
    }

    public AlnQueryUpdate andLike(final int tableNo, final String column, final Object value, boolean condition) {
        super.andLike(tableNo, column, value, condition);
        return this;
    }

    public AlnQueryUpdate orLike(final String column, final Object value) {
        super.orLike(column, value);
        return this;
    }

    public AlnQueryUpdate orLike(final String column, final Object value, boolean condition) {
        super.orLike(column, value, condition);
        return this;
    }

    public AlnQueryUpdate orLike(final int tableNo, final String column, final Object value) {
        super.orLike(tableNo, column, value);
        return this;
    }

    public AlnQueryUpdate orLike(final int tableNo, final String column, final Object value, final boolean condition) {
        super.orLike(tableNo, column, value, condition);
        return this;
    }

    public AlnQueryUpdate andStartWith(final String column, final Object value) {
        super.andStartWith(column, value);
        return this;
    }

    public AlnQueryUpdate andStartWith(final int tableNo, final String column, final Object value) {
        super.andStartWith(tableNo, column, value);
        return this;
    }

    public AlnQueryUpdate orStartWith(final String column, final Object value) {
        super.orStartWith(column, value);
        return this;
    }

    public AlnQueryUpdate orStartWith(final int tableNo, final String column, final Object value) {
        super.orStartWith(tableNo, column, value);
        return this;
    }

    public AlnQueryUpdate orEndWith(final String column, final Object value) {
        super.orEndWith(column, value);
        return this;
    }

    public AlnQueryUpdate orEndWith(final int tableNo, final String column, final Object value) {
        super.orEndWith(tableNo, column, value);
        return this;
    }

    public AlnQueryUpdate andEndWith(final String column, final Object value) {
        super.andEndWith(column, value);
        return this;
    }

    public AlnQueryUpdate andEndWith(final int tableNo, final String column, final Object value) {
        super.andEndWith(tableNo, column, value);
        return this;
    }

    public AlnQueryUpdate order(final String column, final String direction) {
        super.order(column, direction);
        return this;
    }

    public AlnQueryUpdate orderByASC(final String column) {
        super.orderByASC(column);
        return this;
    }

    public AlnQueryUpdate orderByDESC(final String column) {
        super.orderByDESC(column);
        return this;
    }

    public AlnQueryUpdate limit(final int limit) {
        super.limit(limit);
        return this;
    }

    public AlnQueryUpdate offset(final int offset) {
        super.offset(offset);
        return this;
    }

    public AlnQueryUpdate pageMax(final int page, final int max) {
        super.pageMax(page, max);
        return this;
    }

    public String getQuery() {
        query = " UPDATE " + tables.get(0);

        query += join;

        query += " SET";
        for(int i = 0; i < columns2.size(); i++) {
            AlnQueryColumn columnValue = columns2.get(i);
            String column = columnValue.getColumn();
            String value = columnValue.getValue();
            query += (i > 0 ? ", " : " ") + column + value;
        }

        if(!columns1.isEmpty()) {
            query += " WHERE";
            for(int i = 0; i < columns1.size(); i++) {
                final AlnQueryColumn columnValue = columns1.get(i);
                String column = columnValue.getColumn();
                final String value = columnValue.getValue();
                final String condition = i > 0 ? (column.startsWith(" OR ") ? "OR" : "AND") : "";
                column = column.replaceFirst(" AND ", "").replaceFirst(" OR ", "");
                query += " " + condition + " " + column + value;
            }
        }

        query += groupBy;
        query += orderBy + limitOffset;
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

    public boolean executeUpdateSuccess() {
        return executeUpdate() > 0;
    }

    public AlnDaoRes executeUpdateRes() {
        final AlnDaoRes resUpdate = new AlnDaoRes();
        final boolean isUpdated = executeUpdateSuccess();
        if(isUpdated) {
            resUpdate.setSuccess(true);
        }else {
            resUpdate.setError(getError());
            resUpdate.setException(getException());
        }
        return resUpdate;
    }

    public long executeUpdate() {
        long count = 0;
        try {
            final Statement statement = getConnection(auth).createStatement();
            count = statement.executeUpdate(getQuery());
            statement.close();
        }catch(Exception e) {
            setException(e);
        }
        return count;
    }

}
