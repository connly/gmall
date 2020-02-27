package com.project.gmall.search.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.project.gmall.bean.PmsSearchParam;
import com.project.gmall.bean.PmsSearchSkuInfo;
import com.project.gmall.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService{
    @Autowired
    JestClient jestClient;

    @Override
    public List<PmsSearchSkuInfo> search(PmsSearchParam pmsSearchParam) {

        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList();

        // 查询的dsl封装对象
        String query = "";
        query = getMySearchBuilder(pmsSearchParam);

        Search search = new Search.Builder(query).addIndex("gmall").addType("SearchSkuInfo").build();

        SearchResult execute = null;
        try {
            execute = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);

        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            PmsSearchSkuInfo pmsSearchSkuInfo = hit.source;
//            System.out.println(pmsSearchSkuInfo.getSkuName());
            pmsSearchSkuInfos.add(pmsSearchSkuInfo);
        }

        return pmsSearchSkuInfos;
    }

    public String getMySearchBuilder(PmsSearchParam pmsSearchParam) {
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        String[] valueIds = pmsSearchParam.getValueId();

        // search
        SearchSourceBuilder mySearchBuilder = new SearchSourceBuilder();

        // bool
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

		// term 点击的过滤条件
        if(StringUtils.isNotBlank(catalog3Id)) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", catalog3Id);
            // filter
            boolQueryBuilder.filter(termQueryBuilder);
        }
        if(valueIds!=null && valueIds.length>0){
            for (String valueId : valueIds) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", valueId);
                // filter
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
		// keyword 用户输入的关键字
        if(StringUtils.isNotBlank(keyword)){
            // match
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName",keyword);
            // must
            boolQueryBuilder.must(matchQueryBuilder);
        }

        // query
        mySearchBuilder.query(boolQueryBuilder);

        // from size
        mySearchBuilder.from(0);
        mySearchBuilder.size(20);

        return mySearchBuilder.toString();
    }
}
