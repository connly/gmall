package com.project.gmall.service;

import com.project.gmall.bean.PmsSearchParam;
import com.project.gmall.bean.PmsSearchSkuInfo;

import java.util.List;

public interface SearchService {
    List<PmsSearchSkuInfo> search(PmsSearchParam pmsSearchParam);
}
