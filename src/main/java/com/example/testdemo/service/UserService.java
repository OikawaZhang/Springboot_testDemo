package com.example.testdemo.service;

import com.example.testdemo.entity.User;

import java.util.List;

//复杂业务中 这里需要判断权限 处理数据等等 mapper只负责和数据库交互
public interface UserService {

    User getUserById(Long id);
    List<User> getAllUsers();

    void addUser(User user);

    void deleteUser(Long id);

    void updateUser(User user);
}
