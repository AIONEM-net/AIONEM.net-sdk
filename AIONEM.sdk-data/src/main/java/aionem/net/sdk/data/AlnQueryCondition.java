package aionem.net.sdk.data;

import aionem.net.sdk.core.utils.AlnUtilsText;
import aionem.net.sdk.data.api.AlnAuthData;

import java.util.List;


public class AlnQueryCondition extends AlnQuery {

    protected String join = "";

    protected String groupBy = "";
    protected String orderBy = "";
    protected long offset = 0;
    protected long limit = -1;
    protected String limitOffset = "";

    protected boolean only = true;
    protected boolean oElse = true;

    protected AlnQueryCondition(final String table) {
        super(table);
    }

    protected AlnQueryCondition(final AlnAuthData auth, final String table) {
        super(auth, table);
    }


    @Override
    public AlnQueryCondition data(final AlnData data) {
        super.data(data);
        return this;
    }

    public AlnQueryCondition only(final boolean only) {
        this.only = only;
        this.oElse = oElse && !only;
        return this;
    }

    public AlnQueryCondition onlyElse() {
        this.only = oElse;
        return this;
    }

    public AlnQueryCondition forAll() {
        this.only = true;
        return this;
    }

    public AlnQueryCondition then() {
        return this;
    }

    public AlnQueryCondition join(final String table) {
        if(table != null && only) {
            this.table = "`" + table + "`";
            this.tables.add(this.table);
            join += " JOIN " + this.table;
        }
        return this;
    }

    public AlnQueryCondition on(final String column1, final String column2) {
        on(0, column1, column2);
        return this;
    }

    public AlnQueryCondition on(final int tableNo, final String column1, final String column2) {
        if(only) {
            join += " ON " + tables.get(tableNo) + "." + "`" + column1 + "`" + "=" + "" + table + "." + "`" + column2 + "`";
        }
        return this;
    }

    public AlnQueryCondition where(final String column) {
        where(0, column, data);
        return this;
    }

    public AlnQueryCondition where(final String column, final Object value) {
        where(0, column, value);
        return this;
    }

    public AlnQueryCondition where(final String column, final Object value, final boolean condition) {
        where(0, column, value, condition);
        return this;
    }

    public AlnQueryCondition where(final int tableNo, final String column) {
        where(tableNo, column, data);
        return this;
    }

    public AlnQueryCondition where(final int tableNo, final String column, final Object value) {
        where(tableNo, column, "=", value, true);
        return this;
    }

    public AlnQueryCondition where(final int tableNo, final String column, final Object value, final boolean condition) {
        where(tableNo, column, "=", value, condition);
        return this;
    }

    public AlnQueryCondition where(final int tableNo, final String column, final String logic, final Object value) {
        return where(tableNo, column, logic, value, true);
    }

    public AlnQueryCondition where(final int tableNo, final String column, final String logic, Object value, final boolean condition) {
        value = getNullable(column, value);
        if(column != null && value != null && condition && only) {
            columns1.add(new AlnQueryColumn(tables.get(tableNo) + "." + "`" + column + "`", logic + "'" + AlnUtilsText.toString(value) + "'"));
        }
        return this;
    }

    public AlnQueryCondition and(final String column) {
        and(column, data);
        return this;
    }

    public AlnQueryCondition andNotEmpty(final String column) {
        and(column, data, !data.isEmpty(column));
        return this;
    }

    public AlnQueryCondition and(final String column, final Object value) {
        and(0, column, value);
        return this;
    }

    public AlnQueryCondition and(final String column, final Object value, final boolean condition) {
        and(0, column, value, condition);
        return this;
    }

    public AlnQueryCondition and(final int tableNo, final String column, final Object value) {
        and(tableNo, column, value, true);
        return this;
    }

    public AlnQueryCondition and(final int tableNo, final String column, final Object value, boolean condition) {
        and(tableNo, column, "=", value, condition);
        return this;
    }

    public AlnQueryCondition and(final int tableNo, final String column, final String logic, final Object value) {
        and(tableNo, column, logic, value, true);
        return this;
    }

    public AlnQueryCondition and(final int tableNo, final String column, final String logic, Object value, final boolean condition) {
        value = getNullable(column, value);
        if(column != null && value != null && condition && only) {
            columns1.add(new AlnQueryColumn(" AND " + tables.get(tableNo) + "." + "`" + column + "`", logic + "'" + AlnUtilsText.toString(value) + "'"));
        }
        return this;
    }

    public AlnQueryCondition or(final String column) {
        or(0, column, "=", data);
        return this;
    }

    public AlnQueryCondition or(final String column, final Object value) {
        or(0, column, "=", value);
        return this;
    }

    public AlnQueryCondition or(final int tableNo, final String column, final Object value) {
        or(tableNo, column, "=", value);
        return this;
    }

    public AlnQueryCondition or(final String column, final Object value, final boolean condition) {
        or(0, column, value, condition);
        return this;
    }

    public AlnQueryCondition or(final String column, final List<String> values) {
        for(String value : values) {
            or(0, column, "=", value);
        }
        return this;
    }

    public AlnQueryCondition or(final int tableNo, final String column, final List<String> values) {
        for(String value : values) {
            or(tableNo, column, "=", value);
        }
        return this;
    }

    public AlnQueryCondition or(final String column, final String logic, final Object value) {
        or(0, column, logic, value);
        return this;
    }

    public AlnQueryCondition or(final int tableNo, final String column, final Object value, final boolean condition) {
        or(tableNo, column, "=", value, condition);
        return this;
    }

    public AlnQueryCondition or(final int tableNo, final String column, final String logic, final Object value) {
        or(tableNo, column, logic, value, true);
        return this;
    }

