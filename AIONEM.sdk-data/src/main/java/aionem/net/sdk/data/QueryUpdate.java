package aionem.net.sdk.data;

import aionem.net.sdk.core.utils.UtilsText;
import com.google.gson.JsonObject;

import java.sql.Statement;


public class QueryUpdate extends QueryCondition {

    public QueryUpdate(final String table) {
        super(table);
    }
    public QueryUpdate(final DataAuth auth, final String table) {
        super(auth, table);
    }


    public QueryUpdate params(final Data data) {
        super.params(data);
        return this;
    }

    public QueryUpdate set(final String column) {
        set(0, column);
        return this;
    }
    public QueryUpdate set(final int tableNo, final String column) {
        set(tableNo, column, params, true);
        return this;
    }
    public QueryUpdate set(final String column, final Object value) {
        set(0, column, value);
        return this;
    }
    public QueryUpdate set(final int tableNo, final String column, final Object value) {
        set(tableNo, column, value, true);
        return this;
    }
    public QueryUpdate set(final JsonObject data) {
        set(0, data);
        return this;
    }
    public QueryUpdate set(final int tableNo, final JsonObject data) {
        for(String column : data.keySet()) {
            set(tableNo, column, data.get(column), true);
        }
        return this;
    }
    public QueryUpdate set(final String column, final Data data) {
        set(0, column, data);
        return this;
    }
    public QueryUpdate set(final int tableNo, final String column, final Data data) {
        set(tableNo, column, data, column);
        return this;
    }
    public QueryUpdate set(final String column, final Data data, final String key) {
        set(0, column, data.get(key), data.has(key));
        return this;
    }
    public QueryUpdate set(final int tableNo, final String column, final Data data, final String key) {
        set(tableNo, column, data.get(key), data.has(key));
        return this;
    }
    public QueryUpdate set(final String column, final Object value, final boolean condition) {
        set(0, column, value, condition);
        return this;
    }

    public QueryUpdate set(final int tableNo, final String column, Object value, final boolean condition) {
        value = getNullable(column, value);
        if(column != null && value != null && condition) {
            columns2.add(new QueryColumn(getTable(tableNo), column, "=", value));
        }
        return this;
    }

    public QueryUpdate increment(final String column) {
        increment(0, column);
        return this;
    }
    public QueryUpdate increment(final int tableNo, final String column) {
        increment(tableNo, column, true);
        return this;
    }
    public QueryUpdate increment(final int tableNo, final String column, final boolean condition) {
        adjust(tableNo, column, column, 1, condition);
        return this;
    }

    public QueryUpdate decrement(final String column) {
        decrement(0, column);
        return this;
    }
    public QueryUpdate decrement(final int tableNo, final String column) {
        decrement(tableNo, column, true);
        return this;
    }
    public QueryUpdate decrement(final int tableNo, final String column, final boolean condition) {
        adjust(tableNo, column, column, 1, condition);
        return this;
    }

    public QueryUpdate adjust(final String column, final double value) {
        adjust(0, column, value);
        return this;
    }
    public QueryUpdate adjust(final int tableNo, final String column, final double value) {
        adjust(tableNo, column, column, value);
        return this;
    }
    public QueryUpdate adjust(final int tableNo, final String column1, final String column2, final double value) {
        adjust(tableNo, column1, column2, value, true);
        return this;
    }
    public QueryUpdate adjust(final int tableNo, final String column1, final String column2, final double value, final boolean condition) {
        if(column1 != null && column2 != null && condition) {
            columns2.add(new QueryColumn(getTable(tableNo) + "." + "`" + column1 + "`", "=", column2, "+"+ value));
        }
        return this;
    }

    public QueryUpdate only(final boolean only) {
        super.only(only);
        return this;
    }
    public QueryUpdate onlyElse() {
        super.onlyElse();
        return this;
    }
    public QueryUpdate forAll() {
        super.forAll();
        return this;
    }
    public QueryUpdate then() {
        super.then();
        return this;
    }

    public QueryUpdate join(final String table) {
        super.join(table);
        return this;
    }

    public QueryUpdate on(final String column1, final String column2) {
        super.on(column1, column2);
        return this;
    }

