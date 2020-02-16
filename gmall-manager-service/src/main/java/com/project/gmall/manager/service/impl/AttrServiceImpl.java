package com.project.gmall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.project.gmall.bean.PmsBaseAttrInfo;
import com.project.gmall.bean.PmsBaseAttrValue;
import com.project.gmall.manager.mapper.PmsBaseAttrInfoMapper;
import com.project.gmall.manager.mapper.PmsBaseAttrValueMapper;
import com.project.gmall.service.AttrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AttrServiceImpl implements AttrService{
    @Autowired
    PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;

    @Autowired
    PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Override
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id) {
        PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();
        pmsBaseAttrInfo.setCatalog3Id(catalog3Id);
        return pmsBaseAttrInfoMapper.select(pmsBaseAttrInfo);
    }

    // 插入平台属性
    @Override
    public void saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
        //
        String id = pmsBaseAttrInfo.getId();
        List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();

        if (StringUtils.isNotBlank(id)) {
            // 修改操作
            pmsBaseAttrInfoMapper.updateByPrimaryKeySelective(pmsBaseAttrInfo);
            pmsBaseAttrInfo.setId(id);
            for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                pmsBaseAttrValueMapper.delete(pmsBaseAttrValue);
            }
        }else {
            // 插入一条平台属性
            pmsBaseAttrInfoMapper.insertSelective(pmsBaseAttrInfo);
            // 主键生成策略
            id = pmsBaseAttrInfo.getId();
        }
        for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
            // 填入对应的平台属性id
            pmsBaseAttrValue.setAttrId(id);
            // 插入对应的多条平台属性值
            pmsBaseAttrValueMapper.insertSelective(pmsBaseAttrValue);
        }
    }

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
        pmsBaseAttrValue.setAttrId(attrId);

        return pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
    }
}
