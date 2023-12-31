package aionem.net.sdk.data;

import aionem.net.sdk.data.utils.UtilsDB;
import aionem.net.sdk.core.utils.UtilsParse;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.utils.UtilsData;
import aionem.net.sdk.data.utils.UtilsJson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Log4j2
public class QuerySelect extends QueryCondition {

    private boolean isDistinct = false;

    public QuerySelect(final String table) {
        super(table);
    }
    public QuerySelect(final DataAuth auth, final String table) {
        super(auth, table);
    }


    public QuerySelect params(final Data data) {
        super.params(data);
        return this;
    }

    public QuerySelect all() {
        if(only) {
            select(new QueryColumn("" + table + "." + "*"));
        }
        return this;
    }

    public QuerySelect distinct() {
        this.isDistinct = true;
        return this;
    }
    public QuerySelect distinct(final boolean isDistinct) {
        this.isDistinct = isDistinct;
        return this;
    }
    
    protected QuerySelect select(final QueryColumn queryColumn) {
        if(queryColumn != null && only) {
            columns2.add(queryColumn);
        }
        return this;
    }
    
    public QuerySelect column(final String column) {
        column(column, "");
        return this;
    }
    public QuerySelect column(final String column, final String alias) {
        if(column != null && only) {
            select(new QueryColumn("" + table + "." + "`" + column + "`" + (!UtilsText.isEmpty(alias) ? " AS " + "'" + alias + "'" : "")));
        }
        return this;
    }

    public QuerySelect coalesce(final String column1, final String column2) {
        coalesce(0, column1, column2, column1);
        return this;
    }
    public QuerySelect coalesce(final String column1, final String column2, final String alias) {
        coalesce(0, column1, column2, alias);
        return this;
    }
    public QuerySelect coalesce(final int tableNo, final String column1, final String column2) {
        coalesce(tableNo, column1, column2, column1);
        return this;
    }
    public QuerySelect coalesce(final int tableNo, final String column1, final String column2, final String alias) {
        if(column1 != null && column2 != null && only) {
            select(new QueryColumn("COALESCE(NULLIF(NULLIF(" + getTable(tableNo) + "." + "`" + column1 + "`" + ", '0'), ''), " + table + "." + "`" + column2 + "`)" + " AS " + "'" + alias + "'"));
        }
        return this;
    }

    public QuerySelect max(final String column) {
        max(0, column, "");
        return this;
    }
    public QuerySelect max(final String column, final String alias) {
        max(0, column, alias);
        return this;
    }
    public QuerySelect max(final int tableNo, String column, final String alias) {
        if(only) {
            column = getTableColumn(tableNo, column);
            QueryColumn columnValueMax = new QueryColumn("MAX(" + column + ")" + (!UtilsText.isEmpty(alias) ? " AS " + "'" + alias + "'" : "'" + alias + "'"));
            if(!columns2.contains(columnValueMax)) {
                select(columnValueMax);
            }
        }
        return this;
    }
    
    public QuerySelect count() {
        count("", "", false);
        return this;
    }
    public QuerySelect count(final String column, final String alias) {
        count(0, column, alias, false);
        return this;
    }
    public QuerySelect count(final String column, final String alias, boolean distinct) {
        count(0, column, alias, distinct);
        return this;
    }
    public QuerySelect count(final int tableNo, final String column, final String alias) {
        count(tableNo, column, alias, false);
        return this;
    }
    public QuerySelect count(final int tableNo, String column, final String alias, final boolean distinct) {
        if(only) {
            column = !UtilsText.isEmpty(column) ? (distinct ? "DISTINCT " : "") + getTableColumn(tableNo, column) : "*";
            QueryColumn columnValueCount = new QueryColumn("COUNT(" + column + ")" + (!UtilsText.isEmpty(alias) ? " AS " + "'" + alias + "'" : ""));
            if(!columns2.contains(columnValueCount)) {
                select(columnValueCount);
            }
            pageMax(-1, -1);
        }
        return this;
    }

