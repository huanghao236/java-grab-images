package com.example.grabimages.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.grabimages.api.service.AutoHomeApiService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@DisallowConcurrentExecution
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class CrawlerAutoHomeJob {

    //锚点
    int anchorPoint = 1;

    @Autowired
    private AutoHomeApiService autoHomeApiService;

    //添加定时任务--每20秒执行一次
    @Scheduled(cron = "0/20 * * * * ?")
    protected void executeInternal() {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>> start crawlerAutoHomeJob");
        //只抓精华帖子
        String baseUrl = "https://tieba.baidu.com/mg/f/getFrsData?kw=%E8%A1%A8%E6%83%85%E5%8C%85&rn=10&pn="+anchorPoint+"&is_good=1&cid=0&sort_type=0&fr=&default_pro=0&only_thread_list=0";
        String res = autoHomeApiService.getApi(baseUrl);
        if (res != null){
            JSONObject param = JSONObject.parseObject(res);
            JSONArray threadList = param.getJSONObject("data").getJSONArray("thread_list");
            //循环抓取每个帖子详情中的表情图
            for(int i = 0; i < threadList.size(); i++){
                String id = threadList.getJSONObject(i).getString("id");
                autoHomeApiService.getApiAsync("https://tieba.baidu.com/mg/p/getPbData?pn=1&rn=30&only_post=1&kz=" + id);
            }
            anchorPoint++;
            //只抓100页，或自行调整
            if (anchorPoint > 100){
                anchorPoint = 0;
            }
        }
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>> end crawlerAutoHomeJob");
    }
}
