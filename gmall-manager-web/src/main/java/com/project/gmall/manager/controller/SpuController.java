package com.project.gmall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.project.gmall.PathUrls.UrlMenu;
import com.project.gmall.bean.PmsBaseSaleAttr;
import com.project.gmall.bean.PmsProductImage;
import com.project.gmall.bean.PmsProductInfo;
import com.project.gmall.bean.PmsProductSaleAttr;
import com.project.gmall.service.SpuService;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@CrossOrigin
public class SpuController {
    @Reference
    SpuService spuService;

    @RequestMapping("spuImageList")
    @ResponseBody
    public List<PmsProductImage> spuImageList(String spuId){
        return spuService.spuImageList(spuId);
    }

    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId){
        return spuService.spuSaleAttrList(spuId);
    }


        @RequestMapping("spuList")
    @ResponseBody
    public List<PmsProductInfo> spuList(String catalog3Id){
        return spuService.spuList(catalog3Id);
    }

    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<PmsBaseSaleAttr> baseSaleAttrList(){
        return spuService.baseSaleAttrList();
    }

    @RequestMapping("saveSpuInfo")
    @ResponseBody
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){
        spuService.saveSpuInfo(pmsProductInfo);
        return "seccess";
    }

    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile){

        // 1.连接trackerServer
        String conf_filename = SpuController.class.getClassLoader().getResource("tracker.conf").getPath();
        try {
            ClientGlobal.init(conf_filename);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = null;
        try {
            trackerServer = trackerClient.getTrackerServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 2.通过trackerServer获取可用的storageServer地址
        StorageClient storageClient = new StorageClient(trackerServer, null);

        String imageUrl = UrlMenu.VMPath;
        // 3.向storageServer上传文件(ext_name:后缀名)
        try {
            // 获取multipartFile的后缀名
            String originalFilename = multipartFile.getOriginalFilename();
            // 最后一个.开始，到最后一个字符结束
            int i = originalFilename.lastIndexOf(".");
            String ext = originalFilename.substring(i + 1);

            String[] urls = storageClient.upload_file(multipartFile.getBytes(), ext, null);

            // 4.storageServer返回存储地址给页面
            for (String url : urls) {
                imageUrl += "/" + url;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }

        return imageUrl;
    }

}