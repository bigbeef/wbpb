package com.cppba.wbpd.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @description: 上传结果
 * @author: winfed
 * @create: 2019-03-12 17:35
 **/
@Data
public class UploadResponse {
    @JsonProperty("code")
    private String code;

    @JsonProperty("data")
    private ResponseData responseData;

    private String url;

    public String getUrl() {
        return "https://ws1.sinaimg.cn/large/" + responseData.getPics().getPic1().getPid() +".jpg";
    }

    @Data
    public static class ResponseData {

        @JsonProperty("data")
        private String data;

        @JsonProperty("count")
        private int count;

        @JsonProperty("pics")
        private Pics pics;

        @Data
        public static class Pics {
            @JsonProperty("pic_1")
            private Pic1 pic1;

            @Data
            public static class Pic1 {

                @JsonProperty("ret")
                private int ret;

                @JsonProperty("size")
                private int size;

                @JsonProperty("width")
                private int width;

                @JsonProperty("name")
                private String name;

                @JsonProperty("pid")
                private String pid;

                @JsonProperty("height")
                private int height;
            }
        }
    }
}