    public QueryUpdate on(final int tableNo, final String column1, final String column2) {
        super.on(tableNo, column1, column2);
        return this;
    }

    public QueryUpdate where(final String column) {
        super.where(column);
        return this;
    }
    public QueryUpdate where(final String column, final Object value) {
        super.where(column, value);
        return this;
    }
    public QueryUpdate where(final int tableNo, final String column) {
        super.where(tableNo, column);
        return this;
    }
    public QueryUpdate where(final int tableNo, final String column, final Object value) {
        super.where(tableNo, column, value);
        return this;
    }
    public QueryUpdate where(final String column, final String logic, final Object value) {
        super.where(0, column, logic, value);
        return this;
    }
    public QueryUpdate where(final int tableNo, final String column, final String logic, final Object value) {
        super.where(tableNo, column, logic, value);
        return this;
    }

    public QueryUpdate and(final String column) {
        super.and(column);
        return this;
    }
    public QueryUpdate and(final String column, final Object value) {
        super.and(column, value);
        return this;
    }
    public QueryUpdate and(final String column, final String logic, final Object value) {
        super.and(0, column, logic, value);
        return this;
    }
    public QueryUpdate and(final int tableNo, final String column, final Object value) {
        super.and(tableNo, column, value);
        return this;
    }
    public QueryUpdate and(final int tableNo, final String column, final String logic, final Object value) {
        super.and(tableNo, column, logic, value);
        return this;
    }

    public QueryUpdate or(final String column) {
        super.or(column);
        return this;
    }
    public QueryUpdate or(final String column, final Object value) {
        super.or(column, value);
        return this;
    }
    public QueryUpdate or(final int tableNo, final String column, final Object value) {
        super.or(tableNo, column, value);
        return this;
    }
    public QueryUpdate or(final int tableNo, final String column, final Object value, final boolean condition) {
        super.or(tableNo, column, value, condition);
        return this;
    }
    public QueryUpdate or(final int tableNo, final String column, final String logic, final Object value) {
        super.or(tableNo, column, logic, value);
        return this;
    }

    public QueryUpdate like(final String[] columns, final Object value) {
        super.like(columns, value);
        return this;
    }
    public QueryUpdate like(final String[] columns, final Object value, final boolean condition) {
        super.like(columns, value, condition);
        return this;
    }

    public QueryUpdate andLike(final String column, final Object value) {
        super.andLike(column, value);
        return this;
    }
    public QueryUpdate andLike(final String column, final Object value, final boolean condition) {
        super.andLike(column, value, condition);
        return this;
    }
    public QueryUpdate andLike(final int tableNo, final String column, final Object value) {
        super.andLike(tableNo, column, value);
        return this;
    }
    public QueryUpdate andLike(final int tableNo, final String column, final Object value, boolean condition) {
        super.andLike(tableNo, column, value, condition);
        return this;
    }

    public QueryUpdate orLike(final String column, final Object value) {
        super.orLike(column, value);
        return this;
    }
    public QueryUpdate orLike(final String column, final Object value, boolean condition) {
        super.orLike(column, value, condition);
        return this;
    }
    public QueryUpdate orLike(final int tableNo, final String column, final Object value) {
        super.orLike(tableNo, column, value);
        return this;
    }
    public QueryUpdate orLike(final int tableNo, final String column, final Object value, final boolean condition) {
        super.orLike(tableNo, column, value, condition);
        return this;
    }

    public QueryUpdate andGreater(final String column, final Object value) {
        super.andGreater(column, value);
        return this;
    }
    public QueryUpdate andGreater(final String column, final Object value, final boolean condition) {
        super.andGreater(column, value, condition);
        return this;
    }
    public QueryUpdate andGreater(final int tableNo, final String column, final Object value) {
        super.andGreater(tableNo, column, value);
        return this;
    }
    public QueryUpdate andGreater(final int tableNo, final String column, final Object value, boolean condition) {
        super.andGreater(tableNo, column, value, condition);
        return this;
    }

