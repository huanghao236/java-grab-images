package com.example.grabimages.api.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.grabimages.api.service.AutoHomeApiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

@Service
@Slf4j
public class AutoHomeApiServiceImpl implements AutoHomeApiService {


    private PoolingHttpClientConnectionManager connectionManager;

    @Autowired
    public void setConnectionManager(PoolingHttpClientConnectionManager connectionManager) {
        this.connectionManager = connectionManager;

    }

    /**
     * 异步抓取Api数据并下载图片到本地
     */
    @Async
    public void getApiAsync(String url)
    {
        String res = this.getApi(url);
        if (res != null){
            JSONObject param = JSONObject.parseObject(res);
            JSONArray threadList = param.getJSONObject("data").getJSONArray("post_list");
            //循环抓取每个帖子详情中的表情图
            for(int i = 0; i < threadList.size(); i++){
                JSONArray content = threadList.getJSONObject(i).getJSONArray("content");
                for(int j = 0; j < content.size(); j++){
                    String src = content.getJSONObject(j).getString("src");
                    if (src != null){
                        this.getImage(src);
                    }
                }
            }
        }
    }

    /**
     * 根据百度API获取数据
     * @param url
     * @return
     */
    @Override
    public String getApi(String url){
        // 使用连接池管理器获取连接
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse httpResponse = null;
        try {
            // 发起请求
            httpResponse = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = httpResponse.getEntity();
            return EntityUtils.toString(responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("获取信息异常：{}", e);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("获取信息响应关闭异常：{}", e);
                }
            }
        }
        return null;
    }


    @Override
    public String getHtml(String url) {
        // 使用连接池管理器获取连接
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
        // 创建httpGet请求
        HttpGet httpGet = new HttpGet(url);
        setHeaders(httpGet);
        CloseableHttpResponse httpResponse = null;
        String html;
        try {
            // 发起请求
            httpResponse = httpClient.execute(httpGet);
            // 判断请求是否成功
            if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {
                // 判断是否有响应体
                if (httpResponse.getEntity() != null) {
                    // 如果有响应体，则进行解析
                    html = EntityUtils.toString(httpResponse.getEntity(), Charsets.UTF_8);
                    return html;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("获取信息异常：{}", e);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("获取信息响应关闭异常：{}", e);
                }
            }
        }
        return null;
    }

    /**
     * 下载图片到本地
     * @param url
     * @return
     */
    @Override
    public String getImage(String url) {
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
        HttpGet httpGet = new HttpGet(url);
        //setHeaders(httpGet);
        CloseableHttpResponse httpResponse = null;
        String fileName;
        try {
            httpResponse = httpClient.execute(httpGet);
            // 判断请求是否成功
            if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {
                // 判断是否有响应体
                if (httpResponse.getEntity() != null) {
                    // 如果有响应体，则进行解析
                    String contentTypeVal = httpResponse.getFirstHeader("Content-Type").getValue();
                    if (contentTypeVal.contains("image/")) {
                        String extName = contentTypeVal.split("/")[1];
                        fileName = UUID.randomUUID().toString().replace("-", "") + "." + extName;
                        //存储地址自行更改
                        OutputStream os = new FileOutputStream("D:\\phpstudy_pro\\WWW\\web2\\test\\" + fileName);
                        httpResponse.getEntity().writeTo(os);
                        return fileName;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("获取图片异常：{}", e);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("获取图片响应关闭异常：{}", e);
                }
            }
        }
        return null;
    }



    private void setHeaders(HttpGet get) {
        get.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        get.setHeader("Accept-Encoding", "gzip, deflate");
        get.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
        get.setHeader("Cache-Control", "max-age=0");
        get.setHeader("Cookie", "BAIDUID=1C7B44FEB3F17597BD6C81EB4A61D0FD:FG=1; BAIDUID_BFESS=1C7B44FEB3F17597BD6C81EB4A61D0FD:FG=1; Hm_lvt_98b9d8c2fd6608d564bf2ac2ae642948=1652150595; Hm_lpvt_98b9d8c2fd6608d564bf2ac2ae642948=1652150595; video_bubble0=1; BAIDU_WISE_UID=wapp_1652150595602_684; USER_JUMP=-1; ab_sr=1.0.1_NTQwZjdkNjI3NDI5MTY0YTFmY2U0YTQ3ZjhkMTg1NTMzMDhmZGQ3YzdiNTkyZTY1ZDNjMTUzM2RjMjJkNzNjZmQ2NmE0MDdlYWVjNDRkYjUzNDViZDk5ZTM2M2M2YWEwMmY4MjQ4YjZjZTZiNzEwNDQwM2UxMjY3NjE1N2Y3M2UzMjA2NDI4YTgzOWY2Mzg5MjMxZjRjYTI0ZmY4OThmOA==; st_data=b2fad95d045dffd8dbcf5a4fd13079552f634a3f0330b6e58658bf0e8dceceb709232216922e3cb2afab382ab2b06242799435cbb08423d904514693f4c782de70eba1f029e9262cbd1d9b4fa7376f1f4cb959348c27b60213f7855017986fbc; st_key_id=17; st_sign=565f5937");
        get.setHeader("Connection", "keep-alive");
        get.setHeader("Host", "tieba.baidu.com");
        get.setHeader("Upgrade-Insecure-Requests", "1");
        //get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.41 Safari/537.36");
    }
}
