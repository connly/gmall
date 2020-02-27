package com.project.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.project.gmall.bean.OmsCartItem;
import com.project.gmall.bean.PmsSkuInfo;
import com.project.gmall.service.CartService;
import com.project.gmall.service.SkuService;
import com.project.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CartController {
    @Reference
    SkuService skuService;

    @Reference
    CartService cartService;

    /**
     *  跳转到结算成功页面
     * @return
     */
    @RequestMapping("toTrade")
    public String toTrade(){
        //
        return "tradeSuccess";
    }


    /**
     *  多选框，确定选中状态
     * @param skuId
     * @param isChecked
     * @param request
     * @param modelMap
     * @return
     */
    @RequestMapping("checkCart")
    public String checkCart(String skuId, String isChecked, HttpServletRequest request,HttpServletResponse response, ModelMap modelMap){
        String userId = "1";

        List<OmsCartItem> omsCartItems = new ArrayList<>();
        // 判断用户是否已经登录
        if(StringUtils.isNotBlank(userId)){
            // 用户已经登录,修改db并同步缓存
            cartService.updateChecked(userId,skuId,isChecked);
            // 获得购物车集合
            omsCartItems = cartService.getCartsByUserId(userId);
        }else{
            // 没有登录，修改cookie
            String cartListCookieStr = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if(StringUtils.isNotBlank(cartListCookieStr)){
                omsCartItems = JSON.parseArray(cartListCookieStr, OmsCartItem.class);

                for (OmsCartItem omsCartItem : omsCartItems) {
                    if(omsCartItem.getProductSkuId().equals(skuId)){
                        omsCartItem.setIsChecked(isChecked);
//                        omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
                    }
                }
                // 修改完成，实现cookie覆盖
                CookieUtil.setCookie(request,response,"cartListCookie",JSON.toJSONString(omsCartItems),60*60,true);
             }
        }
        // 将购物车集合返回给页面
        modelMap.put("cartList",omsCartItems);

        // 总价格计算
        if(omsCartItems != null && omsCartItems.size()>0 ) {
            BigDecimal sum = getSum(omsCartItems);
            modelMap.put("sum", sum);
        }

        return "cartListInner";
    }

    // 总价格计算方法
    private BigDecimal getSum(List<OmsCartItem> omsCartItems) {
        BigDecimal sum = new BigDecimal("0");
        if(omsCartItems != null && omsCartItems.size()>0 ) {
            for (OmsCartItem omsCartItem : omsCartItems) {
                if (omsCartItem.getIsChecked().equals("1")) {
                    sum = sum.add(omsCartItem.getQuantity().multiply(omsCartItem.getPrice()));
                }
            }
        }
        return sum;
    }

    // 购物车列表
    @RequestMapping("cartList")
    public String cartList(HttpServletRequest request, ModelMap modelMap){
        String userId = "1";

        // 创建购物车集合，共下面分支使用
        List<OmsCartItem> cartItemList =  new ArrayList();

        if(StringUtils.isNotBlank(userId)) {
            // 用户已经登录
            cartItemList = cartService.getCartsByUserId(userId);
        }else{
            // 用户未登录
            String cartListCookieStr = CookieUtil.getCookieValue(request, "cartListCookie", true);

            if(StringUtils.isNotBlank(cartListCookieStr)){
                cartItemList = JSON.parseArray(cartListCookieStr,OmsCartItem.class);
//                for (OmsCartItem omsCartItem : cartItemList) {
//                    BigDecimal multiply = omsCartItem.getPrice().multiply(omsCartItem.getQuantity());
//                    omsCartItem.setTotalPrice(multiply);
//                }
            }
        }
        modelMap.put("cartList",cartItemList);

        if(cartItemList != null && cartItemList.size()>0 ) {
            BigDecimal sum = getSum(cartItemList);
            modelMap.put("sum", sum);
        }

        return "cartList";
    }

    // 添加购物车
    @RequestMapping("addToCart")
    public String addToCart(HttpServletRequest request, HttpServletResponse response, String skuId, BigDecimal quantity){
        // sku商品数据转换为购物车数据
        PmsSkuInfo skuInfo = skuService.getSkuBySkuId(skuId);
        if(skuInfo!=null ) {
            OmsCartItem omsCartItem = new OmsCartItem();

            // 将选中的商品数据添加到购物车对象中
            omsCartItem.setProductSkuId(skuId);
            omsCartItem.setProductId(skuInfo.getProductId());
            omsCartItem.setProductCategoryId(skuInfo.getCatalog3Id());
            omsCartItem.setProductName(skuInfo.getSkuName());
            omsCartItem.setProductPic(skuInfo.getSkuDefaultImg());
            omsCartItem.setQuantity(quantity);
            omsCartItem.setPrice(skuInfo.getPrice());
            omsCartItem.setCreateDate(new Date());
            omsCartItem.setIsChecked("1");

            // 创建购物车集合，供下面所有分支使用
            List<OmsCartItem> cartItemlist = new ArrayList<>();

            // 判断用户是否登录
            String userId = "1";  // 测试使用的假数据

            if(StringUtils.isNotBlank(userId)){
                // 用户已登录，存储到DB
                omsCartItem.setMemberId(userId);
                // 返回当前用户添加当前商品的对象,有对象代表添加过当前商品
                OmsCartItem cartItemFromUser = cartService.isCartEmpty(userId,skuId);

                if(cartItemFromUser != null){
                    // 更新
                    // 更改数量
                    cartItemFromUser.setQuantity(cartItemFromUser.getQuantity().add(quantity));
                    cartService.update(cartItemFromUser);
                } else {
                    // 添加
                    cartService.add(omsCartItem);
                }

            } else{
            /*// 用户未登录，存储到cookie
            Cookie[] cookies = request.getCookies();
            // 购物车集合的cookie的json字符串
            Cookie cookie = new Cookie("cartListCookie", "json");
            // 设置过期时间
            cookie.setMaxAge(1000*60*60);*/

                // 用户未登录，存储到cookie
                String cartListCookieStr = CookieUtil.getCookieValue(request, "cartListCookie", true);

                // 是否存在购物车集合cookie字符串
                if(StringUtils.isNotBlank(cartListCookieStr)){
                    // 更新购物车集合的json字符串到cookie中
                    // 判断添加的数据和购物车的某条数据是否重复
                    cartItemlist = JSON.parseArray(cartListCookieStr, OmsCartItem.class);

                    if(newCart(cartItemlist,omsCartItem)){
                        // 重复了，修改购物车该商品数量
                        for (OmsCartItem cartItem : cartItemlist) {
                            String productSkuId = cartItem.getProductSkuId();
                            if(productSkuId.equals(omsCartItem.getProductSkuId())){
                                cartItem.setQuantity(cartItem.getQuantity().add(quantity));
                            }
                        }
                    }else{
                        // 不重复，将该数据添加到购物车集合中去
                        cartItemlist.add(omsCartItem);
                    }

                } else{
                    // 添加购物车集合的json字符串到cookie中
                    // 购物车数据放入购物车集合中去
                    cartItemlist.add(omsCartItem);

                }
                // 覆盖cookie
                CookieUtil.setCookie(request,response, "cartListCookie",
                        JSON.toJSONString(cartItemlist),60*60,true);
            }
        }

        // 重定向页面的时候向页面传递商品数据(静态)
        return "redirect:/success.html";
    }

    /**
     * 判断当前商品在购物车商品集合中是否存在，并返回结果
     * @param omsCartItems
     * @param omsCartItem
     * @return
     */
    private boolean newCart(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem){
        for (OmsCartItem cartItem : omsCartItems) {
            if(cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())){
                // 重复
                return true;
            }
        }
        // 不存在
        return false;
    }
}
