package com.example.grabimages;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.grabimages.api.service.AutoHomeApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GrabImagesApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private AutoHomeApiService autoHomeApiService;

    int anchorPoint = 0;
    /**
     * 测试获取HTML内容
     */
    @Test
    public void getHtml() {
        //只抓精华帖子
        String baseUrl = "https://tieba.baidu.com/mg/f/getFrsData?kw=%E8%A1%A8%E6%83%85%E5%8C%85&rn=10&pn="+anchorPoint+"&is_good=1&cid=0&sort_type=0&fr=&default_pro=0&only_thread_list=0";
        String res = autoHomeApiService.getApi(baseUrl);
        if (res != null){
            JSONObject param = JSONObject.parseObject(res);
            JSONArray threadList = param.getJSONObject("data").getJSONArray("thread_list");
            //循环抓取每个帖子详情中的表情图
            for(int i = 0; i < threadList.size(); i++){
                String id = threadList.getJSONObject(i).getString("id");
                String rest = autoHomeApiService.getApi("https://tieba.baidu.com/mg/p/getPbData?pn=1&rn=30&only_post=1&kz=" + id);
                if (rest != null){
                    JSONObject params = JSONObject.parseObject(rest);
                    JSONArray threadLists = params.getJSONObject("data").getJSONArray("post_list");
                    //循环抓取每个帖子详情中的表情图
                    for(int m = 0; m < threadLists.size(); m++){
                        JSONArray content = threadLists.getJSONObject(m).getJSONArray("content");
                        for(int j = 0; j < content.size(); j++){
                            String src = content.getJSONObject(j).getString("src");
                            System.out.println(src);
                            if (src != null){
                                autoHomeApiService.getImage(src);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * 测试获取图片
     */
    @Test
    public void getImage() {
        String image = autoHomeApiService.getImage("https://car2.autoimg.cn/cardfs/product/g24/M09/AE/EB/800x0_1_q87_autohomecar__wKgHIVpxGh6AFSN1AAY8kcz3Aww921.jpg");
        System.out.println("image = " + image);
    }
}
