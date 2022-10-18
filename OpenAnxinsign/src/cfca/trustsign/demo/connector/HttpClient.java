package cfca.trustsign.demo.connector;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.net.ssl.*;

import cfca.trustsign.demo.constant.Request;
import cfca.trustsign.demo.util.CommonUtil;

public class HttpClient {
    public static final String PREFIX = "--", LINEND = "\r\n", DEFAULT_CHARSET = "UTF-8", DEFAULT_ENCODING = "gzip";
    public static final int DEFAULT_BUFFER_SIZE = 2048, DEFAULT_FILE_BUFFER_SIZE = 10 * 1024, DEFAULT_CONNECT_TIMEOUT = 3000, DEFAULT_READ_TIMEOUT = 15000;

    public static final String DEFAULT_SSL_PROTOCOL = "TLSv1.2", DEFAULT_HTTP_USER_AGENT = "client", DEFAULT_HTTP_CONNECTION = "close",
            DEFAULT_HTTP_CONTENT_TYPE = "text/plain", DEFAULT_HTTP_ACCEPT = "text/plain";

    public static final String DEFAULT_KEY_ALGORITHM = KeyManagerFactory.getDefaultAlgorithm(), DEFAULT_KEY_STORE_TYPE = KeyStore.getDefaultType(),
            DEFAULT_TRUST_ALGORITHM = TrustManagerFactory.getDefaultAlgorithm(), DEFAULT_TRUST_STORE_TYPE = KeyStore.getDefaultType();

    private static HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    public Config config = new Config();
    public SSLConfig sslConfig = new SSLConfig();
    public HttpConfig httpConfig = new HttpConfig();

    private SSLSocketFactory sslSocketFactory;

    public void initSSL(String keyStorePath, char[] keyStorePassword, String trustStorePath, char[] trustStorePassword)
            throws GeneralSecurityException, IOException {
        KeyManagerFactory keyManagerFactory = null;
        KeyStore keyStore = null;
        if (CommonUtil.isEmpty(sslConfig.keyProvider)) {
            keyManagerFactory = KeyManagerFactory.getInstance(sslConfig.keyAlgorithm);
            if (CommonUtil.isNotEmpty(sslConfig.keyStoreType)) {
                keyStore = KeyStore.getInstance(sslConfig.keyStoreType);
            }
        } else {
            keyManagerFactory = KeyManagerFactory.getInstance(sslConfig.keyAlgorithm, sslConfig.keyProvider);
            if (CommonUtil.isNotEmpty(sslConfig.keyStoreType)) {
                keyStore = KeyStore.getInstance(sslConfig.keyStoreType, sslConfig.keyProvider);
            }
        }
        if (CommonUtil.isEmpty(keyStorePath)) {
            keyManagerFactory.init(keyStore, keyStorePassword);
        } else {
            try (FileInputStream fileInputStream = new FileInputStream(keyStorePath)) {
                keyStore.load(fileInputStream, keyStorePassword);
                keyManagerFactory.init(keyStore, keyStorePassword);
            }
        }

        TrustManagerFactory trustManagerFactory = null;
        KeyStore trustStore = null;
        if (CommonUtil.isEmpty(sslConfig.trustProvider)) {
            trustManagerFactory = TrustManagerFactory.getInstance(sslConfig.trustAlgorithm);
            if (CommonUtil.isNotEmpty(sslConfig.trustStoreType)) {
                trustStore = KeyStore.getInstance(sslConfig.trustStoreType);
            }
        } else {
            trustManagerFactory = TrustManagerFactory.getInstance(sslConfig.trustAlgorithm, sslConfig.trustProvider);
            if (CommonUtil.isNotEmpty(sslConfig.trustStoreType)) {
                trustStore = KeyStore.getInstance(sslConfig.trustStoreType, sslConfig.trustProvider);
            }
        }
        if (CommonUtil.isEmpty(trustStorePath)) {
            trustManagerFactory.init(trustStore);
        } else {
            try (FileInputStream fileInputStream = new FileInputStream(trustStorePath)) {
                trustStore.load(fileInputStream, trustStorePassword);
                trustManagerFactory.init(trustStore);
            }
        }

        SSLContext sslContext = CommonUtil.isEmpty(sslConfig.sslProvider) ? SSLContext.getInstance(sslConfig.sslProtocol)
                : SSLContext.getInstance(sslConfig.sslProtocol, sslConfig.sslProvider);
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        sslSocketFactory = sslContext.getSocketFactory();
    }

