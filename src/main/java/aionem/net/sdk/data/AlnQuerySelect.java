package aionem.net.sdk.data;

import aionem.net.sdk.auth.AlnAuthData;
import aionem.net.sdk.utils.AlnDBUtils;
import aionem.net.sdk.utils.AlnDataUtils;
import aionem.net.sdk.utils.AlnJsonUtils;
import aionem.net.sdk.utils.AlnParseUtils;
import aionem.net.sdk.utils.AlnTextUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Log4j
public class AlnQuerySelect extends AlnQueryCondition {


    public AlnQuerySelect(final AlnAuthData auth, final String table) {
        super(auth, table);
    }

    public AlnQuerySelect data(final AlnData data) {
        super.data(data);
        return this;
    }


    public AlnQuerySelect all() {
        if(only) {
            columns2.add(new AlnQueryColumn("" + table + "." + "*", ""));
        }
        return this;
    }

    public AlnQuerySelect column(final String column) {
        column(column, "");
        return this;
    }

    public AlnQuerySelect column(final String column, final String alias) {
        if(column != null && only) {
            columns2.add(new AlnQueryColumn("" + table + "." + "`" + column + "`" + (!AlnTextUtils.isEmpty(alias) ? " AS " + "'" + alias + "'" : ""), ""));
        }
        return this;
    }

    public AlnQuerySelect coalesce(final String column1, final String column2) {
        coalesce(0, column1, column2, column1);
        return this;
    }

    public AlnQuerySelect coalesce(final String column1, final String column2, final String alias) {
        coalesce(0, column1, column2, alias);
        return this;
    }

    public AlnQuerySelect coalesce(final int tableNo, final String column1, final String column2) {
        coalesce(tableNo, column1, column2, column1);
        return this;
    }

    public AlnQuerySelect coalesce(final int tableNo, final String column1, final String column2, final String alias) {
        if(column1 != null && column2 != null && only) {
            columns2.add(new AlnQueryColumn("COALESCE(NULLIF(NULLIF(" + tables.get(tableNo) + "." + "`" + column1 + "`" + ", '0'), ''), " + table + "." + "`" + column2 + "`)" + " AS " + "'" + alias + "'", ""));
        }
        return this;
    }

    public AlnQuerySelect count() {
        count("", "", false);
        return this;
    }

    public AlnQuerySelect count(final String column, final String alias) {
        count(0, column, alias, false);
        return this;
    }

    public AlnQuerySelect count(final String column, final String alias, boolean distinct) {
        count(0, column, alias, distinct);
        return this;
    }

    public AlnQuerySelect count(final int tableNo, final String column, final String alias) {
        count(tableNo, column, alias, false);
        return this;
    }

    public AlnQuerySelect count(final int tableNo, String column, final String alias, final boolean distinct) {
        if(only) {
            column = !AlnTextUtils.isEmpty(column) ? (distinct ? "DISTINCT " : "") + tables.get(tableNo) + "." + "`" + column + "`" : "*";
            AlnQueryColumn columnValueCount = new AlnQueryColumn("COUNT(" + column + ")" + (!AlnTextUtils.isEmpty(alias) ? " AS " + "'" + alias + "'" : ""), "");
            if(!columns2.contains(columnValueCount)) {
                columns2.add(columnValueCount);
            }
            pageMax(-1, -1);
        }
        return this;
    }

    public AlnQuerySelect sum(final String column) {
        sum(0, column, "SUM(#)", false);
        return this;
    }

    public AlnQuerySelect sum(final String column, final String alias) {
        sum(0, column, alias, false);
        return this;
    }

    public AlnQuerySelect sum(final String column, final String alias, final boolean distinct) {
        sum(0, column, alias, distinct);
        return this;
    }

    public AlnQuerySelect sum(final int tableNo, final String column, final String alias) {
        sum(tableNo, column, alias, false);
        return this;
    }

    public AlnQuerySelect sum(final int tableNo, String column, final String alias, final boolean distinct) {
        if(column != null && only) {
            column = (distinct ? "DISTINCT " : "") + tables.get(tableNo) + "." + "`" + column + "`";
            AlnQueryColumn columnValueSum = new AlnQueryColumn("SUM(" + column + ")" + (!AlnTextUtils.isEmpty(alias) ? " AS " + "'" + alias + "'" : ""), "");
            if(!columns2.contains(columnValueSum)) {
                columns2.add(columnValueSum);
            }
        }
        return this;
    }

    public AlnQuerySelect join(final String table) {
        super.join(table);
        return this;
    }

    public AlnQuerySelect on(final String column1, final String column2) {
        super.on(column1, column2);
        return this;
    }

