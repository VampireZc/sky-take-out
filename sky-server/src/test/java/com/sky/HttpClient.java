package com.sky;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class HttpClient {

    @Test
    public void testGet() throws IOException { // 测试GET请求
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet("http://localhost:8080/admin/shop/status");
        // 添加请求头 token
        get.addHeader("token", "eyJhbGciOiJIUzI1NiJ9.eyJlbXBJZCI6MSwiZXhwIjoxNzkzOTYyOTYyfQ.jK5b-zMS9mBZvX6_zW6AIK3papvLgLQDlcQKE53g1ME");
        // 拿到响应
        CloseableHttpResponse response = client.execute(get);

        // 拿到响应状态
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("响应状态为: " + statusCode);

        // 解析返回值
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        System.out.println("响应结果为: " + responseBody);

        response.close();
        client.close();

    }

    @Test
    public void testPost() throws IOException, JSONException { // 测试POST请求
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://localhost:8080/admin/employee/login");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "admin");
        jsonObject.put("password", "123456");

        // 设置请求体
        StringEntity entity = new StringEntity(jsonObject.toString());
        entity.setContentType("application/json");
        entity.setContentEncoding("utf-8");
        post.setEntity(entity);


        CloseableHttpResponse response = client.execute(post);

        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("响应状态为: " + statusCode);

        HttpEntity responseEntity = response.getEntity();
        String responseBody = EntityUtils.toString(responseEntity);
        System.out.println("响应结果为: " + responseBody);
    }
}
