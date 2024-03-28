package aionem.net.sdk.data.query;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.beans.DaoRes;
import aionem.net.sdk.data.beans.Data;

import java.sql.SQLException;
import java.sql.Statement;


public class QueryDelete extends QueryCondition {

    public QueryDelete(final String table) {
        super(table);
    }


    @Override
    public QueryDelete params(final Data data) {
        super.params(data);
        return this;
    }

    public QueryDelete only(final boolean only) {
        super.only(only);
        return this;
    }
    public QueryDelete onlyElse() {
        super.onlyElse();
        return this;
    }
    public QueryDelete forAll() {
        super.forAll();
        return this;
    }
    public QueryDelete then() {
        super.then();
        return this;
    }

    public QueryDelete where(final String column) {
        super.where(column);
        return this;
    }
    public QueryDelete where(final String column, final Object value) {
        super.where(column, value);
        return this;
    }
    public QueryDelete where(final int tableNo, final String column) {
        super.where(tableNo, column);
        return this;
    }
    public QueryDelete where(final int tableNo, final String column, final Object value) {
        super.where(tableNo, column, value);
        return this;
    }
    public QueryDelete where(final String column, final String logic, final Object value) {
        super.where(0, column, logic, value);
        return this;
    }
    public QueryDelete where(final int tableNo, final String column, final String logic, final Object value) {
        super.where(tableNo, column, logic, value);
        return this;
    }

    public QueryDelete and(final String column, final Object value) {
        super.and(column, value);
        return this;
    }
    public QueryDelete and(final String column, final String logic, final Object value) {
        super.and(0, column, logic, value);
        return this;
    }
    public QueryDelete and(final int tableNo, final String column, final Object value) {
        super.and(tableNo, column, value);
        return this;
    }
    public QueryDelete and(final int tableNo, final String column, final String logic, final Object value) {
        super.and(tableNo, column, logic, value);
        return this;
    }

    public QueryDelete or(final String column) {
        super.or(column);
        return this;
    }
    public QueryDelete or(final String column, final Object value) {
        super.or(column, value);
        return this;
    }
    public QueryDelete or(final int tableNo, final String column, final Object value) {
        super.or(tableNo, column, value);
        return this;
    }
    public QueryDelete or(final int tableNo, final String column, final Object value, final boolean condition) {
        super.or(tableNo, column, value, condition);
        return this;
    }
    public QueryDelete or(final int tableNo, final String column, final String logic, final Object value) {
        super.or(tableNo, column, logic, value);
        return this;
    }

    public QueryDelete like(final String[] columns, final Object value) {
        super.like(columns, value);
        return this;
    }
    public QueryDelete like(final String[] columns, final Object value, final boolean condition) {
        super.like(columns, value, condition);
        return this;
    }

    public QueryDelete andLike(final String column, final Object value) {
        super.andLike(column, value);
        return this;
    }
    public QueryDelete andLike(final String column, final Object value, final boolean condition) {
        super.andLike(column, value, condition);
        return this;
    }
    public QueryDelete andLike(final int tableNo, final String column, final Object value) {
        super.andLike(tableNo, column, value);
        return this;
    }
    public QueryDelete andLike(final int tableNo, final String column, final Object value, final boolean condition) {
        super.andLike(tableNo, column, value, condition);
        return this;
    }

    public QueryDelete orLike(final String column, final Object value) {
        super.orLike(column, value);
        return this;
    }
    public QueryDelete orLike(final String column, final Object value, final boolean condition) {
        super.orLike(column, value, condition);
        return this;
    }
    public QueryDelete orLike(final int tableNo, final String column, final Object value) {
        super.orLike(tableNo, column, value);
        return this;
    }
    public QueryDelete orLike(final int tableNo, final String column, final Object value, final boolean condition) {
        super.orLike(tableNo, column, value, condition);
        return this;
    }

    public QueryDelete andGreater(final String column, final Object value) {
        super.andGreater(column, value);
        return this;
    }
    public QueryDelete andGreater(final String column, final Object value, final boolean condition) {
        super.andGreater(column, value, condition);
        return this;
    }
    public QueryDelete andGreater(final int tableNo, final String column, final Object value) {
        super.andGreater(tableNo, column, value);
        return this;
    }
    public QueryDelete andGreater(final int tableNo, final String column, final Object value, final boolean condition) {
        super.andGreater(tableNo, column, value, condition);
        return this;
    }