    public AlnQuerySelect on(final int tableNo, final String column1, final String column2) {
        super.on(tableNo, column1, column2);
        return this;
    }

    public AlnQuerySelect only(boolean only) {
        super.only(only);
        return this;
    }

    public AlnQuerySelect onlyElse() {
        super.onlyElse();
        return this;
    }

    public AlnQuerySelect forAll() {
        super.forAll();
        return this;
    }

    public AlnQuerySelect then() {
        super.then();
        return this;
    }

    public AlnQuerySelect where(final String column) {
        super.where(column);
        return this;
    }

    public AlnQuerySelect where(final String column, final Object value) {
        super.where(column, value);
        return this;
    }

    public AlnQuerySelect where(final String column, final Object value, final boolean condition) {
        super.where(column, value, condition);
        return this;
    }

    public AlnQuerySelect where(final int tableNo, final String column) {
        super.where(tableNo, column);
        return this;
    }

    public AlnQuerySelect where(final int tableNo, final String column, final Object value) {
        super.where(tableNo, column, value);
        return this;
    }

    public AlnQuerySelect where(final int tableNo, final String column, final Object value, final boolean condition) {
        super.where(tableNo, column, value, condition);
        return this;
    }

    public AlnQuerySelect where(final String column, final String logic, final Object value) {
        super.where(0, column, logic, value);
        return this;
    }

    public AlnQuerySelect where(final String column, final String logic, final Object value, final boolean condition) {
        super.where(0, column, logic, value, condition);
        return this;
    }

    public AlnQuerySelect where(final int tableNo, final String column, final String logic, final Object value) {
        super.where(tableNo, column, logic, value);
        return this;
    }

    public AlnQuerySelect where(final int tableNo, final String column, final String logic, final Object value, final boolean condition) {
        super.where(tableNo, column, logic, value, condition);
        return this;
    }

    public AlnQuerySelect and(final String column) {
        super.and(column);
        return this;
    }

    public AlnQuerySelect andNotEmpty(final String column) {
        super.andNotEmpty(column);
        return this;
    }

    public AlnQuerySelect and(final String column, final Object value) {
        super.and(column, value);
        return this;
    }

    public AlnQuerySelect and(final String column, final Object value, final boolean condition) {
        super.and(0, column, value, condition);
        return this;
    }

    public AlnQuerySelect and(final String column, final String logic, final Object value) {
        super.and(0, column, logic, value);
        return this;
    }

    public AlnQuerySelect and(final int tableNo, final String column, final Object value) {
        super.and(tableNo, column, value);
        return this;
    }

    public AlnQuerySelect and(final int tableNo, final String column, final String logic, final Object value) {
        super.and(tableNo, column, logic, value);
        return this;
    }

    public AlnQuerySelect and(final int tableNo, final String column, final Object value, final boolean condition) {
        super.and(tableNo, column, value, condition);
        return this;
    }

    public AlnQuerySelect or(final String column) {
        super.or(column);
        return this;
    }

    public AlnQuerySelect or(final String column, final Object value) {
        super.or(column, value);
        return this;
    }

    public AlnQuerySelect or(final int tableNo, final String column, final Object value) {
        super.or(tableNo, column, value);
        return this;
    }

    public AlnQuerySelect or(final String column, final List<String> values) {
        super.or(column, values);
        return this;
    }

    public AlnQuerySelect or(final int tableNo, final String column, final List<String> values) {
        super.or(tableNo, column, values);
        return this;
    }

    public AlnQuerySelect or(final int tableNo, final String column, final String logic, final Object value) {
        super.or(tableNo, column, logic, value);
        return this;
    }

    public AlnQuerySelect or(final int tableNo, final String column, final Object value, final boolean condition) {
        super.or(tableNo, column, value, condition);
        return this;
    }

    public AlnQuerySelect like(final String[] columns, final Object value) {
        super.like(columns, value);
        return this;
    }

    public AlnQuerySelect like(final String[] columns, final Object value, final boolean condition) {
        super.like(columns, value, condition);
        return this;
    }

    public AlnQuerySelect andLike(final String column, final Object value) {
        super.andLike(column, value);
        return this;
    }

    public AlnQuerySelect andLike(final String column, final Object value, final boolean condition) {
        super.andLike(column, value, condition);
        return this;
    }

    public AlnQuerySelect andLike(final int tableNo, final String column, final Object value) {
        super.andLike(tableNo, column, value);
        return this;
    }

    public AlnQuerySelect andLike(final int tableNo, final String column, final Object value, final boolean condition) {
        super.andLike(tableNo, column, value, condition);
        return this;
    }

