package com.project.gmall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.project.gmall.bean.PmsSearchParam;
import com.project.gmall.bean.PmsSearchSkuInfo;
import com.project.gmall.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class SearchController {

    @Reference
    SearchService searchService;

    @RequestMapping("index.html")
    public String index() {
        return "index";
    }

    @RequestMapping("list.html")
    public String list(PmsSearchParam pmsSearchParam, ModelMap modelMap) {
        // 检索
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.search(pmsSearchParam);

        if(pmsSearchSkuInfos!=null && pmsSearchSkuInfos.size()>0){

            modelMap.put("skuLsInfoList",pmsSearchSkuInfos);
            return "list";
        }else{

            return "listError";
        }

    }
}
