package com.project.gmall.search;


import com.alibaba.dubbo.config.annotation.Reference;
import com.project.gmall.bean.PmsSearchSkuInfo;
import com.project.gmall.bean.PmsSkuInfo;
import com.project.gmall.search.testBean.Movie;
import com.project.gmall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.bind.SchemaOutputResolver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public 	class GmallSearchServiceApplicationTests {

	@Autowired
	JestClient jestClient;

	@Reference
	SkuService skuService;

	/**
	 *  测试elasticSearch
	 */
	@Test
	public 	void contextLoads() {
		// 查询数据
		List<PmsSkuInfo> pmsSkuInfos = skuService.getAllSku();

		// 封装数据
		List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
		for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {

			PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
			BeanUtils.copyProperties(pmsSkuInfo,pmsSearchSkuInfo);
			// 导入数据
			// 插入的dsl封装对象
			Index index = new Index.Builder(pmsSearchSkuInfo).id(pmsSearchSkuInfo.getId()).type("SearchSkuInfo").index("gmall").build();

			try {
				DocumentResult execute = jestClient.execute(index);
			} catch (IOException e) {
				e.printStackTrace();
			}

			pmsSearchSkuInfos.add(pmsSearchSkuInfo);
		}



	}

	public void search(){
		// 查询的dsl封装对象
		String query = "";
		query = getMySearchBuilder();

		Search search = new Search.Builder(query).addIndex("movie_chn").addType("movie").build();

		SearchResult execute = null;
		try {
			execute = jestClient.execute(search);
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<SearchResult.Hit<Movie, Void>> hits = execute.getHits(Movie.class);

		for (SearchResult.Hit<Movie, Void> hit : hits) {
			Movie source = hit.source;
			System.out.println(source.getName());
		}

		System.out.println(execute);
	}

	public String getMySearchBuilder() {
		// search
		SearchSourceBuilder mySearchBuilder = new SearchSourceBuilder();

		// bool
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

//		// term
//		TermQueryBuilder termQueryBuilder = new TermQueryBuilder("id","2");
//		// filter
//		boolQueryBuilder.filter(termQueryBuilder);

		// match
		MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("name","行动");
		// must
		boolQueryBuilder.must(matchQueryBuilder);

		// query
		mySearchBuilder.query(boolQueryBuilder);

		return mySearchBuilder.toString();
	}
}
