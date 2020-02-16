package com.project.gmall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.project.gmall.bean.PmsBaseCatalog1;
import com.project.gmall.bean.PmsBaseCatalog2;
import com.project.gmall.bean.PmsBaseCatalog3;
import com.project.gmall.manager.mapper.PmsBaseCatalog1Mapper;
import com.project.gmall.manager.mapper.PmsBaseCatalog2Mapper;
import com.project.gmall.manager.mapper.PmsBaseCatalog3Mapper;
import com.project.gmall.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class CatalogServiceImpl implements CatalogService {
    @Autowired
    PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;

    @Autowired
    PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;

    @Autowired
    PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;

    // 一级查询
    @Override
    public List<PmsBaseCatalog1> getCatalog1() {
        List<PmsBaseCatalog1> pmsBaseCatalog1s = pmsBaseCatalog1Mapper.selectAll();
        return pmsBaseCatalog1s;
    }

    // 二级分类
    @Override
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id) {
        // 通用mapper增加参数时，参数类型一般都是该对象所封装的参数
        // 把catalog1封装给catalog1_id的字段
        PmsBaseCatalog2 pmsBaseCatalog2 = new PmsBaseCatalog2();
        pmsBaseCatalog2.setCatalog1Id(catalog1Id);

        List<PmsBaseCatalog2> pmsBaseCatalog2s = pmsBaseCatalog2Mapper.select(pmsBaseCatalog2);
        return pmsBaseCatalog2s;
    }

    // 三级分类
    @Override
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id) {
        PmsBaseCatalog3 pmsBaseCatalog3 = new PmsBaseCatalog3();
        pmsBaseCatalog3.setCatalog2Id(catalog2Id);

        List<PmsBaseCatalog3> pmsBaseCatalog3s = pmsBaseCatalog3Mapper.select(pmsBaseCatalog3);
        return pmsBaseCatalog3s;
    }
}
