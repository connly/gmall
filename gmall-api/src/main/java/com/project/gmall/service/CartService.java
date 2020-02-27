package com.project.gmall.service;

import com.project.gmall.bean.OmsCartItem;

import java.util.List;

public interface CartService {
    OmsCartItem isCartEmpty(String userId, String skuId);

    void update(OmsCartItem cartItemFromUser);

    void add(OmsCartItem omsCartItem);

    List<OmsCartItem> getCartsByUserId(String userId);

    void updateChecked(String userId, String skuId, String isChecked);
}