    public QueryUpdate orGreater(final String column, final Object value) {
        super.orGreater(column, value);
        return this;
    }
    public QueryUpdate orGreater(final String column, final Object value, boolean condition) {
        super.orGreater(column, value, condition);
        return this;
    }
    public QueryUpdate orGreater(final int tableNo, final String column, final Object value) {
        super.orGreater(tableNo, column, value);
        return this;
    }
    public QueryUpdate orGreater(final int tableNo, final String column, final Object value, final boolean condition) {
        super.orGreater(tableNo, column, value, condition);
        return this;
    }

    public QueryUpdate andLess(final String column, final Object value) {
        super.andLess(column, value);
        return this;
    }
    public QueryUpdate andLess(final String column, final Object value, final boolean condition) {
        super.andLess(column, value, condition);
        return this;
    }
    public QueryUpdate andLess(final int tableNo, final String column, final Object value) {
        super.andLess(tableNo, column, value);
        return this;
    }
    public QueryUpdate andLess(final int tableNo, final String column, final Object value, boolean condition) {
        super.andLess(tableNo, column, value, condition);
        return this;
    }

    public QueryUpdate orLess(final String column, final Object value) {
        super.orLess(column, value);
        return this;
    }
    public QueryUpdate orLess(final String column, final Object value, boolean condition) {
        super.orLess(column, value, condition);
        return this;
    }
    public QueryUpdate orLess(final int tableNo, final String column, final Object value) {
        super.orLess(tableNo, column, value);
        return this;
    }
    public QueryUpdate orLess(final int tableNo, final String column, final Object value, final boolean condition) {
        super.orLess(tableNo, column, value, condition);
        return this;
    }

    public QueryUpdate andStartWith(final String column, final Object value) {
        super.andStartWith(column, value);
        return this;
    }
    public QueryUpdate andStartWith(final int tableNo, final String column, final Object value) {
        super.andStartWith(tableNo, column, value);
        return this;
    }
    public QueryUpdate orStartWith(final String column, final Object value) {
        super.orStartWith(column, value);
        return this;
    }
    public QueryUpdate orStartWith(final int tableNo, final String column, final Object value) {
        super.orStartWith(tableNo, column, value);
        return this;
    }
    public QueryUpdate orEndWith(final String column, final Object value) {
        super.orEndWith(column, value);
        return this;
    }
    public QueryUpdate orEndWith(final int tableNo, final String column, final Object value) {
        super.orEndWith(tableNo, column, value);
        return this;
    }

    public QueryUpdate andEndWith(final String column, final Object value) {
        super.andEndWith(column, value);
        return this;
    }
    public QueryUpdate andEndWith(final int tableNo, final String column, final Object value) {
        super.andEndWith(tableNo, column, value);
        return this;
    }

    public QueryUpdate order(final String column, final String direction) {
        super.order(column, direction);
        return this;
    }
    public QueryUpdate orderByASC(final String column) {
        super.orderByASC(column);
        return this;
    }
    public QueryUpdate orderByDESC(final String column) {
        super.orderByDESC(column);
        return this;
    }

    public QueryUpdate limit(final int limit) {
        super.limit(limit);
        return this;
    }
    public QueryUpdate offset(final int offset) {
        super.offset(offset);
        return this;
    }
    public QueryUpdate pageMax(final int page, final int max) {
        super.pageMax(page, max);
        return this;
    }

    public String getQuery() {
        String query = " UPDATE " + tables.get(0);

        query += join;

        query += " SET";
        for(int i = 0; i < columns2.size(); i++) {
            QueryColumn columnValue = columns2.get(i);
            String column = columnValue.getColumn();
            String value = columnValue.getValue();
            query += (i > 0 ? ", " : " ") + column + value;
        }

        if(!columns1.isEmpty()) {
            query += " WHERE";
            for(int i = 0; i < columns1.size(); i++) {
                final QueryColumn columnValue = columns1.get(i);
                String column = columnValue.getColumn();
                final String value = columnValue.getValue();
                final String condition = i > 0 ? (column.startsWith(" OR ") ? "OR" : "AND") : "";
                column = column.replaceFirst(" AND ", "").replaceFirst(" OR ", "");
                query += " " + condition + " " + column + value;
            }
        }

        query += groupBy;
        query += orderBy + limitOffset;
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

    public boolean executeUpdateSuccess() {
        return executeUpdate() > 0;
    }

    public DaoRes executeUpdateRes() {
        final DaoRes resUpdate = new DaoRes();
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
