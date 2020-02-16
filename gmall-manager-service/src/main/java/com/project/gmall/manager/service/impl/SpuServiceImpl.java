package com.project.gmall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.project.gmall.bean.*;
import com.project.gmall.manager.mapper.*;
import com.project.gmall.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SpuServiceImpl implements SpuService{
    @Autowired
    PmsProductInfoMapper pmsProductInfoMapper;

    @Autowired
    PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;

    @Autowired
    PmsProductImageMapper pmsProductImageMapper;

    @Autowired
    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;

    @Autowired
    PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {
        PmsProductInfo pmsProductInfo = new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);

        return pmsProductInfoMapper.select(pmsProductInfo);
    }

    @Override
    public List<PmsBaseSaleAttr> baseSaleAttrList() {
        return pmsBaseSaleAttrMapper.selectAll();
    }

    @Override
    public void saveSpuInfo(PmsProductInfo pmsProductInfo){
        // 添加spu信息
        pmsProductInfoMapper.insertSelective(pmsProductInfo);
        // 获取spu主键
        String spuId = pmsProductInfo.getId();

        // 添加spu销售属性
        List<PmsProductSaleAttr> spuSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
        for (PmsProductSaleAttr pmsProductSaleAttr : spuSaleAttrList) {
            // 添加spu主键
            pmsProductSaleAttr.setProductId(spuId);
            pmsProductSaleAttrMapper.insertSelective(pmsProductSaleAttr);
//            pmsProductSaleAttr.getId(); 不采用销售属性id，采用平台已经定义好的属性字典表id
            // 获取销售属性字典表id
            String saleAttrId = pmsProductSaleAttr.getSaleAttrId();

            // 添加销售属性值
            List<PmsProductSaleAttrValue> spuSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
            for (PmsProductSaleAttrValue pmsProductSaleAttrValue : spuSaleAttrValueList) {
                // 采用联合外键
                // 添加spu主键
                pmsProductSaleAttrValue.setProductId(spuId);
                // 添加销售属性字典表id
                pmsProductSaleAttrValue.setSaleAttrId(saleAttrId);

                pmsProductSaleAttrValueMapper.insertSelective(pmsProductSaleAttrValue);
            }
        }

        // 添加spu图片


    }
}