    public QueryDelete orGreater(final String column, final Object value) {
        super.orGreater(column, value);
        return this;
    }
    public QueryDelete orGreater(final String column, final Object value, final boolean condition) {
        super.orGreater(column, value, condition);
        return this;
    }
    public QueryDelete orGreater(final int tableNo, final String column, final Object value) {
        super.orGreater(tableNo, column, value);
        return this;
    }
    public QueryDelete orGreater(final int tableNo, final String column, final Object value, final boolean condition) {
        super.orGreater(tableNo, column, value, condition);
        return this;
    }

    public QueryDelete andLess(final String column, final Object value) {
        super.andLess(column, value);
        return this;
    }
    public QueryDelete andLess(final String column, final Object value, final boolean condition) {
        super.andLess(column, value, condition);
        return this;
    }
    public QueryDelete andLess(final int tableNo, final String column, final Object value) {
        super.andLess(tableNo, column, value);
        return this;
    }
    public QueryDelete andLess(final int tableNo, final String column, final Object value, final boolean condition) {
        super.andLess(tableNo, column, value, condition);
        return this;
    }

    public QueryDelete orLess(final String column, final Object value) {
        super.orLess(column, value);
        return this;
    }
    public QueryDelete orLess(final String column, final Object value, final boolean condition) {
        super.orLess(column, value, condition);
        return this;
    }
    public QueryDelete orLess(final int tableNo, final String column, final Object value) {
        super.orLess(tableNo, column, value);
        return this;
    }
    public QueryDelete orLess(final int tableNo, final String column, final Object value, final boolean condition) {
        super.orLess(tableNo, column, value, condition);
        return this;
    }

    public QueryDelete andStartWith(final String column, final Object value) {
        super.andStartWith(column, value);
        return this;
    }
    public QueryDelete andStartWith(final int tableNo, final String column, final Object value) {
        super.andStartWith(tableNo, column, value);
        return this;
    }
    public QueryDelete orStartWith(final String column, final Object value) {
        super.orStartWith(column, value);
        return this;
    }
    public QueryDelete orStartWith(final int tableNo, final String column, final Object value) {
        super.orStartWith(tableNo, column, value);
        return this;
    }

    public QueryDelete andEndWith(final String column, final Object value) {
        super.andEndWith(column, value);
        return this;
    }
    public QueryDelete andEndWith(final int tableNo, final String column, final Object value) {
        super.andEndWith(tableNo, column, value);
        return this;
    }
    public QueryDelete orEndWith(final String column, final Object value) {
        super.orEndWith(column, value);
        return this;
    }
    public QueryDelete orEndWith(final int tableNo, final String column, final Object value) {
        super.orEndWith(tableNo, column, value);
        return this;
    }

    public QueryDelete order(final String column, final String direction) {
        super.order(column, direction);
        return this;
    }
    public QueryDelete orderByASC(final String column) {
        super.orderByASC(column);
        return this;
    }
    public QueryDelete orderByDESC(final String column) {
        super.orderByDESC(column);
        return this;
    }

    public QueryDelete limit(final int limit) {
        super.limit(limit);
        return this;
    }
    public QueryDelete offset(final int offset) {
        super.offset(offset);
        return this;
    }

    public QueryDelete pageMax(final int page, final int max) {
        super.pageMax(page, max);
        return this;
    }

    public String getQuery() {
        String query = " DELETE FROM " + table + " WHERE";
        for(int i = 0; i < columns1.size(); i++) {
            final QueryColumn columnValue = columns1.get(i);
            String column = columnValue.getColumn();
            final String value = columnValue.getValue();
            final String condition = i > 0 ? (column.startsWith(" OR ") ? " OR" : " AND") : "";
            column = column.replaceFirst(" AND ", "").replaceFirst(" OR ", "");
            query += condition + " " + column + value;
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

    public DaoRes executeDeleteRes() {
        final DaoRes resDelete = new DaoRes();
        try {
            final boolean isDeleted = executeDeleteSuccess();
            if(isDeleted) {
                resDelete.setSuccess(true);
            }else {
                resDelete.setError(getError());
                resDelete.setException(getException());
            }
        } catch (SQLException e) {
            resDelete.setException(e);
        }
        return resDelete;
    }

    public boolean executeDeleteSuccess() throws SQLException {
        return executeDelete() > 0;
    }

    public long executeDelete() throws SQLException {
        long count = -1;
        try {
            final Statement statement = getConnection().createStatement();
            count = statement.executeUpdate(getQuery());
            statement.close();
        }catch (final SQLException e) {
            setException(e);
            throw e;
        }
        return count;
    }

}