    public AlnQueryCondition or(final int tableNo, final String column, final String logic, Object value, final boolean condition) {
        value = getNullable(column, value);
        if(column != null && value != null && condition && only) {
            columns1.add(new AlnQueryColumn(" OR " + tables.get(tableNo) + "." + "`" + column + "`", logic + "'" + AlnUtilsText.toString(value) + "'"));
        }
        return this;
    }

    public AlnQueryCondition like(final String[] columns, final Object value) {
        return like(columns, value, true);
    }

    public AlnQueryCondition like(final String[] columns, final Object value, final boolean condition) {
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

    public AlnQueryCondition andLike(final String column, final Object value) {
        return andLike(column, value, true);
    }

    public AlnQueryCondition andLike(final String column, Object value, final boolean condition) {
        return andLike(0, column, value, condition);
    }

    public AlnQueryCondition andLike(final int tableNo, final String column, final Object value) {
        return andLike(tableNo, column, value, true);
    }

    public AlnQueryCondition andLike(final int tableNo, final String column, Object value, final boolean condition) {
        value = getNullable(column, value);
        if(column != null && value != null && condition && only) {
            columns1.add(new AlnQueryColumn(" AND " + tables.get(tableNo) + "." + "`" + column + "`", " LIKE '%" + AlnUtilsText.toString(value) + "%'"));
        }
        return this;
    }

    public AlnQueryCondition orLike(final String column, final Object value) {
        return orLike(column, value, true);
    }

    public AlnQueryCondition orLike(final String column, final Object value, boolean condition) {
        return orLike(0, column, value, condition);
    }

    public AlnQueryCondition orLike(final int tableNo, final String column, final Object value) {
        return orLike(tableNo, column, value, true);
    }

    public AlnQueryCondition orLike(final int tableNo, final String column, Object value, final boolean condition) {
        value = getNullable(column, value);
        if(column != null && value != null && condition && only) {
            columns1.add(new AlnQueryColumn(" OR " + tables.get(tableNo) + "." + "`" + column + "`", " LIKE '%" + AlnUtilsText.toString(value) + "%'"));
        }
        return this;
    }

    public AlnQueryCondition andStartWith(final String column, final Object value) {
        return andStartWith(0, column, value);
    }

    public AlnQueryCondition andStartWith(final int tableNo, final String column, Object value) {
        value = getNullable(column, value);
        if(column != null && value != null && only) {
            columns1.add(new AlnQueryColumn(" AND " + tables.get(tableNo) + "." + "`" + column + "`", " LIKE '" + AlnUtilsText.toString(value) + "%'"));
        }
        return this;
    }

    public AlnQueryCondition orStartWith(final String column, final Object value) {
        return orStartWith(0, column, value);
    }

    public AlnQueryCondition orStartWith(final int tableNo, final String column, Object value) {
        value = getNullable(column, value);
        if(column != null && value != null && only) {
            columns1.add(new AlnQueryColumn(" OR " + tables.get(tableNo) + "." + "`" + column + "`", " LIKE '" + AlnUtilsText.toString(value) + "%'"));
        }
        return this;
    }

    public AlnQueryCondition andEndWith(final String column, final Object value) {
        return orEndWith(0, column, value);
    }

    public AlnQueryCondition andEndWith(final int tableNo, final String column, Object value) {
        value = getNullable(column, value);
        if(column != null && value != null && only) {
            columns1.add(new AlnQueryColumn(" AND " + tables.get(tableNo) + "." + "`" + column + "`", " LIKE '%" + AlnUtilsText.toString(value) + "'"));
        }
        return this;
    }

    public AlnQueryCondition orEndWith(final String column, final Object value) {
        return orEndWith(0, column, value);
    }

    public AlnQueryCondition orEndWith(final int tableNo, final String column, Object value) {
        value = getNullable(column, value);
        if(column != null && value != null && only) {
            columns1.add(new AlnQueryColumn(" OR " + tables.get(tableNo) + "." + "`" + column + "`", " LIKE '%" + AlnUtilsText.toString(value) + "'"));
        }
        return this;
    }

    public AlnQueryCondition groupBy(final String column) {
        return groupBy(0, column);
    }

    public AlnQueryCondition groupBy(final int tableNo, final String column) {
        if(column != null && only) {
            groupBy = " GROUP BY " + tables.get(tableNo) + "." + "`" + column + "`" + "";
        }
        return this;
    }

    public AlnQueryCondition orderByASC(final String column) {
        return orderByASC(0, column);
    }

    public AlnQueryCondition orderByASC(final int tableNo, final String column) {
        order(tableNo, column, "ASC");
        return this;
    }

    public AlnQueryCondition orderByDESC(final String column) {
        return orderByDESC(0, column);
    }

    public AlnQueryCondition orderByDESC(final int tableNo, final String column) {
        order(tableNo, column, "DESC");
        return this;
    }

    public AlnQueryCondition order(final String column, final String direction) {
        return order(0, column, direction);
    }

    public AlnQueryCondition order(final int tableNo, final String column, String direction) {
        if(!AlnUtilsText.isEmpty(column) && only) {
            direction = AlnUtilsText.notEmpty(direction, "ASC");
            orderBy = " ORDER BY " + tables.get(tableNo) + "." + "`" + column + "`" + " " + direction;
        }
        return this;
    }

    public AlnQueryCondition limit(final int limit) {
        this.limit = limit;
        this.limitOffset = getLimitOffset();
        return this;
    }

    public AlnQueryCondition offset(final int offset) {
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

    public AlnQueryCondition pageMax(final int page, final int max) {
        limit(max);
        offset(page <= 0 ? 0 : (page * max) - max);
        return this;
    }

}
