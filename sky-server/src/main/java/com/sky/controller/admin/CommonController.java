package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.CommonService;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RestController
@RequestMapping("/admin/common")
public class CommonController {

    @Autowired
    private CommonService commonService;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        String url = commonService.upload(file);
        if (url != null){
            return Result.success(url);
        }
        return Result.error("文件上传失败");
    }
}
