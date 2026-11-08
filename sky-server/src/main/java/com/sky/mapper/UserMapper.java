package com.sky.mapper;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    public void insertUser(User user);

    public User getByOpenid(String openid);

    public User getById(Long id);
}
