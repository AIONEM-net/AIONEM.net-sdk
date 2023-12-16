package aionem.net.sdk.data;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.api.AuthData;

import java.util.List;


public class QueryCondition extends Query {

    protected String join = "";

    protected String groupBy = "";
    protected String orderBy = "";
    protected long offset = 0;
    protected long limit = -1;
    protected String limitOffset = "";

    protected boolean only = true;
    protected boolean oElse = true;

    protected QueryCondition(final String table) {
        super(table);
    }

    protected QueryCondition(final AuthData auth, final String table) {
        super(auth, table);
    }


    @Override
    public QueryCondition params(final Data data) {
        super.params(data);
        return this;
    }

    public QueryCondition only(final boolean only) {
        this.only = only;
        this.oElse = oElse && !only;
        return this;
    }
    public QueryCondition onlyElse() {
        this.only = oElse;
        return this;
    }
    public QueryCondition forAll() {
        this.only = true;
        return this;
    }
    public QueryCondition then() {
        return this;
    }

    public QueryCondition join(final String table) {
        if(table != null && only) {
            this.table = "`" + table + "`";
            this.tables.add(this.table);
            join += " JOIN " + this.table;
        }
        return this;
    }
    public QueryCondition on(final String column1, final String column2) {
        on(0, column1, column2);
        return this;
    }
    public QueryCondition on(final int tableNo, final String column1, final String column2) {
        if(only) {
            join += " ON " + getTable(tableNo) + "." + "`" + column1 + "`" + "=" + "" + table + "." + "`" + column2 + "`";
        }
        return this;
    }

    private QueryCondition condition(final QueryColumn queryColumn) {
        if(queryColumn != null && only) {
            columns1.add(queryColumn);
        }
        return this;
    }

    public QueryCondition where(final String column) {
        where(0, column, params);
        return this;
    }
    public QueryCondition where(final String column, final Object value) {
        where(0, column, value);
        return this;
    }
    public QueryCondition where(final String column, final Object value, final boolean condition) {
        where(0, column, value, condition);
        return this;
    }
    public QueryCondition where(final int tableNo, final String column) {
        where(tableNo, column, params);
        return this;
    }
    public QueryCondition where(final int tableNo, final String column, final Object value) {
        where(tableNo, column, "=", value, true);
        return this;
    }
    public QueryCondition where(final int tableNo, final String column, final Object value, final boolean condition) {
        where(tableNo, column, "=", value, condition);
        return this;
    }
    public QueryCondition where(final int tableNo, final String column, final String logic, final Object value) {
        return where(tableNo, column, logic, value, true);
    }
    public QueryCondition where(final int tableNo, final String column, final String logic, Object value, final boolean condition) {
        value = getNullable(column, value);
        if(column != null && value != null && condition && only) {
            condition(new QueryColumn(getTable(tableNo), column, logic, value));
        }
        return this;
    }

    public QueryCondition and(final String column) {
        and(column, params);
        return this;
    }
    public QueryCondition andNotEmpty(final String column) {
        and(column, params, !params.isEmpty(column));
        return this;
    }
    public QueryCondition and(final String column, final Object value) {
        and(0, column, value);
        return this;
    }
    public QueryCondition and(final String column, final Object value, final boolean condition) {
        and(0, column, value, condition);
        return this;
    }
    public QueryCondition and(final int tableNo, final String column, final Object value) {
        and(tableNo, column, value, true);
        return this;
    }
    public QueryCondition and(final int tableNo, final String column, final Object value, boolean condition) {
        and(tableNo, column, "=", value, condition);
        return this;
    }
    public QueryCondition and(final int tableNo, final String column, final String logic, final Object value) {
        and(tableNo, column, logic, value, true);
        return this;
    }
    public QueryCondition and(final int tableNo, final String column, final String logic, Object value, final boolean condition) {
        value = getNullable(column, value);
        if(column != null && value != null && condition && only) {
            condition(new QueryColumn("AND", getTable(tableNo), column, logic, value));
        }
        return this;
    }

