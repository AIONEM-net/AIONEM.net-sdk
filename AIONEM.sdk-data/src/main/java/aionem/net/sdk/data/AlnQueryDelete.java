package aionem.net.sdk.data;

import aionem.net.sdk.core.utils.AlnUtilsText;
import aionem.net.sdk.data.api.AlnAuthData;

import java.sql.Statement;


public class AlnQueryDelete extends AlnQueryCondition {

    public AlnQueryDelete(final String table) {
        super(table);
    }
    public AlnQueryDelete(final AlnAuthData auth, final String table) {
        super(auth, table);
    }


    @Override
    public AlnQueryDelete params(final AlnData data) {
        super.params(data);
        return this;
    }

    public AlnQueryDelete only(final boolean only) {
        super.only(only);
        return this;
    }
    public AlnQueryDelete onlyElse() {
        super.onlyElse();
        return this;
    }
    public AlnQueryDelete forAll() {
        super.forAll();
        return this;
    }
    public AlnQueryDelete then() {
        super.then();
        return this;
    }

    public AlnQueryDelete where(final String column) {
        super.where(column);
        return this;
    }
    public AlnQueryDelete where(final String column, final Object value) {
        super.where(column, value);
        return this;
    }
    public AlnQueryDelete where(final int tableNo, final String column) {
        super.where(tableNo, column);
        return this;
    }
    public AlnQueryDelete where(final int tableNo, final String column, final Object value) {
        super.where(tableNo, column, value);
        return this;
    }
    public AlnQueryDelete where(final String column, final String logic, final Object value) {
        super.where(0, column, logic, value);
        return this;
    }
    public AlnQueryDelete where(final int tableNo, final String column, final String logic, final Object value) {
        super.where(tableNo, column, logic, value);
        return this;
    }

    public AlnQueryDelete and(final String column, final Object value) {
        super.and(column, value);
        return this;
    }
    public AlnQueryDelete and(final String column, final String logic, final Object value) {
        super.and(0, column, logic, value);
        return this;
    }
    public AlnQueryDelete and(final int tableNo, final String column, final Object value) {
        super.and(tableNo, column, value);
        return this;
    }
    public AlnQueryDelete and(final int tableNo, final String column, final String logic, final Object value) {
        super.and(tableNo, column, logic, value);
        return this;
    }

    public AlnQueryDelete or(final String column) {
        super.or(column);
        return this;
    }
    public AlnQueryDelete or(final String column, final Object value) {
        super.or(column, value);
        return this;
    }
    public AlnQueryDelete or(final int tableNo, final String column, final Object value) {
        super.or(tableNo, column, value);
        return this;
    }
    public AlnQueryDelete or(final int tableNo, final String column, final Object value, final boolean condition) {
        super.or(tableNo, column, value, condition);
        return this;
    }
    public AlnQueryDelete or(final int tableNo, final String column, final String logic, final Object value) {
        super.or(tableNo, column, logic, value);
        return this;
    }

    public AlnQueryDelete like(final String[] columns, final Object value) {
        super.like(columns, value);
        return this;
    }
    public AlnQueryDelete like(final String[] columns, final Object value, final boolean condition) {
        super.like(columns, value, condition);
        return this;
    }

    public AlnQueryDelete andLike(final String column, final Object value) {
        super.andLike(column, value);
        return this;
    }
    public AlnQueryDelete andLike(final String column, final Object value, final boolean condition) {
        super.andLike(column, value, condition);
        return this;
    }
    public AlnQueryDelete andLike(final int tableNo, final String column, final Object value) {
        super.andLike(tableNo, column, value);
        return this;
    }
    public AlnQueryDelete andLike(final int tableNo, final String column, final Object value, final boolean condition) {
        super.andLike(tableNo, column, value, condition);
        return this;
    }

    public AlnQueryDelete orLike(final String column, final Object value) {
        super.orLike(column, value);
        return this;
    }
    public AlnQueryDelete orLike(final String column, final Object value, final boolean condition) {
        super.orLike(column, value, condition);
        return this;
    }
    public AlnQueryDelete orLike(final int tableNo, final String column, final Object value) {
        super.orLike(tableNo, column, value);
        return this;
    }
    public AlnQueryDelete orLike(final int tableNo, final String column, final Object value, final boolean condition) {
        super.orLike(tableNo, column, value, condition);
        return this;
    }

