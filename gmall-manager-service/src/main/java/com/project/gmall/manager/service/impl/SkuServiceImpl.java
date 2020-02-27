package com.project.gmall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.project.gmall.Util.RedisUtil;
import com.project.gmall.bean.*;
import com.project.gmall.manager.mapper.PmsSkuAttrValueMapper;
import com.project.gmall.manager.mapper.PmsSkuImageMapper;
import com.project.gmall.manager.mapper.PmsSkuInfoMapper;
import com.project.gmall.manager.mapper.PmsSkuSaleAttrValueMapper;
import com.project.gmall.service.SkuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;
    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;
    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;
    @Autowired
    RedisUtil redisUtil;


    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        // 主表
        pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        String skuId = pmsSkuInfo.getId();

        // 图片
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(skuId);
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }

        // 平台属性
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(skuId);
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }

        // 销售属性
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuId);
            pmsSkuSaleAttrValueMapper.insertSelective(skuSaleAttrValue);
        }

    }

    // 查询数据库
    public PmsSkuInfo getSkuBySkuIdFromDb(String skuId) {

        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        // 一个sku查出一件商品信息
        PmsSkuInfo pmsSkuInfo1 = pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        // 图片列表
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);

        pmsSkuInfo1.setSkuImageList(pmsSkuImages);
        return pmsSkuInfo1;
    }

    @Override
    public PmsSkuInfo getSkuBySkuId(String skuId) {

        PmsSkuInfo pmsSkuInfo = null;
        // 先查缓存
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();

            // 给key取名为：Sku:商品id:info (第id号的商品信息)
            String skuJSON = jedis.get("Sku:" + skuId + ":info");

            if (StringUtils.isNotBlank(skuJSON)) {
                // 缓存存在，从缓存中拿数据
                pmsSkuInfo = JSON.parseObject(skuJSON, PmsSkuInfo.class);

                // 为防止缓冲穿透，使用分布式锁保护数据库。
                // 通过redis的setnx方法设置锁。在同一时间访问同一商品的用户只能有一人，并设置访问时间
            } else {
                // 缓存不存在时，查询数据库
                // 设置过期时间的redis'
                String value = UUID.randomUUID().toString();
                String success = jedis.set("Sku:"+skuId+":lock",value,"nx","ex",10);// 生成自己特定的锁
                if(StringUtils.isNotBlank(success)){
                    pmsSkuInfo = getSkuBySkuIdFromDb(skuId);
                    // 查询完数据库后同步缓存
                    if(pmsSkuInfo!=null) {
                        jedis.set("Sku:" + skuId + ":info", JSON.toJSONString(pmsSkuInfo));

                        // 使用lua脚本同一时间执行删除操作
                        String script = "if redis.call('get', KEYS[1]) == ARGV[1] " +
                                "then return redis.call('del', KEYS[1]) else return 0 end";
                        jedis.eval(script, Collections.singletonList("Sku:"+skuId+":lock"), Collections.singletonList(value));
//                        // 删除自己定义的锁
//                        String values = jedis.get(success);
//                        if(values.equals(value)){
//                            jedis.del("Sku"+skuId+":lock");
//                        }
                    }
                }else {
                    // 还没有拿到分布式锁的用户
                    Thread.sleep(3000);
                    return getSkuBySkuId(skuId);
                }

            }
        } catch (Exception e) {
            // 记录系统异常日志
            e.printStackTrace();
        } finally {
            // 关闭jedis
            jedis.close();
        }
        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValuesBySpuId(String spuId) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setProductId(spuId);
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.select(pmsSkuInfo);

        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            String skuId = skuInfo.getId();
            PmsSkuSaleAttrValue pmsSkuSaleAttrValue = new PmsSkuSaleAttrValue();
            pmsSkuSaleAttrValue.setSkuId(skuId);
            List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValues = pmsSkuSaleAttrValueMapper.select(pmsSkuSaleAttrValue);

            skuInfo.setSkuSaleAttrValueList(pmsSkuSaleAttrValues);
        }

        return pmsSkuInfos;
    }

    @Override
    public List<PmsSkuInfo> getAllSku() {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectAll();

        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            String id = pmsSkuInfo.getId();

            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(id);
            List<PmsSkuAttrValue> skuAttrValueList = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);

            pmsSkuInfo.setSkuAttrValueList(skuAttrValueList);
        }
        return pmsSkuInfos;
    }

}
