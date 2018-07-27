package com.cn.BBSAutoRelay.service;

import com.cn.BBSAutoRelay.model.User;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * Created by Administrator on 2018/4/19.
 */
public interface UserService {

    int addUser(User user);

    PageInfo<User> findAllUser(int pageNum, int pageSize);
}
