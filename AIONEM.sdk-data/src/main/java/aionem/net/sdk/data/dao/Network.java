package aionem.net.sdk.data.dao;

import aionem.net.sdk.core.utils.UtilsNetwork;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.beans.DaoRes;
import aionem.net.sdk.data.beans.Data;
import lombok.extern.log4j.Log4j2;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


@Log4j2
public @lombok.Data class Network {

    protected String link;
    protected Data dataParams;
    protected Data dataHeaders;

    protected int timeout = 30*1000;

    public Network(final String link) {
        setLink(link);
    }

    public Network setLink(String link) {
        this.link = link;
        return this;
    }

    public Network setDataParams(final Data dataParams) {
        this.dataParams = dataParams != null ? dataParams : new Data();
        return this;
    }

    public Network setDataHeaders(final Data dataHeaders) {
        this.dataHeaders = dataHeaders != null ? dataHeaders : new Data();
        return this;
    }

    public Network setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public static DaoRes doGet(final String link) {
        return doGet(link, new Data());
    }
    public static DaoRes doGet(final String link, final Data dataParams) {
        return doGet(link, dataParams, new Data());
    }
    public static DaoRes doGet(final String link, final Data dataParams, final Data dataHeaders) {
        return new Get(link)
                .setDataParams(dataParams)
                .setDataHeaders(dataHeaders)
                .get();
    }

    public static DaoRes doPost(final String link) {
        return doPost(link, new Data());
    }
    public static DaoRes doPost(final String link, final Data dataParams) {
        return doPost(link, dataParams, new Data());
    }
    public static DaoRes doPost(final String link, final Data dataBody, final Data dataHeaders) {
        return doPost(link, new Data(), dataBody, dataHeaders);
    }
    public static DaoRes doPost(final String link, final Data dataParams, final Data dataBody, final Data dataHeaders) {
        return new Post(link)
                .setDataParams(dataParams)
                .setDataBody(dataBody)
                .setDataHeaders(dataHeaders)
                .post();
    }

    public static class Get extends Network {

        public Get(String link) {
            super(link);
        }

        @Override
        public Get setLink(final String link) {
            super.setLink(link);
            return this;
        }

        @Override
        public Get setDataParams(final Data dataParams) {
            super.setDataParams(dataParams);
            return this;
        }

        @Override
        public Get setDataHeaders(final Data dataHeaders) {
            super.setDataHeaders(dataHeaders);
            return this;
        }

        @Override
        public Get setTimeout(int timeout) {
            super.setTimeout(timeout);
            return this;
        }

        public DaoRes get() {

            final DaoRes resGet = new DaoRes();

            try {

                String linkUrl = link;

                if(dataParams != null) {
                    linkUrl = UtilsNetwork.addParameter(link, dataParams.getValuesString());
                }

                UtilsNetwork.disableSslVerification();

                final URL url = new URL(linkUrl);
                final HttpURLConnection httpURLConnection;
                if("https".equalsIgnoreCase(url.getProtocol())) {
                    httpURLConnection = (HttpsURLConnection) url.openConnection();
                }else {
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                }

                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setConnectTimeout(timeout);
                httpURLConnection.setReadTimeout(timeout);

                if(dataHeaders != null) {
                    for(final String key : dataHeaders.keySet()) {
                        final String value = dataHeaders.get(key);
                        httpURLConnection.setRequestProperty(key, value);
                    }
                }

                httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

                resGet.setStatus(httpURLConnection.getResponseCode());
                resGet.setResponse(UtilsText.toString(httpURLConnection));
                resGet.setSuccess(true);

                httpURLConnection.disconnect();

            }catch(Exception e) {
                resGet.setException(e);
            }

            return resGet;
        }

    }

    public static class Post extends Network {

        private Data dataBody;

        public Post(String link) {
            super(link);
        }

        @Override
        public Post setLink(final String link) {
            super.setLink(link);
            return this;
        }

        @Override
        public Post setDataParams(final Data dataParams) {
            super.setDataParams(dataParams);
            return this;
        }

        public Post setDataBody(final Data dataBody) {
            this.dataBody = dataBody;
            return this;
        }

        @Override
        public Post setDataHeaders(final Data dataHeaders) {
            super.setDataHeaders(dataHeaders);
            return this;
        }

        @Override
        public Post setTimeout(int timeout) {
            super.setTimeout(timeout);
            return this;
        }

        public DaoRes post() {

            final DaoRes resPost = new DaoRes();

            try {

                String linkUrl = link;

                if(dataParams != null) {
                    linkUrl = UtilsNetwork.addParameter(link, dataParams.getValuesString());
                }

                UtilsNetwork.disableSslVerification();

                final URL url = new URL(linkUrl);
                final HttpURLConnection httpURLConnection;
                if("https".equalsIgnoreCase(url.getProtocol())) {
                    httpURLConnection = (HttpsURLConnection) url.openConnection();
                }else {
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                }

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setConnectTimeout(timeout);
                httpURLConnection.setReadTimeout(timeout);

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
                resPost.setResponse(UtilsText.toString(httpURLConnection));

                httpURLConnection.disconnect();

            }catch(Exception e) {
                resPost.setException(e);
            }

            return resPost;
        }

    }

}
