package com.cppba.wbpd;

import static java.net.HttpURLConnection.HTTP_OK;

import java.io.File;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.cppba.wbpd.util.HttpUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @description: 微博图床上传工具
 * @author: winfed
 * @create: 2019-03-11 17:05
 **/
@Slf4j
public class WbpdClient {
    private String              username      = "pb";
    private String              password      = "BRo62INvQp8ws0fN";

    private final static Long   EXPIRE_MINUTE = 30L;

    private final static String LOGIN_URL     = "https://login.sina.com.cn/sso/login.php";

    // 缓存登录信息
    private Map<String, Object> loginCache    = new ConcurrentHashMap<>();

    ////////////////////////////////////////////////////////////////////
    /////////// 上传相关
    ////////////////////////////////////////////////////////////////////
    public void upload(File image) throws Exception {
        // 检测登录
        checkLogin();
    }

    ////////////////////////////////////////////////////////////////////
    /////////// 登录相关
    ////////////////////////////////////////////////////////////////////

    private void checkLogin() throws Exception {
        if (loginCache.get("cookie") == null) {
            login();
        } else {
            // 是否过期
            Long expireTime = (Long) loginCache.get("expireTime");
            if (expireTime < System.currentTimeMillis()) {
                login();
            }
        }
    }

    private void login() throws Exception {
        HttpUtils.HttpResponse httpResponse = HttpUtils.doPost(LOGIN_URL, buildLoginHeader(), buildLoginParam());
        if (httpResponse.getStatusCode() == HTTP_OK) {

            String cookie = httpResponse.getHeader().get("set-cookie");
            if (cookie == null) {
                cookie = httpResponse.getHeader().get("Set-Cookie");
            }
            log.debug("login cookie result: \n" + cookie);
            if (cookie == null) {
                throw new Exception("登陆失败，无法获取cookie");
            }
            if (cookie.length() < 50) {
                throw new Exception("登陆失败，大概是用户名密码不正确大概是需要输入验证码了。" + "由于不知道为何读取返回的body时候乱码，无法解决，所以无法具体说出什么原因。");
            }
            log.debug("登陆成功,cookie:--->\n\n" + cookie + "\n");
            log.debug("登陆成功！获取cookie成功!");
            // 存入cookie
            loginCache.put("cookie", cookie);
            loginCache.put("expireTime", TimeUnit.MINUTES.toMillis(EXPIRE_MINUTE) + System.currentTimeMillis());
        } else {
            throw new Exception("登录失败，原因: " + httpResponse.getBody());
        }
    }

    /**
     * 构建登录参数
     * 
     * @return
     */
    private Map<String, String> buildLoginParam() {
        Map<String, String> params = new HashMap<>();
        params.put("client", "ssologin.js(v1.4.15)");
        // todo
        params.put("_", "1403138799543");
        params.put("entry", "sso");
        params.put("gateway", "1");
        params.put("from", "");
        params.put("savestate", "30");
        params.put("useticket", "0");
        params.put("pagerefer", "");
        params.put("vsnf", "1");
        params.put("su", Base64.getEncoder().encodeToString(username.getBytes()));
        params.put("service", "sso");
        params.put("sp", password);
        params.put("sr", "1920*1080");
        params.put("encoding", "UTF-8");
        params.put("cdult", "3");
        params.put("domain", "sina.com.cn");
        params.put("prelt", "0");
        params.put("returntype", "TEXT");
        return params;
    }

    /**
     * 构建登录头
     * 
     * @return
     */
    private Map<String, String> buildLoginHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("Referer", "https://weibo.com/");
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.109 Safari/537.36");
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("Accept", "text/html,application/xhtml+xm…plication/xml;q=0.9,*/*;q=0.8");
        header.put("Accept-Encoding", "gzip, deflate, br");
        header.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
        return header;
    }
}
