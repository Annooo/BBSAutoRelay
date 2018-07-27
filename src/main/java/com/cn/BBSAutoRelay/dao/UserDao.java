package com.cn.BBSAutoRelay.dao;

import com.cn.BBSAutoRelay.model.User;

import java.util.List;

public interface UserDao {


    int insert(User record);



    List<User> selectUsers();
}