    public QuerySelect sum(final String column) {
        sum(0, column, "SUM(#)", false);
        return this;
    }
    public QuerySelect sum(final String column, final String alias) {
        sum(0, column, alias, false);
        return this;
    }
    public QuerySelect sum(final String column, final String alias, final boolean distinct) {
        sum(0, column, alias, distinct);
        return this;
    }
    public QuerySelect sum(final int tableNo, final String column, final String alias) {
        sum(tableNo, column, alias, false);
        return this;
    }
    public QuerySelect sum(final int tableNo, String column, final String alias, final boolean distinct) {
        if(column != null && only) {
            column = (distinct ? "DISTINCT " : "") + getTableColumn(tableNo, column);
            QueryColumn columnValueSum = new QueryColumn("SUM(" + column + ")" + (!UtilsText.isEmpty(alias) ? " AS " + "'" + alias + "'" : ""));
            if(!columns2.contains(columnValueSum)) {
                select(columnValueSum);
            }
        }
        return this;
    }

    public QuerySelect join(final String table) {
        super.join(table);
        return this;
    }
    public QuerySelect on(final String column1, final String column2) {
        super.on(column1, column2);
        return this;
    }
    public QuerySelect on(final int tableNo, final String column1, final String column2) {
        super.on(tableNo, column1, column2);
        return this;
    }

    public QuerySelect only(boolean only) {
        super.only(only);
        return this;
    }
    public QuerySelect onlyElse() {
        super.onlyElse();
        return this;
    }
    public QuerySelect forAll() {
        super.forAll();
        return this;
    }
    public QuerySelect then() {
        super.then();
        return this;
    }

    public QuerySelect where(final String column) {
        super.where(column);
        return this;
    }
    public QuerySelect where(final String column, final Object value) {
        super.where(column, value);
        return this;
    }
    public QuerySelect where(final String column, final Object value, final boolean condition) {
        super.where(column, value, condition);
        return this;
    }

    public QuerySelect where(final int tableNo, final String column) {
        super.where(tableNo, column);
        return this;
    }
    public QuerySelect where(final int tableNo, final String column, final Object value) {
        super.where(tableNo, column, value);
        return this;
    }
    public QuerySelect where(final int tableNo, final String column, final Object value, final boolean condition) {
        super.where(tableNo, column, value, condition);
        return this;
    }
    public QuerySelect where(final String column, final String logic, final Object value) {
        super.where(0, column, logic, value);
        return this;
    }
    public QuerySelect where(final String column, final String logic, final Object value, final boolean condition) {
        super.where(0, column, logic, value, condition);
        return this;
    }
    public QuerySelect where(final int tableNo, final String column, final String logic, final Object value) {
        super.where(tableNo, column, logic, value);
        return this;
    }
    public QuerySelect where(final int tableNo, final String column, final String logic, final Object value, final boolean condition) {
        super.where(tableNo, column, logic, value, condition);
        return this;
    }

    public QuerySelect and(final String column) {
        super.and(column);
        return this;
    }
    public QuerySelect andNotEmpty(final String column) {
        super.andNotEmpty(column);
        return this;
    }
    public QuerySelect and(final String column, final Object value) {
        super.and(column, value);
        return this;
    }
    public QuerySelect and(final String column, final Object value, final boolean condition) {
        super.and(0, column, value, condition);
        return this;
    }
    public QuerySelect and(final String column, final String logic, final Object value) {
        super.and(0, column, logic, value);
        return this;
    }
    public QuerySelect and(final int tableNo, final String column, final Object value) {
        super.and(tableNo, column, value);
        return this;
    }
    public QuerySelect and(final int tableNo, final String column, final String logic, final Object value) {
        super.and(tableNo, column, logic, value);
        return this;
    }
    public QuerySelect and(final int tableNo, final String column, final Object value, final boolean condition) {
        super.and(tableNo, column, value, condition);
        return this;
    }

