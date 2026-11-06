package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginContext;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatProperties properties;

    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        // 调用微信登录接口拿到用户信息openid
        String json = getAccessToken(userLoginDTO.getCode());
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");

        if (openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        // 判断用户是否已经在数据库中存在
        User user = userMapper.getByOpenid(openid);
        if (user == null){
            // 如果不存在则在数据库新增用户
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insertUser(user);
        }
        return user;
    }


    // 调用微信登录接口，获取access_token和openid
    private String getAccessToken(String code) {
        CloseableHttpClient client = HttpClients.createDefault();
        HashMap<String, String> param = new HashMap<>();
        param.put("appid", properties.getAppid());
        param.put("secret", properties.getSecret());
        param.put("js_code", code);
        param.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN_URL, param);
        return json;
    }
}
