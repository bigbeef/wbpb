package com.cppba.wbpd.controller;

import com.cppba.wbpd.WbpdClient;
import com.cppba.wbpd.response.UploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description: 图床控制器
 * @author: winfed
 * @create: 2019-03-13 10:53
 **/
@RestController
@RequestMapping("/wbpb")
@Slf4j
public class WbpbController {

    @PostMapping("/uploadWithUsername")
    public UploadResponse UploadResponse(MultipartFile image, String username, String password) {
        if (image == null || StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return null;
        }
        try {
            return WbpdClient.upload(image.getBytes(), username, password);
        } catch (Exception e) {
            log.error("image读取异常！", e);
        }
        return null;
    }
}