    public QuerySelect or(final String column) {
        super.or(column);
        return this;
    }
    public QuerySelect or(final String column, final Object value) {
        super.or(column, value);
        return this;
    }
    public QuerySelect or(final int tableNo, final String column, final Object value) {
        super.or(tableNo, column, value);
        return this;
    }
    public QuerySelect or(final String column, final List<String> values) {
        super.or(column, values);
        return this;
    }
    public QuerySelect or(final int tableNo, final String column, final List<String> values) {
        super.or(tableNo, column, values);
        return this;
    }
    public QuerySelect or(final int tableNo, final String column, final String logic, final Object value) {
        super.or(tableNo, column, logic, value);
        return this;
    }
    public QuerySelect or(final int tableNo, final String column, final Object value, final boolean condition) {
        super.or(tableNo, column, value, condition);
        return this;
    }

    public QuerySelect like(final String[] columns, final Object value) {
        super.like(columns, value);
        return this;
    }
    public QuerySelect like(final String[] columns, final Object value, final boolean condition) {
        super.like(columns, value, condition);
        return this;
    }

    public QuerySelect andLike(final String column, final Object value) {
        super.andLike(column, value);
        return this;
    }
    public QuerySelect andLike(final String column, final Object value, final boolean condition) {
        super.andLike(column, value, condition);
        return this;
    }
    public QuerySelect andLike(final int tableNo, final String column, final Object value) {
        super.andLike(tableNo, column, value);
        return this;
    }
    public QuerySelect andLike(final int tableNo, final String column, final Object value, final boolean condition) {
        super.andLike(tableNo, column, value, condition);
        return this;
    }

    public QuerySelect orLike(final String column, final Object value) {
        super.orLike(column, value);
        return this;
    }
    public QuerySelect orLike(final String column, final Object value, final boolean condition) {
        super.orLike(column, value, condition);
        return this;
    }
    public QuerySelect orLike(final int tableNo, final String column, final Object value) {
        super.orLike(tableNo, column, value);
        return this;
    }
    public QuerySelect orLike(final int tableNo, final String column, final Object value, final boolean condition) {
        super.orLike(tableNo, column, value, condition);
        return this;
    }

    public QuerySelect andGreater(final String column, final Object value) {
        super.andGreater(column, value);
        return this;
    }
    public QuerySelect andGreater(final String column, final Object value, final boolean condition) {
        super.andGreater(column, value, condition);
        return this;
    }
    public QuerySelect andGreater(final int tableNo, final String column, final Object value) {
        super.andGreater(tableNo, column, value);
        return this;
    }
    public QuerySelect andGreater(final int tableNo, final String column, final Object value, final boolean condition) {
        super.andGreater(tableNo, column, value, condition);
        return this;
    }

    public QuerySelect orGreater(final String column, final Object value) {
        super.orGreater(column, value);
        return this;
    }
    public QuerySelect orGreater(final String column, final Object value, final boolean condition) {
        super.orGreater(column, value, condition);
        return this;
    }
    public QuerySelect orGreater(final int tableNo, final String column, final Object value) {
        super.orGreater(tableNo, column, value);
        return this;
    }
    public QuerySelect orGreater(final int tableNo, final String column, final Object value, final boolean condition) {
        super.orGreater(tableNo, column, value, condition);
        return this;
    }

    public QuerySelect andLess(final String column, final Object value) {
        super.andLess(column, value);
        return this;
    }
    public QuerySelect andLess(final String column, final Object value, final boolean condition) {
        super.andLess(column, value, condition);
        return this;
    }
    public QuerySelect andLess(final int tableNo, final String column, final Object value) {
        super.andLess(tableNo, column, value);
        return this;
    }
    public QuerySelect andLess(final int tableNo, final String column, final Object value, final boolean condition) {
        super.andLess(tableNo, column, value, condition);
        return this;
    }

    public QuerySelect orLess(final String column, final Object value) {
        super.orLess(column, value);
        return this;
    }
    public QuerySelect orLess(final String column, final Object value, final boolean condition) {
        super.orLess(column, value, condition);
        return this;
    }
    public QuerySelect orLess(final int tableNo, final String column, final Object value) {
        super.orLess(tableNo, column, value);
        return this;
    }
    public QuerySelect orLess(final int tableNo, final String column, final Object value, final boolean condition) {
        super.orLess(tableNo, column, value, condition);
        return this;
    }

