package com.project.gmall.service;

import com.project.gmall.bean.PmsSkuInfo;

import java.util.List;

public interface SkuService {
    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo getSkuBySkuId(String skuId);

    List<PmsSkuInfo> getSkuSaleAttrValuesBySpuId(String spuId);

    List<PmsSkuInfo> getAllSku();
}
