package aionem.net.sdk.data;

import aionem.net.sdk.utils.AlnJsonUtils;
import aionem.net.sdk.utils.AlnTextUtils;
import com.google.gson.JsonArray;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;


public class AlnDatas implements Iterable<AlnData> {

    private final ArrayList<AlnData> listDatas = new ArrayList<>();

    @Override
    public @NotNull Iterator<AlnData> iterator() {
        return listDatas.iterator();
    }

    @Override
    public void forEach(Consumer<? super AlnData> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<AlnData> spliterator() {
        return Iterable.super.spliterator();
    }

    public void add(final AlnData data) {
        listDatas.add(data);
    }

    public void addAll(final AlnDatas datas) {
        listDatas.addAll(datas.listDatas);
    }

    public int size() {
        return listDatas.size();
    }

    public boolean isEmpty() {
        return listDatas.isEmpty();
    }

    public void sort(final Comparator<? super AlnData> comparator) {
        listDatas.sort(comparator);
    }

    public void sortByKeyASC(final String key) {
        sortByKeyASC(key, null);
    }
    public void sortByKeyASC(final String key, final String value) {
        if(AlnTextUtils.isEmpty(key)) return;

        final Comparator<AlnData> comparator = new Comparator<AlnData>() {
            @Override
            public int compare(final AlnData data1, final AlnData data2) {
                final boolean isData1 = data1.equalsIgnoreCase(value, key);
                final boolean isData2 = data2.equalsIgnoreCase(value, key);
                if(isData1 && isData2) return 0;
                if(isData1) return -1;
                if(isData2) return 1;
                return AlnTextUtils.compareTo(data1.get(key), data2.get(key));
            }
        };

        sort(comparator);
    }

    public void sortByKeyDESC(final String key) {
        sortByKeyDESC(key, null);
    }
    public void sortByKeyDESC(final String key, final String value) {
        if(AlnTextUtils.isEmpty(key)) return;

        final Comparator<AlnData> comparator = new Comparator<AlnData>() {
            @Override
            public int compare(final AlnData data1, final AlnData data2) {
                final boolean isData1 = data1.equalsIgnoreCase(value, key);
                final boolean isData2 = data2.equalsIgnoreCase(value, key);
                if(isData1 && isData2) return 0;
                if(isData1) return 1;
                if(isData2) return -1;
                return AlnTextUtils.compareTo(data2.get(key), data1.get(key));
            }
        };

        sort(comparator);
    }

    public JsonArray getDatas() {
        final JsonArray jsonArray = AlnJsonUtils.jsonArray();
        for(final AlnData data : listDatas) {
            jsonArray.add(data.getData());
        }
        return jsonArray;
    }

    @Override
    public String toString() {
        return getDatas().toString();
    }

    @Override
    public boolean equals(final Object object) {
        if(this == object) return true;
        if(object == null || getClass() != object.getClass()) return false;
        final AlnDatas alnData = (AlnDatas) object;
        return Objects.equals(listDatas, alnData.listDatas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listDatas);
    }

}