    public AlnQuerySelect orLike(final String column, final Object value) {
        super.orLike(column, value);
        return this;
    }

    public AlnQuerySelect orLike(final String column, final Object value, final boolean condition) {
        super.orLike(column, value, condition);
        return this;
    }

    public AlnQuerySelect orLike(final int tableNo, final String column, final Object value) {
        super.orLike(tableNo, column, value);
        return this;
    }

    public AlnQuerySelect orLike(final int tableNo, final String column, final Object value, final boolean condition) {
        super.orLike(tableNo, column, value, condition);
        return this;
    }

    public AlnQuerySelect andStartWith(final String column, final Object value) {
        super.andStartWith(column, value);
        return this;
    }

    public AlnQuerySelect andStartWith(final int tableNo, final String column, final Object value) {
        super.andStartWith(tableNo, column, value);
        return this;
    }

    public AlnQuerySelect orStartWith(final String column, final Object value) {
        super.orStartWith(column, value);
        return this;
    }

    public AlnQuerySelect orStartWith(final int tableNo, final String column, final Object value) {
        super.orStartWith(tableNo, column, value);
        return this;
    }

    public AlnQuerySelect andEndWith(final String column, final Object value) {
        super.andEndWith(column, value);
        return this;
    }

    public AlnQuerySelect andEndWith(final int tableNo, final String column, final Object value) {
        super.andEndWith(tableNo, column, value);
        return this;
    }

    public AlnQuerySelect orEndWith(final String column, final Object value) {
        super.orEndWith(column, value);
        return this;
    }

    public AlnQuerySelect orEndWith(final int tableNo, final String column, final Object value) {
        super.orEndWith(tableNo, column, value);
        return this;
    }

    public AlnQuerySelect groupBy(final String column) {
        super.groupBy(column);
        return this;
    }

    public AlnQuerySelect groupBy(final int tableNo, final String column) {
        super.groupBy(tableNo, column);
        return this;
    }

    public AlnQuerySelect order(final String column, final String direction) {
        super.order(column, direction);
        return this;
    }

    public AlnQuerySelect order(final int tableNo, final String column, final String direction) {
        super.order(tableNo, column, direction);
        return this;
    }

    public AlnQuerySelect orderByASC(final String column) {
        super.orderByASC(column);
        return this;
    }

    public AlnQuerySelect orderByASC(final int tableNo, final String column) {
        super.orderByASC(tableNo, column);
        return this;
    }

    public AlnQuerySelect orderByDESC(final String column) {
        super.orderByDESC(column);
        return this;
    }

    public AlnQuerySelect orderByDESC(final int tableNo, final String column) {
        super.orderByDESC(tableNo, column);
        return this;
    }

    public AlnQuerySelect limit(final int limit) {
        super.limit(limit);
        return this;
    }

    public AlnQuerySelect offset(final int offset) {
        super.offset(offset);
        return this;
    }

    public AlnQuerySelect pageMax(final int page, final int max) {
        super.pageMax(page, max);
        return this;
    }

    public AlnQuerySelect list(final AlnData data) {

        final int length = data.get(AlnDBUtils.PAR_LENGTH, 10);
        final int max = data.get(AlnDBUtils.PAR_MAX, length);
        final int start = data.get(AlnDBUtils.PAR_START, 0);
        final int page = data.get(AlnDBUtils.PAR_PAGE, length > 0 ? (start / length) + 1 : 1);

        final String orderColumn = data.get("order[0][column]");
        final String orderName = data.getOr("columns[" + orderColumn + "][name]", "orderBy");
        final String[] orderBy = orderName.split("\\.");
        final String orderByColumn = orderBy.length > 1 ? orderBy[1] : orderName;
        final int orderByTableNo = (int) AlnParseUtils.toNumber(orderBy.length > 1 ? orderBy[0] : 0, 0);
        final String orderDir = data.getOr("order[0][dir]", "orderDir");

        this.order(orderByTableNo, orderByColumn, orderDir);
        this.pageMax(page, max);

        return this;
    }