    public AlnQueryDelete andGreater(final String column, final Object value) {
        super.andGreater(column, value);
        return this;
    }
    public AlnQueryDelete andGreater(final String column, final Object value, final boolean condition) {
        super.andGreater(column, value, condition);
        return this;
    }
    public AlnQueryDelete andGreater(final int tableNo, final String column, final Object value) {
        super.andGreater(tableNo, column, value);
        return this;
    }
    public AlnQueryDelete andGreater(final int tableNo, final String column, final Object value, final boolean condition) {
        super.andGreater(tableNo, column, value, condition);
        return this;
    }

    public AlnQueryDelete orGreater(final String column, final Object value) {
        super.orGreater(column, value);
        return this;
    }
    public AlnQueryDelete orGreater(final String column, final Object value, final boolean condition) {
        super.orGreater(column, value, condition);
        return this;
    }
    public AlnQueryDelete orGreater(final int tableNo, final String column, final Object value) {
        super.orGreater(tableNo, column, value);
        return this;
    }
    public AlnQueryDelete orGreater(final int tableNo, final String column, final Object value, final boolean condition) {
        super.orGreater(tableNo, column, value, condition);
        return this;
    }

    public AlnQueryDelete andLess(final String column, final Object value) {
        super.andLess(column, value);
        return this;
    }
    public AlnQueryDelete andLess(final String column, final Object value, final boolean condition) {
        super.andLess(column, value, condition);
        return this;
    }
    public AlnQueryDelete andLess(final int tableNo, final String column, final Object value) {
        super.andLess(tableNo, column, value);
        return this;
    }
    public AlnQueryDelete andLess(final int tableNo, final String column, final Object value, final boolean condition) {
        super.andLess(tableNo, column, value, condition);
        return this;
    }

    public AlnQueryDelete orLess(final String column, final Object value) {
        super.orLess(column, value);
        return this;
    }
    public AlnQueryDelete orLess(final String column, final Object value, final boolean condition) {
        super.orLess(column, value, condition);
        return this;
    }
    public AlnQueryDelete orLess(final int tableNo, final String column, final Object value) {
        super.orLess(tableNo, column, value);
        return this;
    }
    public AlnQueryDelete orLess(final int tableNo, final String column, final Object value, final boolean condition) {
        super.orLess(tableNo, column, value, condition);
        return this;
    }

    public AlnQueryDelete andStartWith(final String column, final Object value) {
        super.andStartWith(column, value);
        return this;
    }
    public AlnQueryDelete andStartWith(final int tableNo, final String column, final Object value) {
        super.andStartWith(tableNo, column, value);
        return this;
    }
    public AlnQueryDelete orStartWith(final String column, final Object value) {
        super.orStartWith(column, value);
        return this;
    }
    public AlnQueryDelete orStartWith(final int tableNo, final String column, final Object value) {
        super.orStartWith(tableNo, column, value);
        return this;
    }

    public AlnQueryDelete andEndWith(final String column, final Object value) {
        super.andEndWith(column, value);
        return this;
    }
    public AlnQueryDelete andEndWith(final int tableNo, final String column, final Object value) {
        super.andEndWith(tableNo, column, value);
        return this;
    }
    public AlnQueryDelete orEndWith(final String column, final Object value) {
        super.orEndWith(column, value);
        return this;
    }
    public AlnQueryDelete orEndWith(final int tableNo, final String column, final Object value) {
        super.orEndWith(tableNo, column, value);
        return this;
    }

    public AlnQueryDelete order(final String column, final String direction) {
        super.order(column, direction);
        return this;
    }
    public AlnQueryDelete orderByASC(final String column) {
        super.orderByASC(column);
        return this;
    }
    public AlnQueryDelete orderByDESC(final String column) {
        super.orderByDESC(column);
        return this;
    }

    public AlnQueryDelete limit(final int limit) {
        super.limit(limit);
        return this;
    }
    public AlnQueryDelete offset(final int offset) {
        super.offset(offset);
        return this;
    }

    public AlnQueryDelete pageMax(final int page, final int max) {
        super.pageMax(page, max);
        return this;
    }

    public String getQuery() {
        query = " DELETE FROM " + table + " WHERE";
        for(int i = 0; i < columns1.size(); i++) {
            final AlnQueryColumn columnValue = columns1.get(i);
            String column = columnValue.getColumn();
            final String value = columnValue.getValue();
            final String condition = i > 0 ? (column.startsWith(" OR ") ? " OR" : " AND") : "";
            column = column.replaceFirst(" AND ", "").replaceFirst(" OR ", "");
            query += condition + " " + column + value;
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

    public boolean executeDeleteSuccess() {
        return executeDelete() > 0;
    }

    public long executeDelete() {
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