    public QuerySelect andStartWith(final String column, final Object value) {
        super.andStartWith(column, value);
        return this;
    }
    public QuerySelect andStartWith(final int tableNo, final String column, final Object value) {
        super.andStartWith(tableNo, column, value);
        return this;
    }
    public QuerySelect orStartWith(final String column, final Object value) {
        super.orStartWith(column, value);
        return this;
    }
    public QuerySelect orStartWith(final int tableNo, final String column, final Object value) {
        super.orStartWith(tableNo, column, value);
        return this;
    }

    public QuerySelect andEndWith(final String column, final Object value) {
        super.andEndWith(column, value);
        return this;
    }
    public QuerySelect andEndWith(final int tableNo, final String column, final Object value) {
        super.andEndWith(tableNo, column, value);
        return this;
    }

    public QuerySelect orEndWith(final String column, final Object value) {
        super.orEndWith(column, value);
        return this;
    }
    public QuerySelect orEndWith(final int tableNo, final String column, final Object value) {
        super.orEndWith(tableNo, column, value);
        return this;
    }

    public QuerySelect groupBy(final String... columns) {
        super.groupBy(columns);
        return this;
    }
    public QuerySelect groupBy(final int tableNo, final String... column) {
        super.groupBy(tableNo, column);
        return this;
    }

    public QuerySelect order(final String column, final String direction) {
        super.order(column, direction);
        return this;
    }
    public QuerySelect order(final int tableNo, final String column, final String direction) {
        super.order(tableNo, column, direction);
        return this;
    }

    public QuerySelect orderByASC(final String column) {
        super.orderByASC(column);
        return this;
    }
    public QuerySelect orderByASC(final int tableNo, final String column) {
        super.orderByASC(tableNo, column);
        return this;
    }
    public QuerySelect orderByDESC(final String column) {
        super.orderByDESC(column);
        return this;
    }
    public QuerySelect orderByDESC(final int tableNo, final String column) {
        super.orderByDESC(tableNo, column);
        return this;
    }

    public QuerySelect limit(final int limit) {
        super.limit(limit);
        return this;
    }
    public QuerySelect offset(final int offset) {
        super.offset(offset);
        return this;
    }
    public QuerySelect pageMax(final int page, final int max) {
        super.pageMax(page, max);
        return this;
    }

    public QuerySelect list(final Data data) {

        final int start = data.get(UtilsDB.PAR_START, 0);
        final int end = data.get(UtilsDB.PAR_END, 0);
        final int length = data.get(UtilsDB.PAR_LENGTH, 10);
        final int max = (end > 0 && end > start) ? end - start : data.get(UtilsDB.PAR_MAX, length);
        final int page = data.get(UtilsDB.PAR_PAGE, max > 0 ? Math.round((float) start / max) + 1 : 1);

        final String orderColumn = data.get("order[0][column]");
        final String orderName = data.get("columns[" + orderColumn + "][name]", "orderBy");
        final String[] orderBy = orderName.split("\\.");
        final String orderByColumn = orderBy.length > 1 ? orderBy[1] : orderName;
        final int orderByTableNo = (int) UtilsParse.toNumber(orderBy.length > 1 ? orderBy[0] : 0, 0);
        final String orderDir = data.get("order[0][dir]", "orderDir");

        this.order(orderByTableNo, orderByColumn, orderDir);
        this.pageMax(page, max);

        return this;
    }

