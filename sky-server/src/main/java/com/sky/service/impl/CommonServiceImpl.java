package com.sky.service.impl;

import com.sky.result.Result;
import com.sky.service.CommonService;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class CommonServiceImpl implements CommonService {

    @Autowired
    private AliOssUtil aliOssUtil;
    @Override
    public String upload(MultipartFile file) {
        String filename = file.getOriginalFilename();
        // 截取文件的后缀名
        String extName = filename.substring(filename.lastIndexOf("."));
        String objectName = UUID.randomUUID().toString() + extName;
        try {
            return aliOssUtil.upload(file.getBytes(), objectName);
        } catch (IOException e) {
            log.info("文件上传失败");
        }
        return null;
    }
}
