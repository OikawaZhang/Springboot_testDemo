package com.example.testdemo.controller;

import com.example.testdemo.entity.User;
import com.example.testdemo.service.UserService;
import com.example.testdemo.utils.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id){

        try {

            User user = userService.getUserById(id);

            return Result.success(user);

        }catch(RuntimeException e){

            return Result.fail(e.getMessage());

        }
    }

    @GetMapping("/list")
    public Result<List<User>> getAllUsers(){
        try {

            List<User> users = userService.getAllUsers();

            return Result.success(users);

        } catch(RuntimeException e){

            return Result.fail(e.getMessage());

        }
    }

    //@requestbody可以将前端输入自动转换为目标对象
    @PostMapping
    public Result<User> addUser(@RequestBody User user){

        try {
            userService.addUser(user);
            return Result.success(user);
        }catch(RuntimeException e){
            return Result.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<User> deleteUser(@PathVariable Long id){

        try {
            userService.deleteUser(id);
            return Result.success(null);
        }catch(RuntimeException e){
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping
    public Result<User> updateUser(@RequestBody User user){

        try {
            userService.updateUser(user);
            return Result.success(null);
        }catch(RuntimeException e){
            return Result.fail(e.getMessage());
        }
    }
}
