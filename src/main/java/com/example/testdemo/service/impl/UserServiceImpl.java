package com.example.testdemo.service.impl;

import com.example.testdemo.entity.User;
import com.example.testdemo.mapper.UserMapper;
import com.example.testdemo.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User getUserById(Long id){
        User user = userMapper.findById(id);


        if(user == null){

            throw new RuntimeException("用户不存在");

        }


        return user;
    }

    @Override
    public List<User> getAllUsers(){
        return userMapper.findAll();
    }

    @Override
    public void addUser(User user){

        if(user.getName() == null){
            throw new RuntimeException("用户名不能为空");
        }

        int result = userMapper.insert(user);

        if(result == 0){
            throw new RuntimeException("添加失败");
        }
    }

    @Override
    public void deleteUser(Long id){

        // 先根据查找查找方法 找到这个用户
        User user = userMapper.findById(id);

        if(user == null){
            throw new RuntimeException("用户不存在");
        }

        //再调用删除方法 删除该用户
        int result = userMapper.delete(id);

        if(result == 0){
            throw new RuntimeException("删除失败");
        }
    }

    @Override
    public void updateUser(User user){

        User oldUser = userMapper.findById(user.getId());

        if(oldUser == null){
            throw new RuntimeException("用户不存在");
        }

        int result = userMapper.update(user);

        if(result == 0){
            throw new RuntimeException("修改失败");
        }
    }
}
