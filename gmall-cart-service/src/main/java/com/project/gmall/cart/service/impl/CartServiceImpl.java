package com.project.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.project.gmall.Util.RedisUtil;
import com.project.gmall.bean.OmsCartItem;
import com.project.gmall.cart.mapper.OmsCartItemMapper;
import com.project.gmall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService{
    @Autowired
    OmsCartItemMapper omsCartItemMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public OmsCartItem isCartEmpty(String userId, String skuId) {
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(userId);
        omsCartItem.setProductSkuId(skuId);

        return omsCartItemMapper.selectOne(omsCartItem);
    }

    /**
     * 更新购物车功能
     * @param cartItemFromUser
     */
    @Override
    public void update(OmsCartItem cartItemFromUser) {
        Example e = new Example(OmsCartItem.class);
        e.createCriteria().andEqualTo("id",cartItemFromUser.getId());

        omsCartItemMapper.updateByExampleSelective(cartItemFromUser,e);

        // 同步缓存
//        setCache(cartItemFromUser);
        flushCacheByUserId(cartItemFromUser.getMemberId());

    }

    /**
     * 添加购物车功能
     * @param omsCartItem
     */
    @Override
    public void add(OmsCartItem omsCartItem) {

        omsCartItemMapper.insertSelective(omsCartItem);

        // 同步缓存
//        setCache(omsCartItem);
        flushCacheByUserId(omsCartItem.getMemberId());

    }

    /**
     * 根据用户id获取该用户的购物车数据
     * @param userId
     * @return
     */
    @Override
    public List<OmsCartItem> getCartsByUserId(String userId) {
        return getCacheByUserId(userId);
    }

    /**
     * 更改购物车特定数据的选中状态
     * @param userId
     * @param skuId
     * @param isChecked
     */
    @Override
    public void updateChecked(String userId, String skuId, String isChecked) {
        Example e = new Example(OmsCartItem.class);
        e.createCriteria().andEqualTo("productSkuId",skuId)
                .andEqualTo("memberId",userId);

        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setIsChecked(isChecked);

        omsCartItemMapper.updateByExampleSelective(omsCartItem,e);

        // 同步缓存
        flushCacheByUserId(userId);

    }

    // 查缓存
    private List<OmsCartItem> getCacheByUserId(String userId) {
        List<OmsCartItem> omsCartItemList = new ArrayList<>();

        Jedis jedis = redisUtil.getJedis();
        List<String> hvals = jedis.hvals("User:" + userId + ":carts");

        if(hvals!=null && hvals.size()>0){
            for (String hval : hvals) {
                OmsCartItem omsCartItem = JSON.parseObject(hval,OmsCartItem.class);
                omsCartItemList.add(omsCartItem);
            }
        }
        jedis.close();
        return omsCartItemList;
    }

    /**
     * /购物车缓存整体同步
     * @param userId
     */
    public void flushCacheByUserId(String userId){
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(userId);
        List<OmsCartItem> omsCartItems = omsCartItemMapper.select(omsCartItem);

        // 若此用户购物车数据不为空
        if(omsCartItems!=null && omsCartItems.size()>0){
            // key:用户购物车，k:商品id， v:该商品的购物车数据的json字符串
            String key = "User:"+userId+":carts";

            Map<String,String> map = new HashMap<>();
            for (OmsCartItem cartItem : omsCartItems) {
                String k = cartItem.getProductSkuId();
                String v = JSON.toJSONString(cartItem);
                map.put(k,v);
            }

            Jedis jedis = redisUtil.getJedis();
            jedis.hmset(key,map);

            jedis.close();
        }
    }
}
