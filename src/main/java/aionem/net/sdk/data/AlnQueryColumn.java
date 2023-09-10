package aionem.net.sdk.data;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.util.Objects;


@Log4j
@Getter
public class AlnQueryColumn {

    private final String column;
    private final String value;

    protected AlnQueryColumn(final String column, final String value) {
        this.column = column;
        this.value = value;
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        final AlnQueryColumn that = (AlnQueryColumn) o;
        return Objects.equals(column, that.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(column);
    }

}
