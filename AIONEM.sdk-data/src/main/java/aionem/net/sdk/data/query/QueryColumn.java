package aionem.net.sdk.data.query;

import aionem.net.sdk.core.utils.UtilsText;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.Objects;


@Log4j2
@Getter
public class QueryColumn {

    private final String column;
    private final String value;

    protected QueryColumn(final String column) {
        this.column = column;
        this.value = "";
    }

    protected QueryColumn(final String column, final Object value) {
        this.column = column;
        this.value = getValue(value);
    }

    protected QueryColumn(final String column, final String logic, final Object value) {
        this.column = column;
        this.value = " "+ logic +" " + getValue(value);
    }

    protected QueryColumn(final String table, final String column, final String logic, final Object value) {
        this.column = table + "." + "`" + column + "`";
        this.value = " "+ logic +" " + getValue(value);
    }

    protected QueryColumn(final String operator, final String table, final String column, final String logic, final Object value) {
        this.column = " "+ operator +" " + table + "." + "`" + column + "`";
        this.value = " "+ logic +" " + getValue(value);
    }

    protected QueryColumn(final String column, final String logic, final String column1, final String value) {
        this.column = column;
        this.value = " "+ logic +" " + "`"+ column1 +"`" +" "+ UtilsText.toString(value);
    }

    public String getValue(final Object value) {
        if(value instanceof QueryFunction) {
            return value.toString();
        }else {
            return "'"+ UtilsText.toString(value) +"'";
        }
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        final QueryColumn that = (QueryColumn) o;
        return Objects.equals(column, that.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(column);
    }

}
