package aionem.net.sdk.data.query;


public class QueryFunction {

    private final String value;

    public QueryFunction(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static final QueryFunction NOW = new QueryFunction("NOW()");

}
