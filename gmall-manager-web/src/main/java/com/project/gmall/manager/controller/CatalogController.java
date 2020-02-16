package com.project.gmall.manager.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.project.gmall.bean.PmsBaseCatalog1;
import com.project.gmall.bean.PmsBaseCatalog2;
import com.project.gmall.bean.PmsBaseCatalog3;
import com.project.gmall.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
// 允许跨域请求
@CrossOrigin
public class CatalogController {

    @Reference
    CatalogService catalogService;


    @RequestMapping("getCatalog1")
    @ResponseBody
    public List<PmsBaseCatalog1> getCatalog1(){

        List<PmsBaseCatalog1> pmsBaseCatalog1s = catalogService.getCatalog1();

        return pmsBaseCatalog1s;
    }

    @RequestMapping("getCatalog2")
    @ResponseBody
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id){

        List<PmsBaseCatalog2> pmsBaseCatalog2s = catalogService.getCatalog2(catalog1Id);

        return pmsBaseCatalog2s;
    }

    @RequestMapping("getCatalog3")
    @ResponseBody
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id){

        List<PmsBaseCatalog3> pmsBaseCatalog3s = catalogService.getCatalog3(catalog2Id);

        return pmsBaseCatalog3s;
    }

}
