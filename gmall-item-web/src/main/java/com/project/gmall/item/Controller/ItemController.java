package com.project.gmall.item.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.project.gmall.bean.PmsProductSaleAttr;
import com.project.gmall.bean.PmsSkuInfo;
import com.project.gmall.bean.PmsSkuSaleAttrValue;
import com.project.gmall.service.SkuService;
import com.project.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {

    @Reference
    SkuService skuService;
    @Reference
    SpuService spuService;

    /**
     * 静态访问
     *
     * @param skuId
     * @param modelMap
     * @return
     */
    // 将该skuId对应的spuId找出来，并将该spuId下的所有销售属性值组合存放到静态文件中
    @RequestMapping("{skuId}.htm")
    public String item2(@PathVariable String skuId, ModelMap modelMap) {

        // 提前将网站的所有spuId对应的销售属性值组合的map全部生成好，生成.js的静态文件
        // 在用户访问功能时，只需要向前台传递spuId就可以
        // 前台根据spuId找到对应的静态.js文件，获得map，找到对应的skuId
        // 如果前台加载js文件失败了，则异步调用一个后台方法，生成该js静态文件
        PmsSkuInfo pmsSkuInfo = skuService.getSkuBySkuId(skuId);
        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(), skuId);

        modelMap.put("skuInfo", pmsSkuInfo);
        modelMap.put("spuSaleAttrListCheckBySku", pmsProductSaleAttrs);

        modelMap.put("spuId", pmsSkuInfo.getProductId());

        return "item";
    }

    // 页面的切换
    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap modelMap) {
        // 在用户查询商品详情时，将商品skuId对应的销售属性组合map隐藏到页面
        // 然后用户在点击销售属性值时，不进行后台查询，直接对应查找前台的map中是否包含skuId
        // 如果有，则跳转，如果没有，则不跳。
        PmsSkuInfo pmsSkuInfo = skuService.getSkuBySkuId(skuId);
        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(), skuId);
//      当前sku的销售属性值在网页上可直接显示

//      1.
//        当前sku的销售属性值
//        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : pmsSkuSaleAttrValues) {
//            // skuId的spu销售属性值
//            for (PmsProductSaleAttr pmsProductSaleAttr : pmsProductSaleAttrs) {
//                List<PmsProductSaleAttrValue> spuSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
//                for (PmsProductSaleAttrValue pmsProductSaleAttrValue : spuSaleAttrValueList) {
//                    // 作比较
//                    if(pmsSkuSaleAttrValue.getId().equals(pmsProductSaleAttrValue.getId())) {
//                        // 当前被选中的销售属性id，将pmsSkuSaleAttrValue的isCheck标记为1
//
//                    }
//                }
//            }
//        ｝
        // 写mybatis的sql语句
        modelMap.put("skuInfo", pmsSkuInfo);
        modelMap.put("spuSaleAttrListCheckBySku", pmsProductSaleAttrs);

        // 查询当前spu下的销售属性和skuId对应关系
        // 1.根据当前sku找到对应的spuId，
        String spuId = pmsSkuInfo.getProductId();
        // 2.根据spuId找到当前spu下所有的sku
        List<PmsSkuInfo> pmsSkuInfos = skuService.getSkuSaleAttrValuesBySpuId(spuId);

        Map<String, String> map = new HashMap();
        // 3.找到当前spu下所有sku对应的销售属性值组，并将其视为key存入map中去
        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
            String k = "";
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
                k += "|" + pmsSkuSaleAttrValue.getSaleAttrValueId();
            }
            String v = skuInfo.getId();
            map.put(k, v);
        }

        // 将map键值对转化为JSON数据传到页面
        modelMap.put("skuIdMap", JSON.toJSONString(map));

        return "item";
    }

    public String getMySpuIdMap(String spuId){
        List<PmsSkuInfo> pmsSkuInfos = skuService.getSkuSaleAttrValuesBySpuId(spuId);

        Map<String, String> map = new HashMap();
        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
            String k = "";
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
                k += "|" + pmsSkuSaleAttrValue.getSaleAttrValueId();
            }
            String v = skuInfo.getId();
            map.put(k, v);
        }

        String json = JSON.toJSONString(map);
        File file = new File("static.spuIdMap/spuId_" + spuId + ".json");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    @RequestMapping("index.html")
    public String index(ModelMap modelMap, HttpSession session) {
        modelMap.put("hello", "hello");

        List<Object> hellos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            hellos.add("字符串" + (i + 1));
        }
        modelMap.put("hellos", hellos);

        modelMap.put("mv", "美女");

        session.setAttribute("name", "tom");

        return "index";
    }
}