    public HttpURLConnection connect(String url, String method) throws IOException {
        System.out.println("url:" + url);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        if (sslSocketFactory != null) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) connection;
            httpsConn.setSSLSocketFactory(sslSocketFactory);
            if (sslConfig.ignoreHostname) {
                httpsConn.setHostnameVerifier(ignoreHostnameVerifier);
            }
        }
        connection.setConnectTimeout(config.connectTimeout);
        connection.setReadTimeout(config.readTimeout);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod(method);
        connection.setRequestProperty("User-Agent", httpConfig.userAgent);
        connection.setRequestProperty("Connection", httpConfig.connection);
        connection.setRequestProperty("Content-Type", httpConfig.contentType + ";charset=" + config.charset);
        connection.setRequestProperty("Accept", httpConfig.accept);
        connection.setRequestProperty("Accept-Charset", config.charset);
        connection.setRequestProperty("Accept-Encoding", config.encoding);
        return connection;
    }

    public int send(HttpURLConnection connection, byte[] requestData) throws IOException {
        if (requestData != null) {
            connection.setFixedLengthStreamingMode(requestData.length);
            connection.connect();
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestData);
            outputStream.flush();
        } else {
            connection.connect();
        }
        return connection.getResponseCode();
    }

    public int sendFile(HttpURLConnection connection, Map<String, String> paramMap, InputStream is, String fileName, String contentType) throws IOException {
        if (CommonUtil.isNotEmpty(paramMap)) {
            String BOUNDARY = java.util.UUID.randomUUID().toString();
            connection.setRequestProperty("Connection", DEFAULT_HTTP_CONNECTION);
            connection.setRequestProperty("Charsert", DEFAULT_CHARSET);
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);

            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            for (Entry<String, String> entry : paramMap.entrySet()) {
                addTextBody(entry.getKey(), entry.getValue(), BOUNDARY, os);
            }

            if (is != null) {
                addBinaryBody(Request.CONTRACT_FILE, fileName, contentType, BOUNDARY, is, os);
            }

            byte[] end_data = CommonUtil.getBytes(PREFIX + BOUNDARY + PREFIX + LINEND);
            os.write(end_data);
            os.flush();
        } else {
            connection.connect();
        }
        return connection.getResponseCode();
    }

    private void addTextBody(String name, String text, String BOUNDARY, DataOutputStream os) throws IOException {
        StringBuilder sb = new StringBuilder(PREFIX).append(BOUNDARY).append(LINEND);
        sb.append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(LINEND);
        sb.append(LINEND).append(text).append(LINEND);
        os.write(CommonUtil.getBytes(sb.toString()));
    }

    private void addBinaryBody(String name, String fileName, String contentType, String BOUNDARY, InputStream is, DataOutputStream os) throws IOException {
        StringBuilder sb = new StringBuilder(PREFIX).append(BOUNDARY).append(LINEND);
        sb.append("Content-Disposition: form-data; name=\"").append(name).append("\"; filename=\"").append(fileName).append("\"").append(LINEND);
        sb.append("Content-Type: ").append(contentType).append(LINEND).append(LINEND);
        os.write(CommonUtil.getBytes(sb.toString()));

        byte[] buffer = new byte[DEFAULT_FILE_BUFFER_SIZE];
        int read = -1;
        while ((read = is.read(buffer)) != -1) {
            os.write(buffer, 0, read);
        }
        os.write(CommonUtil.getBytes(LINEND));
    }

    public byte[] receive(HttpURLConnection connection) throws IOException {
        InputStream inputStream = connection.getErrorStream();
        if (inputStream == null) {
            inputStream = connection.getInputStream();
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream(config.bufferSize);
        byte[] buffer = new byte[config.bufferSize];
        int read = -1, length = 0;
        while ((read = inputStream.read(buffer)) != -1) {
            os.write(buffer, 0, read);
            length += read;
        }
        System.out.println("length:" + length);
        return os.toByteArray();
    }

    public void disconnect(HttpURLConnection connection) {
        Optional.ofNullable(connection).ifPresent(HttpURLConnection::disconnect);
    }

    public static class Config {
        public String charset = DEFAULT_CHARSET;
        public String encoding = DEFAULT_ENCODING;
        public int bufferSize = DEFAULT_BUFFER_SIZE;
        public int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
        public int readTimeout = DEFAULT_READ_TIMEOUT;
    }

    public static class SSLConfig {
        public String sslProvider = null;
        public String sslProtocol = DEFAULT_SSL_PROTOCOL;
        public String keyProvider = null;
        public String keyAlgorithm = DEFAULT_KEY_ALGORITHM;
        public String keyStoreType = DEFAULT_KEY_STORE_TYPE;
        public String trustProvider = null;
        public String trustAlgorithm = DEFAULT_TRUST_ALGORITHM;
        public String trustStoreType = DEFAULT_TRUST_STORE_TYPE;
        public boolean ignoreHostname = true;
    }

    public static class HttpConfig {
        public String userAgent = DEFAULT_HTTP_USER_AGENT;
        public String connection = DEFAULT_HTTP_CONNECTION;
        public String contentType = DEFAULT_HTTP_CONTENT_TYPE;
        public String accept = DEFAULT_HTTP_ACCEPT;
    }
}
