package com.example.testdemo.mapper;

import com.example.testdemo.entity.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Insert;

import java.util.List;

//声明一下这个接口是操作数据库的
@Mapper
public interface UserMapper {

    // select * 查找所有字段
    // from user 是从哪个表查
    // where id = #{id}是筛选条件
    // #{id} 是传入的参数
    @Select("select * from user where id = #{id}")
    User findById(Long id);// 可以传这个参数或者不传 integer属于对象类型 int属于基本数据类型
    @Select("select * from user")
    List<User> findAll();

    // 在数据库中新增一条数据
    @Insert(""" 
insert into user(name,phone)
    value(#{name},#{phone})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user); //成功返回1 失败返回0

    // 根据id删除用户
    @Delete("delete from user where id = #{id}")

    //若依框架
    int delete(Long id);

    @Update("""
update user
set name = #{name},
    phone = #{phone}
    where id =#{id}
    """)
    int update(User user);
}
