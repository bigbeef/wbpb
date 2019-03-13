package com.cppba.wbpd.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description: 登录缓存类
 * @author: winfed
 * @create: 2019-03-13 10:59
 **/
@Data
@AllArgsConstructor
public class LoginCache {
    private Long expireTime;
    private String cookie;
}
