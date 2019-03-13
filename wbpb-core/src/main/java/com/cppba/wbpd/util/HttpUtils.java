package com.cppba.wbpd.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @description: http工具类
 * @author: winfed
 * @create: 2019-03-11 17:14
 **/
public class HttpUtils {

    public static HttpResponse doPostMultiPart(String url, Map<String, String> header, Map<String, String> queryParams, Map<String, String> params, String content) throws IOException {
        if (queryParams != null && !queryParams.isEmpty()) {
            String queryString = convertParams(queryParams);
            url += "?" + queryString;
        }

        HttpURLConnection connection = getHttpURLConnection(url, header);

        StringBuilder requestBody = new StringBuilder();
        // 参数
        if (params != null) {
            requestBody.append(convertParams(params));
        }
        // 附件
        if (StringUtils.isNotBlank(content)) {
            String END_LINE = "\r\n";
            String TWO = "--";
            String boundary = "===" + System.currentTimeMillis() + "===";
            String contentType = "multipart/form-data; boundary=" + boundary;
            connection.setRequestProperty("Content-Type", contentType);
            StringBuilder bodyBulider = new StringBuilder();
            bodyBulider.append(TWO).append(boundary).append(END_LINE)
                    .append("Content-Disposition: form-data; name=\"b64_data\"")
                    .append(END_LINE).append(END_LINE)
                    .append(content)
                    .append(END_LINE)
                    .append(TWO).append(boundary).append(TWO);
            requestBody.append(bodyBulider);
        }
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        bw.write(requestBody.toString());
        bw.flush();
        bw.close();
        connection.connect();

        return new HttpResponse(connection.getResponseCode(), getHeaderFromConnection(connection), getBodyFromConnection(connection));
    }

    public static HttpResponse doPost(String url, Map<String, String> header, Map<String, String> params) throws IOException {
        return HttpUtils.doPostMultiPart(url, header, null, params, null);
    }

    public static HttpResponse doPost(String url, Map<String, String> header, Map<String, String> queryParams, Map<String, String> params) throws IOException {
        return HttpUtils.doPostMultiPart(url, header, queryParams, params, null);
    }

    private static HttpURLConnection getHttpURLConnection(String url, Map<String, String> header) throws IOException {
        URL u = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) u.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);

        if (header != null) {
            header.forEach(connection::setRequestProperty);
        }
        return connection;
    }

    private static String convertParams(Map<String, String> params) {
        Set<Map.Entry<String, String>> entries = params.entrySet();
        StringBuilder sb = new StringBuilder();
        entries.forEach(e -> {
            sb.append(e.getKey());
            sb.append("=");
            sb.append(e.getValue());
            sb.append("&");
        });
        String s = sb.toString();
        return s.substring(0, s.length() - 1);
    }

    private static Map<String, String> getHeaderFromConnection(HttpURLConnection connection) {
        Map<String, List<String>> headerFields = connection.getHeaderFields();

        List<String> list = headerFields.get("Set-Cookie");
        String cookie = null;
        if (list != null) {
            StringBuilder sb = new StringBuilder();
            for (String s : list) {
                int i = s.indexOf(";");
                if (i != -1) {
                    String substring = s.substring(0, i);
                    sb.append(substring).append("; ");
                } else {
                    break;
                }
            }
            cookie = sb.toString();
            if (cookie.length() != 0) {
                cookie = cookie.substring(0, cookie.length() - 2);
            }
        }

        Map<String, String> header = new HashMap<>();
        Set<Map.Entry<String, List<String>>> entries = headerFields.entrySet();
        entries.forEach(e -> {
            StringBuilder sb = new StringBuilder();
            List<String> values = e.getValue();
            for (String s : values) {
                sb.append(s);
            }
            header.put(e.getKey(), sb.toString());
        });
        header.put("Set-Cookie", cookie);
        return header;
    }

    private static String getBodyFromConnection(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();

        if (responseCode == HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            return readInputStream(inputStream);
        } else {
            InputStream errorStream = connection.getErrorStream();
            return readInputStream(errorStream);
        }
    }

    private static String readInputStream(InputStream is) throws IOException {
        String str;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        while ((str = bufferedReader.readLine()) != null) {
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }

    @Data
    @AllArgsConstructor
    public static class HttpResponse {
        private int statusCode;
        private Map<String, String> header;
        private String body;
    }
}
