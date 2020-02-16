package com.project.gmall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.project.gmall.bean.PmsBaseAttrInfo;
import com.project.gmall.bean.PmsBaseAttrValue;
import com.project.gmall.service.AttrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin
public class AttrController {

    @Reference
    AttrService attrService;

    // 修改平台属性
    @RequestMapping("getAttrValueList")
    @ResponseBody
    public List<PmsBaseAttrValue> getAttrValueList(String attrId){
      return attrService.getAttrValueList(attrId);
    }

    // 新增平台属性
    // 属性包裹属性值的json数据
    @RequestMapping("saveAttrInfo")
    @ResponseBody
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo){
        attrService.saveAttrInfo(pmsBaseAttrInfo);
        return "success";
    }

    // 根据三级分类列出对应的平台属性
    @RequestMapping("attrInfoList")
    @ResponseBody
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id){

        return attrService.attrInfoList(catalog3Id);
    }

}