    public String getQuery() {
        query = " SELECT";
        for(int i = 0; i < columns2.size(); i++) {
            final AlnQueryColumn columnValue = columns2.get(i);
            final String column = columnValue.getColumn();
            query += (i > 0 ? ", " : " ") + column;
        }
        query += columns2.isEmpty() ? " " + tables.get(0) + "." + "*" : "";
        query += " FROM " + tables.get(0);

        query += join;

        if(!columns1.isEmpty()) {
            query += " WHERE";
            for(int i = 0; i < columns1.size(); i++) {
                final AlnQueryColumn columnValue = columns1.get(i);
                String column = columnValue.getColumn();
                final String value = columnValue.getValue();
                final String condition = i > 0 ? (column.startsWith(" OR ") ? " OR" : " AND") : "";
                column = column.replaceFirst(" AND ", "").replaceFirst(" OR ", "");
                query += condition + " " + column + value;
            }
        }

        query += groupBy;
        query += orderBy + limitOffset;
        if(!AlnTextUtils.isEmpty(query)) {
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

    public AlnData executeData() {
        return executeData(AlnData.class);
    }

    public <T> T executeData(Class<T> type) {
        try {
            return AlnDataUtils.adaptTo(type, executeJson());
        }catch(Exception e) {
            setException(e);
            return null;
        }
    }

    public JsonObject executeJson() {
        final ArrayList<AlnData> listData = executeListData();
        return !listData.isEmpty() ? listData.get(0).getData() : null;
    }

    public boolean executeExists() {
        return !executeListData().isEmpty();
    }

    public JsonArray executeJsonArray() {
        final JsonArray arrayData = AlnJsonUtils.jsonArray();
        for(final AlnData data : executeListData()) {
            arrayData.add(data.getData());
        }
        return arrayData;
    }

    public <T> ArrayList<T> executeList(Class<T> type) {
        final ArrayList<T> listData = new ArrayList<>();
        for(final AlnData data : executeListData()) {
            try {
                listData.add(AlnDataUtils.adaptTo(type, data.getData()));
            }catch(Exception e) {
                setException(e);
            }
        }
        return listData;
    }

    public ArrayList<AlnData> executeListData() {
        final ArrayList<AlnData> listData = new ArrayList<>();
        try {

            final Statement statement = getConnection(auth).createStatement();
            final ResultSet resultSet = statement.executeQuery(getQuery());

            long index = offset + 1;
            while(resultSet.next()) {

                final AlnData data = new AlnData();

                data.put("index", index);

                final ResultSetMetaData metaData = resultSet.getMetaData();
                final int columns = metaData.getColumnCount();
                for(int columnIndex = 1; columnIndex <= columns; columnIndex++) {

                    final String columnName = metaData.getColumnName(columnIndex);
                    final String columnLabel = metaData.getColumnLabel(columnIndex);
                    final String column = AlnTextUtils.notEmpty(columnLabel, columnName);

                    final int columnType = metaData.getColumnType(columnIndex);

                    switch(columnType) {
                        case Types.INTEGER:
                        case Types.SMALLINT:
                        case Types.TINYINT:
                            final int intValue = resultSet.getInt(columnIndex);
                            data.put(column, intValue);
                            break;

                        case Types.BIGINT:
                            final long longValue = resultSet.getLong(columnIndex);
                            data.put(column, longValue);
                            break;

                        case Types.DOUBLE:
                        case Types.FLOAT:
                            final double doubleValue = resultSet.getDouble(columnIndex);
                            data.put(column, doubleValue);
                            break;

                        case Types.BOOLEAN:
                        case Types.BIT:
                            final boolean booleanValue = resultSet.getBoolean(columnIndex);
                            data.put(column, booleanValue);
                            break;

                        default:
                            final String value = resultSet.getString(columnIndex);
                            data.put(column, value);
                            break;
                    }

                }

                listData.add(data);

                index++;
            }

            resultSet.close();
            statement.close();

        }catch(Exception e) {
            setException(e);
        }
        return listData;
    }

    public long executeCount() {
        return executeCount("COUNT(*)", true);
    }

    public long executeCount(final String columnLabel) {
        return executeCount(columnLabel, false);
    }

    public long executeCount(String columnLabel, boolean addCount) {
        long count = 0;
        if(addCount) {
            count();
        }
        try {

            final Statement statement = getConnection(auth).createStatement();
            final ResultSet result = statement.executeQuery(getQuery());

            while(result.next()) {
                count = result.getLong(columnLabel);
            }

            result.close();
            statement.close();

        }catch(Exception e) {
            setException(e);
        }
        return count;
    }

    public long executeSum() {
        return executeCount("SUM(#)", false);
    }

    public long executeSum(final String columnLabel) {
        return executeSum(columnLabel, true);
    }

    public long executeSum(String columnLabel, boolean addSum) {
        long sum = 0;
        if(addSum) {
            String alias = "SUM(" + columnLabel + ")";
            sum(columnLabel, alias);
            columnLabel = alias;
        }
        try {

            final Statement statement = getConnection(auth).createStatement();
            final ResultSet result = statement.executeQuery(getQuery());

            while(result.next()) {
                sum = result.getLong(columnLabel);
            }

            result.close();
            statement.close();

        }catch(Exception e) {
            setException(e);
        }
        return sum;
    }

}
