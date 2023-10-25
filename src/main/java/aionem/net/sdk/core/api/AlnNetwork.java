package aionem.net.sdk.core.api;

import aionem.net.sdk.core.data.AlnData;
import aionem.net.sdk.core.utils.AlnUtilsNetwork;
import aionem.net.sdk.core.utils.AlnUtilsText;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


@Log4j2
public @Data class AlnNetwork {

    protected String link;
    protected AlnData dataParams;
    protected AlnData dataHeaders;

    public AlnNetwork(final String link) {
        setLink(link);
    }

    public AlnNetwork setLink(String link) {
        this.link = link;
        return this;
    }

    public AlnNetwork setDataParams(final AlnData dataParams) {
        this.dataParams = dataParams != null ? dataParams : new AlnData();
        return this;
    }

    public AlnNetwork setDataHeaders(final AlnData dataHeaders) {
        this.dataHeaders = dataHeaders != null ? dataHeaders : new AlnData();
        return this;
    }

    public static AlnDaoRes doGet(final String link) throws IOException {
        return doGet(link, new AlnData());
    }
    public static AlnDaoRes doGet(final String link, final AlnData dataParams) throws IOException {
        return doGet(link, dataParams, new AlnData());
    }
    public static AlnDaoRes doGet(final String link, final AlnData dataParams, final AlnData dataHeaders) throws IOException {
        return new Get(link)
                .setDataParams(dataParams)
                .setDataHeaders(dataHeaders)
                .get();
    }

    public static AlnDaoRes doPost(final String link) throws IOException {
        return doPost(link, new AlnData());
    }
    public static AlnDaoRes doPost(final String link, final AlnData dataParams) throws IOException {
        return doPost(link, dataParams, new AlnData());
    }
    public static AlnDaoRes doPost(final String link, final AlnData dataBody, final AlnData dataHeaders) {
        return doPost(link, new AlnData(), dataBody, dataHeaders);
    }
    public static AlnDaoRes doPost(final String link, final AlnData dataParams, final AlnData dataBody, final AlnData dataHeaders) {
        return new Post(link)
                .setDataParams(dataParams)
                .setDataBody(dataBody)
                .setDataHeaders(dataHeaders)
                .post();
    }

    public static class Get extends AlnNetwork {

        public Get(String link) {
            super(link);
        }

        public Get setLink(final String link) {
            super.setLink(link);
            return this;
        }

        public Get setDataParams(final AlnData dataParams) {
            super.setDataParams(dataParams);
            return this;
        }

        public Get setDataHeaders(final AlnData dataHeaders) {
            super.setDataHeaders(dataHeaders);
            return this;
        }

        public AlnDaoRes get() {

            final AlnDaoRes resGet = new AlnDaoRes();

            try {

                String linkUrl = link;

                if(dataParams != null) {
                    linkUrl = AlnUtilsNetwork.addParameter(link, dataParams.getValuesString());
                }

                final URL url = new URL(linkUrl);
                final HttpURLConnection httpURLConnection;
                if("https".equalsIgnoreCase(url.getProtocol())) {
                    httpURLConnection = (HttpsURLConnection) url.openConnection();
                }else {
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                }

                httpURLConnection.setRequestMethod("GET");

                if(dataHeaders != null) {
                    for(final String key : dataHeaders.keySet()) {
                        final String value = dataHeaders.get(key);
                        httpURLConnection.setRequestProperty(key, value);
                    }
                }

                httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

                resGet.setSuccess(true);
                resGet.setStatus(httpURLConnection.getResponseCode());
                resGet.setResponse(AlnUtilsText.toString(httpURLConnection));

                httpURLConnection.disconnect();

            }catch(Exception e) {
                resGet.setException(e);
            }

            return resGet;
        }

    }

    public static class Post extends AlnNetwork {

        private AlnData dataBody;

        public Post(String link) {
            super(link);
        }

        public Post setLink(final String link) {
            super.setLink(link);
            return this;
        }

        public Post setDataParams(final AlnData dataParams) {
            super.setDataParams(dataParams);
            return this;
        }

        public Post setDataBody(final AlnData dataBody) {
            this.dataBody = dataBody;
            return this;
        }

        public Post setDataHeaders(final AlnData dataHeaders) {
            super.setDataHeaders(dataHeaders);
            return this;
        }

        public AlnDaoRes post() {

            final AlnDaoRes resPost = new AlnDaoRes();

            try {

                String linkUrl = link;

                if(dataParams != null) {
                    linkUrl = AlnUtilsNetwork.addParameter(link, dataParams.getValuesString());
                }

                final URL url = new URL(linkUrl);
                final HttpURLConnection httpURLConnection;
                if("https".equalsIgnoreCase(url.getProtocol())) {
                    httpURLConnection = (HttpsURLConnection) url.openConnection();
                }else {
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                }
                httpURLConnection.setRequestMethod("POST");

                if(dataHeaders != null) {
                    for(final String key : dataHeaders.keySet()) {
                        final String value = dataHeaders.get(key);
                        httpURLConnection.setRequestProperty(key, value);
                    }
                }

                httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

                if(dataBody != null) {
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    final OutputStream outputStream = httpURLConnection.getOutputStream();
                    final byte[] input;
                    if(dataHeaders != null && dataHeaders.equalsIgnoreCase("application/json", "Content-Type")) {
                        input = dataBody.getJsonBytes();
                    }else {
                        input = dataBody.getQueryBytes();
                    }
                    outputStream.write(input, 0, input.length);
                }

                resPost.setSuccess(true);
                resPost.setStatus(httpURLConnection.getResponseCode());
                resPost.setResponse(AlnUtilsText.toString(httpURLConnection));

                httpURLConnection.disconnect();

            }catch(Exception e) {
                resPost.setException(e);
            }

            return resPost;
        }

    }

}