    public QueryCondition or(final String column) {
        or(0, column, "=", params);
        return this;
    }
    public QueryCondition or(final String column, final Object value) {
        or(0, column, "=", value);
        return this;
    }
    public QueryCondition or(final int tableNo, final String column, final Object value) {
        or(tableNo, column, "=", value);
        return this;
    }
    public QueryCondition or(final String column, final Object value, final boolean condition) {
        or(0, column, value, condition);
        return this;
    }
    public QueryCondition or(final String column, final List<String> values) {
        for(String value : values) {
            or(0, column, "=", value);
        }
        return this;
    }
    public QueryCondition or(final int tableNo, final String column, final List<String> values) {
        for(String value : values) {
            or(tableNo, column, "=", value);
        }
        return this;
    }
    public QueryCondition or(final String column, final String logic, final Object value) {
        or(0, column, logic, value);
        return this;
    }
    public QueryCondition or(final int tableNo, final String column, final Object value, final boolean condition) {
        or(tableNo, column, "=", value, condition);
        return this;
    }
    public QueryCondition or(final int tableNo, final String column, final String logic, final Object value) {
        or(tableNo, column, logic, value, true);
        return this;
    }
    public QueryCondition or(final int tableNo, final String column, final String logic, Object value, final boolean condition) {
        value = getNullable(column, value);
        if(column != null && value != null && condition && only) {
            condition(new QueryColumn("OR", getTable(tableNo), column, logic, value));
        }
        return this;
    }

    public QueryCondition like(final String[] columns, final Object value) {
        return like(columns, value, true);
    }
    public QueryCondition like(final String[] columns, final Object value, final boolean condition) {
        for(int i = 0; i < columns.length; i++) {
            final String column = columns[i];
            if(i == 0) {
                andLike(column, value, condition);
            }else {
                orLike(column, value, condition);
            }
        }
        return this;
    }

    public QueryCondition andLike(final String column, final Object value) {
        return andLike(column, value, true);
    }
    public QueryCondition andLike(final String column, Object value, final boolean condition) {
        return andLike(0, column, value, condition);
    }
    public QueryCondition andLike(final int tableNo, final String column, final Object value) {
        return andLike(tableNo, column, value, true);
    }
    public QueryCondition andLike(final int tableNo, final String column, Object value, final boolean condition) {
        value = getNullable(column, value);
        if(column != null && value != null && condition && only) {
            condition(new QueryColumn("AND", getTable(tableNo), column, "LIKE", "%" + UtilsText.toString(value) + "%"));
        }
        return this;
    }

    public QueryCondition orLike(final String column, final Object value) {
        return orLike(column, value, true);
    }
    public QueryCondition orLike(final String column, final Object value, boolean condition) {
        return orLike(0, column, value, condition);
    }
    public QueryCondition orLike(final int tableNo, final String column, final Object value) {
        return orLike(tableNo, column, value, true);
    }
    public QueryCondition orLike(final int tableNo, final String column, Object value, final boolean condition) {
        value = getNullable(column, value);
        if(column != null && value != null && condition && only) {
            condition(new QueryColumn("OR", getTable(tableNo), column, "LIKE", "%" + UtilsText.toString(value) + "%"));
        }
        return this;
    }

    public QueryCondition andGreater(final String column, final Object value) {
        return andGreater(column, value, true);
    }
    public QueryCondition andGreater(final String column, Object value, final boolean condition) {
        return andGreater(0, column, value, condition);
    }
    public QueryCondition andGreater(final int tableNo, final String column, final Object value) {
        return andGreater(tableNo, column, value, true);
    }
    public QueryCondition andGreater(final int tableNo, final String column, Object value, final boolean condition) {
        value = getNullable(column, value);
        if(column != null && value != null && condition && only) {
            condition(new QueryColumn("AND", getTable(tableNo), column, ">", value));
        }
        return this;
    }

    public QueryCondition orGreater(final String column, final Object value) {
        return orGreater(column, value, true);
    }
    public QueryCondition orGreater(final String column, final Object value, boolean condition) {
        return orGreater(0, column, value, condition);
    }
    public QueryCondition orGreater(final int tableNo, final String column, final Object value) {
        return orGreater(tableNo, column, value, true);
    }
    public QueryCondition orGreater(final int tableNo, final String column, Object value, final boolean condition) {
        value = getNullable(column, value);
        if(column != null && value != null && condition && only) {
            condition(new QueryColumn("OR", getTable(tableNo), column, ">", value));
        }
        return this;
    }

    public QueryCondition andLess(final String column, final Object value) {
        return andLess(column, value, true);
    }
    public QueryCondition andLess(final String column, Object value, final boolean condition) {
        return andLess(0, column, value, condition);
    }
    public QueryCondition andLess(final int tableNo, final String column, final Object value) {
        return andLess(tableNo, column, value, true);
    }
    public QueryCondition andLess(final int tableNo, final String column, Object value, final boolean condition) {
        value = getNullable(column, value);
        if(column != null && value != null && condition && only) {
            condition(new QueryColumn("AND", getTable(tableNo), column, "<", value));
        }
        return this;
    }

