package com.zongmu.ffmpeg.util;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zongmu.ffmpeg.properties.ProxyProperties;

@Service
public class HttpClientWrapper {
    private static Logger logger = Logger.getLogger(HttpClientWrapper.class);
    private HttpClient httpClient;
    private final int connectionTimeout = 50000;
    private final int socketTimeout = 50000;
    private final int requestTimeout = 50000;

    @Autowired
    private ProxyProperties proxyProperties;

    public String get(String url) {
        logger.info("Start to send http get request " + url);
        HttpGet httpGet = new HttpGet(url);

        try {
            HttpResponse httpResponse = this.getHttpClient().execute(httpGet);
            logger.info("Send http get request success.");
            logger.info("Http status code: " + httpResponse.getStatusLine().getStatusCode());
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                InputStream inputStream = httpEntity.getContent();
                String content = IOUtils.toString(inputStream);
                this.safeClose(inputStream);
                logger.info("Http response: ");
                logger.info(content);
                return content;
            } else {
                logger.info("Http response is empty. ");
            }

        } catch (Exception ex) {
            logger.error("Send http get request failed.");
            logger.error(ExceptionUtils.getStackTrace(ex));
            this.safeAbort(httpGet);
        } finally {
            this.safeReleaseConnection(httpGet);
        }

        return null;
    }

    public String post(String url, Object object) {
        logger.info("Start to send http post request " + url);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        String payload = null;
        if (object != null) {
            payload = this.getStringValue(object);
        }

        try {
            if (!StringUtils.isEmpty(payload)) {
                logger.info("Http post payload: ");
                logger.info(payload);
                httpPost.setEntity(new StringEntity(payload));
            }

            HttpResponse httpResponse = this.getHttpClient().execute(httpPost);
            logger.info("Send http post request success.");
            logger.info("Http status code: " + httpResponse.getStatusLine().getStatusCode());
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                InputStream inputStream = httpEntity.getContent();
                String content = IOUtils.toString(inputStream);
                this.safeClose(inputStream);
                logger.info("Http response: ");
                logger.info(content);
                return content;
            } else {
                logger.info("Http response is empty. ");
            }

        } catch (Exception ex) {
            logger.error("Send http post request failed.");
            logger.error(ExceptionUtils.getStackTrace(ex));
            this.safeAbort(httpPost);
        } finally {
            this.safeReleaseConnection(httpPost);
        }

        return null;
    }

    private HttpClient getHttpClient() {
        if (httpClient == null) {
            HttpClientBuilder hcBuilder = HttpClientBuilder.create();
            Builder configBuilder = RequestConfig.custom();
            configBuilder.setConnectTimeout(connectionTimeout);
            configBuilder.setSocketTimeout(socketTimeout);
            configBuilder.setConnectionRequestTimeout(requestTimeout);
            hcBuilder.setDefaultRequestConfig(configBuilder.build());
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
            connManager.setMaxTotal(100);
            connManager.setDefaultMaxPerRoute(10);
            hcBuilder.setConnectionManager(connManager);
            HttpHost proxyHost = this.getProxyHost();
            if (proxyHost != null) {
                hcBuilder.setProxy(proxyHost);
            }

            httpClient = hcBuilder.build();
        }

        return httpClient;
    }

    private HttpHost getProxyHost() {
        if (this.proxyProperties.isEnabled()) {
            return new HttpHost(this.proxyProperties.getHost(), this.proxyProperties.getPort());
        }

        return null;
    }

    private void safeClose(InputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception ex) {
                logger.error("Close stream failed.", ex);
                logger.error(ExceptionUtils.getStackTrace(ex));
            }
        }
    }

    private void safeAbort(HttpUriRequest request) {
        if (request != null) {
            try {
                request.abort();
            } catch (Exception ex) {
                logger.error("Abort request failed.", ex);
                logger.error(ExceptionUtils.getStackTrace(ex));
            }
        }
    }

    private void safeReleaseConnection(HttpRequestBase request) {
        if (request != null) {
            try {
                request.releaseConnection();
            } catch (Exception ex) {
                logger.error("release connection request failed.", ex);
                logger.error(ExceptionUtils.getStackTrace(ex));
            }
        }
    }

    private String getStringValue(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        } catch (Exception ex) {
            logger.error("Convert to string failed.", ex);
            logger.error(ExceptionUtils.getStackTrace(ex));
        }

        return null;
    }
}
