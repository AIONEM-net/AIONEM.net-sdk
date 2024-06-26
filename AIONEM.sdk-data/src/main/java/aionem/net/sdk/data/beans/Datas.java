package aionem.net.sdk.data.beans;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.utils.UtilsJson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;


@Log4j2
public @Getter class Datas implements Iterable<Data> {

    private final ArrayList<Data> listDatas = new ArrayList<>();

    public Datas() {

    }

    public Datas(final JsonArray jsonArray) {
        for(final JsonElement jsonElement : jsonArray) {
            listDatas.add(new Data(jsonElement));
        }
    }

    @Override
    public @NotNull Iterator<Data> iterator() {
        return listDatas.iterator();
    }

    @Override
    public void forEach(Consumer<? super Data> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<Data> spliterator() {
        return Iterable.super.spliterator();
    }

    public void add(final Data data) {
        listDatas.add(data);
    }

    public void add(int index, final Data data) {
        listDatas.add(index, data);
    }

    public void addAll(final Datas datas) {
        listDatas.addAll(datas.listDatas);
    }

    public Data get(int index) {
        return listDatas.get(index);
    }

    public Data remove(int index) {
        return listDatas.remove(index);
    }

    public boolean remove(Data data) {
        return listDatas.remove(data);
    }

    public int size() {
        return listDatas.size();
    }

    public boolean isEmpty() {
        return listDatas.isEmpty();
    }

    public boolean contains(Data data) {
        return listDatas.contains(data);
    }

    public boolean contains(final String key, final Object value) {
        return contains(key, value, false);
    }

    public boolean contains(final String key, final Object value, final boolean ignoreCase) {
        return filter(key, value, ignoreCase).size() > 0;
    }

    public Datas filter(final String key) {
        final Datas datas = new Datas();
        for(final Data data : listDatas) {
            if(data.has(key)) {
                datas.add(data);
            }
        }
        return datas;
    }

    public Datas filter(final String key, final Object value) {
        return filter(key, value, false);
    }

    public Datas filter(final String key, final Object value, final boolean ignoreCase) {
        final Datas datas = new Datas();
        for(final Data data : listDatas) {
            if(ignoreCase ? data.equalsIgnoreCase(key, value) : data.equals(key, value)) {
                datas.add(data);
            }
        }
        return datas;
    }

    public int remove(final String key) {
        int n = 0;
        for(final Data data : filter(key)) {
            if(remove(data)) {
                n++;
            }
        }
        return n;
    }

    public int remove(final String key, final Object value) {
        return remove(key, value, false);
    }

    public int remove(final String key, final Object value, final boolean ignoreCase) {
        int n = 0;
        for(final Data data : filter(key, value, ignoreCase)) {
            if(remove(data)) {
                n++;
            }
        }
        return n;
    }

    public void sort(final Comparator<? super Data> comparator) {
        listDatas.sort(comparator);
    }

    public void sortByKeyASC(final String key) {
        sortByKeyASC(key, null);
    }

    public void sortByKeyASC(final String key, final Object value) {
        if(UtilsText.isEmpty(key)) return;

        final Comparator<Data> comparator = new Comparator<Data>() {
            @Override
            public int compare(final Data data1, final Data data2) {
                final boolean isData1 = data1.equalsIgnoreCase(key, value);
                final boolean isData2 = data2.equalsIgnoreCase(key, value);
                if(isData1 && isData2) return 0;
                if(isData1) return -1;
                if(isData2) return 1;
                return UtilsText.compareTo(data1.get(key), data2.get(key));
            }
        };

        sort(comparator);
    }

    public void sortByKeyDESC(final String key) {
        sortByKeyDESC(key, null);
    }

    public void sortByKeyDESC(final String key, final Object value) {
        if(UtilsText.isEmpty(key)) return;

        final Comparator<Data> comparator = new Comparator<Data>() {
            @Override
            public int compare(final Data data1, final Data data2) {
                final boolean isData1 = data1.equalsIgnoreCase(key, value);
                final boolean isData2 = data2.equalsIgnoreCase(key, value);
                if(isData1 && isData2) return 0;
                if(isData1) return 1;
                if(isData2) return -1;
                return UtilsText.compareTo(data2.get(key), data1.get(key));
            }
        };

        sort(comparator);
    }

    public JsonArray toJson() {
        final JsonArray jsonArray = UtilsJson.jsonArray();
        for(final Data data : listDatas) {
            jsonArray.add(data.toJson());
        }
        return jsonArray;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    @Override
    public boolean equals(final Object object) {
        if(this == object) return true;
        if(object == null || getClass() != object.getClass()) return false;
        final Datas datas = (Datas) object;
        return Objects.equals(listDatas, datas.listDatas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listDatas);
    }

}