    public QueryCondition orLess(final String column, final Object value) {
        return orLess(column, value, true);
    }
    public QueryCondition orLess(final String column, final Object value, boolean condition) {
        return orLess(0, column, value, condition);
    }
    public QueryCondition orLess(final int tableNo, final String column, final Object value) {
        return orLess(tableNo, column, value, true);
    }
    public QueryCondition orLess(final int tableNo, final String column, Object value, final boolean condition) {
        value = getNullable(column, value);
        if(column != null && value != null && condition && only) {
            condition(new QueryColumn("OR", getTable(tableNo), column, "<", value));
        }
        return this;
    }

    public QueryCondition andStartWith(final String column, final Object value) {
        return andStartWith(0, column, value);
    }
    public QueryCondition andStartWith(final int tableNo, final String column, Object value) {
        value = getNullable(column, value);
        if(column != null && value != null && only) {
            condition(new QueryColumn("AND", getTable(tableNo), column, "LIKE", "'" + UtilsText.toString(value) + "%'"));
        }
        return this;
    }
    public QueryCondition orStartWith(final String column, final Object value) {
        return orStartWith(0, column, value);
    }
    public QueryCondition orStartWith(final int tableNo, final String column, Object value) {
        value = getNullable(column, value);
        if(column != null && value != null && only) {
            condition(new QueryColumn("OR", getTable(tableNo), column, "LIKE", "'" + UtilsText.toString(value) + "%'"));
        }
        return this;
    }

    public QueryCondition andEndWith(final String column, final Object value) {
        return orEndWith(0, column, value);
    }
    public QueryCondition andEndWith(final int tableNo, final String column, Object value) {
        value = getNullable(column, value);
        if(column != null && value != null && only) {
            condition(new QueryColumn("AND", getTable(tableNo), column, "LIKE", "'%" + UtilsText.toString(value) + "'"));
        }
        return this;
    }
    public QueryCondition orEndWith(final String column, final Object value) {
        return orEndWith(0, column, value);
    }
    public QueryCondition orEndWith(final int tableNo, final String column, Object value) {
        value = getNullable(column, value);
        if(column != null && value != null && only) {
            condition(new QueryColumn("OR", getTable(tableNo), column, "LIKE", "%" + UtilsText.toString(value) + ""));
        }
        return this;
    }


    public QueryCondition groupBy(final String... columns) {
        return groupBy(0, columns);
    }
    public QueryCondition groupBy(final int tableNo, final String... columns) {
        if(columns != null && only) {
            for(int i = 0; i < columns.length; i++) {
                String column = columns[i];
                if(column != null) {
                    if(i == 0) {

                        if(UtilsText.isEmpty(groupBy)) {
                            groupBy = " GROUP BY ";
                        }else {
                            groupBy += ", ";
                        }

                    }else {
                        groupBy += ", ";
                    }

                    groupBy += getTableColumn(tableNo, column);
                }
            }
        }
        return this;
    }

    public QueryCondition orderByASC(final String column) {
        return orderByASC(0, column);
    }
    public QueryCondition orderByASC(final int tableNo, final String column) {
        order(tableNo, column, "ASC");
        return this;
    }
    public QueryCondition orderByDESC(final String column) {
        return orderByDESC(0, column);
    }
    public QueryCondition orderByDESC(final int tableNo, final String column) {
        order(tableNo, column, "DESC");
        return this;
    }
    public QueryCondition order(final String column, final String direction) {
        return order(0, column, direction);
    }
    public QueryCondition order(final int tableNo, final String column, String direction) {
        if(!UtilsText.isEmpty(column) && only) {
            direction = UtilsText.notEmpty(direction, "ASC");
            orderBy = " ORDER BY " + getTableColumn(tableNo, column) + " " + direction;
        }
        return this;
    }

    public QueryCondition limit(final int limit) {
        this.limit = limit;
        this.limitOffset = getLimitOffset();
        return this;
    }
    public QueryCondition offset(final int offset) {
        this.offset = offset;
        this.limitOffset = getLimitOffset();
        return this;
    }
    private String getLimitOffset() {
        if(this.offset <= 0 && this.limit < 0) {
            this.limitOffset = "";
        }else {
            this.limitOffset = " LIMIT " + (this.offset >= 0 ? this.offset : 0) + (this.limit >= 0 ? ", " + this.limit : "");
        }
        return limitOffset;
    }
    public QueryCondition pageMax(final int page, final int max) {
        limit(max);
        offset(page <= 0 ? 0 : (page * max) - max);
        return this;
    }

}