    public String getQuery() {
        String query = " SELECT";
        if(isDistinct) {
            query += " DISTINCT";
        }
        for(int i = 0; i < columns2.size(); i++) {
            final QueryColumn columnValue = columns2.get(i);
            final String column = columnValue.getColumn();
            query += (i > 0 ? ", " : " ") + column;
        }
        query += columns2.isEmpty() ? " " + tables.get(0) + "." + "*" : "";
        query += " FROM " + tables.get(0);

        query += join;

        if(!columns1.isEmpty()) {
            query += " WHERE";
            for(int i = 0; i < columns1.size(); i++) {
                final QueryColumn columnValue = columns1.get(i);
                String column = columnValue.getColumn();
                final String value = columnValue.getValue();
                final String condition = i > 0 ? (column.startsWith(" OR ") ? " OR" : " AND") : "";
                column = column.replaceFirst(" AND ", "").replaceFirst(" OR ", "");
                query += condition + " " + column + value;
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

    public Data executeData() throws SQLException {
        return executeData(Data.class);
    }

    public <T> T executeData(Class<T> type) throws SQLException {
        try {
            return UtilsData.adaptTo(type, executeJson());
        } catch (IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
            setException(e);
            return null;
        }
    }

    public JsonObject executeJson() throws SQLException {
        final ArrayList<Data> listData = executeListData();
        return !listData.isEmpty() ? listData.get(0).toJson() : null;
    }

    public boolean executeExists() throws SQLException {
        return !executeListData().isEmpty();
    }

    public JsonArray executeJsonArray() throws SQLException {
        final JsonArray arrayData = UtilsJson.jsonArray();
        for(final Data data : executeListData()) {
            arrayData.add(data.toJson());
        }
        return arrayData;
    }

    public Datas executeDatas() throws SQLException {
        final Datas datas = new Datas();
        for(final Data data : executeListData()) {
            datas.add(data);
        }
        return datas;
    }

    public <T> ArrayList<T> executeList(final Class<T> type) throws SQLException {
        final ArrayList<T> listData = new ArrayList<>();
        for(final Data data : executeListData()) {
            try {
                listData.add(UtilsData.adaptTo(type, data.toJson()));
            }catch(Exception e) {
                setException(e);
            }
        }
        return listData;
    }

    public ArrayList<Data> executeListData() throws SQLException {
        final ArrayList<Data> listData = new ArrayList<>();
        try {

            final Statement statement = getConnection().createStatement();
            final ResultSet resultSet = statement.executeQuery(getQuery());

            long index = offset;
            while(resultSet.next()) {

                final Data data = new Data();

                data.put("index", index);

                final ResultSetMetaData metaData = resultSet.getMetaData();
                final int columns = metaData.getColumnCount();
                for(int columnIndex = 1; columnIndex <= columns; columnIndex++) {

                    final String columnName = metaData.getColumnName(columnIndex);
                    final String columnLabel = metaData.getColumnLabel(columnIndex);
                    final String column = UtilsText.notEmpty(columnLabel, columnName);

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
            throw e;
        }
        return listData;
    }

    public long executeCount() throws SQLException {
        return executeCount("COUNT(*)", true);
    }

    public long executeCount(final String columnLabel) throws SQLException {
        return executeCount(columnLabel, false);
    }

    public long executeCount(String columnLabel, boolean addCount) throws SQLException {
        long count = 0;
        if(addCount) {
            count();
        }
        try {

            final Statement statement = getConnection().createStatement();
            final ResultSet result = statement.executeQuery(getQuery());

            while(result.next()) {
                count = result.getLong(columnLabel);
            }

            result.close();
            statement.close();

        }catch(SQLException e) {
            setException(e);
            throw e;
        }
        return count;
    }

    @SneakyThrows
    public long executeSum() {
        return executeCount("SUM(#)", false);
    }

    public long executeSum(final String columnLabel) throws SQLException {
        return executeSum(columnLabel, true);
    }

    public long executeSum(String columnLabel, boolean addSum) throws SQLException {
        long sum = 0;
        if(addSum) {
            String alias = "SUM(" + columnLabel + ")";
            sum(columnLabel, alias);
            columnLabel = alias;
        }
        try {

            final Statement statement = getConnection().createStatement();
            final ResultSet result = statement.executeQuery(getQuery());

            while(result.next()) {
                sum = result.getLong(columnLabel);
            }

            result.close();
            statement.close();

        }catch(SQLException e) {
            setException(e);
            throw e;
        }
        return sum;
    }

}
