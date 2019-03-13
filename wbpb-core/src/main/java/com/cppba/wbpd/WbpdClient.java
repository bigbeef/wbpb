package com.cppba.wbpd;

import com.cppba.wbpd.response.UploadResponse;
import com.cppba.wbpd.util.HttpUtils;
import com.cppba.wbpd.util.JsonUtils;
import com.cppba.wbpd.util.WbpdUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @description: 微博图床上传工具
 * @author: winfed
 * @create: 2019-03-11 17:05
 **/
@Slf4j
public class WbpdClient {

    /**
     * 向微博图床上传图片
     *
     * @param imageData
     * @return
     * @throws Exception
     */
    public static UploadResponse upload(byte[] imageData, String username, String password) throws Exception {
        if (imageData.length == 0) {
            throw new Exception("图片不能为空！");
        }
        // 检测登录
        WbpdUtils.checkLogin(username, password);

        String base64Image = Base64.getEncoder().encodeToString(imageData);
        HttpUtils.HttpResponse httpResponse = HttpUtils.doPostMultiPart(WbpdUtils.UPLOAD_URL, WbpdUtils.buildUploadHeader(username), WbpdUtils.buildUploadParam(), null, base64Image);
        // 如果返回的不是200,则直接上传就失败了
        if (httpResponse.getStatusCode() != HTTP_OK) {
            log.error("上传失败");
        }
        String bodyJson = WbpdUtils.parseBodyJson(httpResponse.getBody());
        return JsonUtils.jsonToBean(bodyJson, UploadResponse.class);
    }


}
