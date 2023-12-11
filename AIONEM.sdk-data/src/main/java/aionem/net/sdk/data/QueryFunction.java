package aionem.net.sdk.data;


public class QueryFunction {

    private final String value;

    public QueryFunction(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
