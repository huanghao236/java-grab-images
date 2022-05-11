package com.example.grabimages.api.service;

public interface AutoHomeApiService {


    /**
     * 异步抓取页面数据并下载图片到本地
     * @param url
     */
    void getApiAsync(String url);


    /**
     * 使用get请求获取API数据
     * @param url
     */
    String getApi(String url);


    /**
     * 使用get请求获取页面数据
     * @param url
     */
    String getHtml(String url);


    /**
     * 使用get请求下载图片,返回图片名称
     * @param url
     */
    String getImage(String url);
}
