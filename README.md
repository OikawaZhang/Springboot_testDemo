# 🌱 Spring Boot 小白入门教程 —— 从零搭建一个用户管理系统

> **适合人群**：完全没接触过 Spring Boot 的新手
> **最终目标**：搭建一个完整的用户增删改查（CRUD）后端服务，并理解企业项目的设计思路
> **技术栈**：Spring Boot 4.0 + MyBatis + MySQL + Lombok

---

## 📋 目录

### 第一部分：从零搭建（已完成的步骤）

1. [准备工作](#1-准备工作)
2. [第一步：创建项目](#2-第一步创建项目)
3. [第二步：配置数据库连接](#3-第二步配置数据库连接)
4. [第三步：创建实体类 Entity](#4-第三步创建实体类-entity)
5. [第四步：创建数据访问层 Mapper](#5-第四步创建数据访问层-mapper)
6. [第五步：创建业务逻辑层 Service](#6-第五步创建业务逻辑层-service)
7. [第六步：创建控制器 Controller](#7-第六步创建控制器-controller)
8. [第七步：统一返回结果 Result](#8-第七步统一返回结果-result)
9. [第八步：测试接口](#9-第八步测试接口)

### 第二部分：排坑指南

10. [踩坑一：Lombok 报错 "requires enabled annotation processing"](#踩坑一lombok-报错-requires-enabled-annotation-processing)
11. [踩坑二：查不到用户时返回 200 success(null) 而不是 500 fail](#踩坑二查不到用户时返回-200-successnull-而不是-500-fail)
12. [踩坑三：新增用户后返回的 id 是 null](#踩坑三新增用户后返回的-id-是-null)

### 第三部分：深入理解

13. [Controller 和 Service 的职责到底怎么分？](#controller-和-service-的职责到底怎么分)
14. [三层架构：请求是如何流转的？](#三层架构请求是如何流转的)
15. [MyBatis 和 MyBatis-Plus 有什么区别？](#mybatis-和-mybatis-plus-有什么区别)
16. [QueryWrapper 是什么？为什么出现在 ServiceImpl 里？](#querywrapper-是什么为什么出现在-serviceimpl-里)

### 第四部分：企业项目进阶

17. [Swagger 接口文档注解详解](#swagger-接口文档注解详解)
18. [后续学习路线图](#后续学习路线图)

### 附录

19. [项目结构总览](#项目结构总览)
20. [常见问题 FAQ](#常见问题-faq)

---

# 第一部分：从零搭建

---

## 1. 准备工作

在开始之前，你需要安装以下工具：

| 工具 | 作用 | 下载地址 |
|------|------|----------|
| **JDK 17** | Java 运行环境 | [Adoptium 下载](https://adoptium.net/) |
| **IntelliJ IDEA** | 写代码的 IDE | [JetBrains 下载](https://www.jetbrains.com/idea/download/) （社区版免费） |
| **MySQL 8.0+** | 数据库 | [MySQL 下载](https://dev.mysql.com/downloads/mysql/) |
| **Git** | 版本控制 | [Git 下载](https://git-scm.com/downloads) |
| **Postman** 或 **HTTP 文件** | 测试接口的工具 | [Postman 下载](https://www.postman.com/downloads/) |

### 安装后验证

打开终端（命令提示符 / PowerShell），依次输入以下命令确认安装成功：

```bash
# 检查 Java 版本（应该显示 17.x.x）
java -version

# 检查 MySQL
mysql -u root -p

# 检查 Git
git --version
```

---

## 2. 第一步：创建项目 ✅

### 使用 IntelliJ IDEA 创建（推荐新手）

1. 打开 IntelliJ IDEA，点击 **New Project**
2. 左侧选择 **Spring Initializr**
3. 填写以下信息：

| 配置项 | 值 | 说明 |
|--------|-----|------|
| Name | `testdemo` | 项目名称 |
| Location | `D:\Ideal\CODE\testdemo` | 项目存放路径 |
| Language | **Java** | 编程语言 |
| Type | **Maven** | 项目构建工具 |
| Group | `com.example` | 组织标识 |
| Artifact | `testdemo` | 项目标识 |
| Package name | `com.example.testdemo` | 包名 |
| JDK | **17** | Java 版本 |
| Packaging | **Jar** | 打包方式 |

4. 点击 **Next**，勾选以下依赖：

| 依赖名称 | 作用 |
|----------|------|
| **Spring Web** (`spring-boot-starter-webmvc`) | 写 RESTful API |
| **MyBatis Framework** (`mybatis-spring-boot-starter`) | 操作数据库 |
| **MySQL Driver** (`mysql-connector-j`) | 连接 MySQL |
| **Lombok** (`lombok`) | 自动生成 getter/setter |

5. 点击 **Create**，IDEA 自动下载依赖并生成项目结构。

### 启动类解读

```java
// src/main/java/com/example/testdemo/TestdemoApplication.java

@SpringBootApplication  // = @Configuration + @EnableAutoConfiguration + @ComponentScan
public class TestdemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestdemoApplication.class, args);  // 启动！
    }
}
```

> **💡 `@SpringBootApplication` 会自动扫描 `com.example.testdemo` 包及其子包下所有带注解的类**（如 `@Controller`、`@Service`、`@Mapper`）。这就是为什么后面的 Controller、Service 都要放在这个包下面。

---

## 3. 第二步：配置数据库连接 ✅

### 3.1 创建数据库和表

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS springboot_test DEFAULT CHARACTER SET utf8mb4;

USE springboot_test;

-- 创建用户表（⚠️ id 必须设为 AUTO_INCREMENT）
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID，自增主键',
    name VARCHAR(50) NOT NULL COMMENT '用户名',
    phone VARCHAR(20) COMMENT '手机号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 3.2 配置 application.properties

```properties
spring.application.name=testdemo

# 数据库连接（改成你自己的用户名和密码！）
spring.datasource.url=jdbc:mysql://localhost:3306/springboot_test
spring.datasource.username=root
spring.datasource.password=你的密码
server.port=8081

# MyBatis 配置
mybatis.mapper-locations=classpath:mapper/*.xml
```

| 配置项 | 含义 |
|--------|------|
| `spring.datasource.url` | `localhost:3306` 是本机 MySQL，`springboot_test` 是数据库名 |
| `server.port` | 应用端口，默认 8080，这里改成 8081 |

---

## 4. 第三步：创建实体类 Entity ✅

Entity（实体类）对应数据库中的一张表，**一个 Java 对象 = 表中的一行数据**。

```java
// src/main/java/com/example/testdemo/entity/User.java

package com.example.testdemo.entity;

public class User {
    private Long id;       // 对应数据库的 id 字段
    private String name;   // 对应数据库的 name 字段
    private String phone;  // 对应数据库的 phone 字段

    // getter / setter（如果用 Lombok 的 @Data 可以省略）
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
```

> 💡 **Lombok `@Data` 做了什么？**  
> 它会在编译时自动生成 `getXxx()`、`setXxx()`、`toString()`、`equals()`、`hashCode()` 方法。  
> 也就是说下面两段代码效果一样：
> ```java
> // 手写版（50+ 行）
> public class User {
>     private Long id;
>     public Long getId() { return id; }
>     public void setId(Long id) { this.id = id; }
>     // ... 还有 name、phone 的 getter/setter，以及 toString、equals、hashCode
> }
>
> // Lombok 版（5 行）
> @Data
> public class User {
>     private Long id;
>     private String name;
>     private String phone;
> }
> ```

---

## 5. 第四步：创建数据访问层 Mapper ✅

Mapper 是**和数据库直接打交道的层**，负责执行 SQL 语句。

```java
// src/main/java/com/example/testdemo/mapper/UserMapper.java

package com.example.testdemo.mapper;

import com.example.testdemo.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper  // 告诉 MyBatis：这是一个操作数据库的接口
public interface UserMapper {

    @Select("select * from user where id = #{id}")
    User findById(Long id);

    @Select("select * from user")
    List<User> findAll();

    @Insert("insert into user(name, phone) values(#{name}, #{phone})")
    @Options(useGeneratedKeys = true, keyProperty = "id")  // ⭐ 自动回填自增ID
    int insert(User user);

    @Delete("delete from user where id = #{id}")
    int delete(Long id);

    @Update("update user set name = #{name}, phone = #{phone} where id = #{id}")
    int update(User user);
}
```

**注解速查表**：

| 注解 | 作用 | 返回值含义 |
|------|------|-----------|
| `@Mapper` | 声明这是 MyBatis 映射接口 | — |
| `@Select` | 执行 SELECT | 查询结果（对象或集合） |
| `@Insert` | 执行 INSERT | `int`：1=成功，0=失败 |
| `@Delete` | 执行 DELETE | `int`：1=成功，0=失败 |
| `@Update` | 执行 UPDATE | `int`：1=成功，0=失败 |
| `@Options(useGeneratedKeys=true, keyProperty="id")` | 数据库生成的自增 id 自动填回 User 对象的 id 属性 | — |

> **💡 `#{xxx}` 是什么？**  
> MyBatis 的参数占位符，`#{id}` 会被替换成方法参数值，并自动加引号防 SQL 注入。

> **⚠️ `@Options` 为什么重要？**  
> 没有它，插入数据后 `user.getId()` 返回 `null`，虽然数据库里已经有 id 了。详见[踩坑三](#踩坑三新增用户后返回的-id-是-null)。

---

## 6. 第五步：创建业务逻辑层 Service ✅

Service 层在 Controller 和 Mapper 之间，负责**处理业务逻辑**（校验数据、判断权限等）。

### 6.1 Service 接口

```java
// src/main/java/com/example/testdemo/service/UserService.java

package com.example.testdemo.service;

import com.example.testdemo.entity.User;
import java.util.List;

public interface UserService {
    User getUserById(Long id);
    List<User> getAllUsers();
    void addUser(User user);
    void deleteUser(Long id);
    void updateUser(User user);
}
```

### 6.2 Service 实现类（⭐ 核心逻辑在这里）

```java
// src/main/java/com/example/testdemo/service/impl/UserServiceImpl.java

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
    public User getUserById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");  // ⭐ 这里必须抛异常！
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    @Override
    public void addUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        int result = userMapper.insert(user);
        if (result == 0) {
            throw new RuntimeException("添加失败");
        }
    }

    @Override
    public void deleteUser(Long id) {
        User user = userMapper.findById(id);      // 先查
        if (user == null) {
            throw new RuntimeException("用户不存在"); // 不存在就抛异常
        }
        int result = userMapper.delete(id);        // 再删
        if (result == 0) {
            throw new RuntimeException("删除失败");
        }
    }

    @Override
    public void updateUser(User user) {
        User oldUser = userMapper.findById(user.getId());  // 先查旧数据
        if (oldUser == null) {
            throw new RuntimeException("用户不存在");
        }
        int result = userMapper.update(user);              // 再更新
        if (result == 0) {
            throw new RuntimeException("修改失败");
        }
    }
}
```

> **⚠️ 关键点：Service 里必须 `throw new RuntimeException()`**  
> 如果 Mapper 返回 null，Service 不抛异常直接 return null，Controller 就会执行 `Result.success(null)`，返回 200 成功而不是 500 失败。详见[踩坑二](#踩坑二查不到用户时返回-200-successnull-而不是-500-fail)。

---

## 7. 第六步：创建控制器 Controller ✅

Controller 是**对外暴露 API 的入口**，浏览器/Postman 访问的就是这里。

```java
// src/main/java/com/example/testdemo/controller/UserController.java

package com.example.testdemo.controller;

import com.example.testdemo.entity.User;
import com.example.testdemo.service.UserService;
import com.example.testdemo.utils.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController              // = @Controller + @ResponseBody
@RequestMapping("/user")     // 所有接口以 /user 开头
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return Result.success(user);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return Result.success(users);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping
    public Result<User> addUser(@RequestBody User user) {
        try {
            userService.addUser(user);
            return Result.success(user);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<User> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping
    public Result<User> updateUser(@RequestBody User user) {
        try {
            userService.updateUser(user);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }
}
```

### RESTful API 设计规范

| HTTP 方法 | 路径 | 作用 | 示例 |
|-----------|------|------|------|
| `GET` | `/user/{id}` | 查询单个用户 | `GET /user/1` |
| `GET` | `/user/list` | 查询所有用户 | `GET /user/list` |
| `POST` | `/user` | 新增用户 | `POST /user` + JSON body |
| `PUT` | `/user` | 更新用户 | `PUT /user` + JSON body |
| `DELETE` | `/user/{id}` | 删除用户 | `DELETE /user/1` |

### 常用注解解释

| 注解 | 作用 |
|------|------|
| `@RestController` | `@Controller` + `@ResponseBody`，返回值自动转 JSON |
| `@RequestMapping("/user")` | 设置基础路径 |
| `@GetMapping` / `@PostMapping` / `@PutMapping` / `@DeleteMapping` | 对应 HTTP 的 GET/POST/PUT/DELETE |
| `@PathVariable` | 从 URL 路径中取值（`/user/{id}` → id） |
| `@RequestBody` | 把请求体中的 JSON 自动转成 Java 对象 |

---

## 8. 第七步：统一返回结果 Result ✅

让前端能统一处理响应，而不是每个接口返回格式都不一样。

```java
// src/main/java/com/example/testdemo/utils/Result.java

package com.example.testdemo.utils;

import lombok.Data;

@Data
public class Result<T> {      // <T> 是泛型，可以包装任意类型

    private Integer code;     // 200=成功，500=失败
    private String message;   // 提示信息
    private T data;           // 返回的数据

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "success";
        result.data = data;
        return result;
    }

    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.code = 500;
        result.message = message;
        result.data = null;
        return result;
    }
}
```

**返回格式示例**：

```json
// ✅ 成功
{ "code": 200, "message": "success", "data": { "id": 1, "name": "张三", "phone": "13800138000" } }

// ❌ 失败
{ "code": 500, "message": "用户不存在", "data": null }
```

> **⚠️ 注意：`code: 500` 和 HTTP 状态码 `500 Internal Server Error` 是两回事！**  
> 当 Controller 正常 `return Result.fail(...)` 时，HTTP 层面仍然是 `200 OK`（因为 Controller 正常返回了一个对象）。  
> 很多业务系统就是这么设计的：HTTP 永远是 200，用 `code` 字段区分业务成功/失败。

---

## 9. 第八步：测试接口 ✅

### 使用 .http 文件（IDEA 内置，推荐）

项目里已有测试文件，在 IDEA 中打开后点击行号旁边的绿色 ▶ 即可发送：

**testCreate.http**：
```
POST http://localhost:8081/user
Content-Type: application/json

{
    "name": "张三",
    "phone": "13800000001"
}
```

**testUpdate.http**：
```
PUT http://localhost:8081/user
Content-Type: application/json

{
    "id": 1,
    "name": "张三改名",
    "phone": "13999999999"
}
```

**testDelete.http**：
```
DELETE http://localhost:8081/user/1
```

### 测试顺序建议

```
1. GET  /user/list          → 查所有（初始为空）
2. POST /user               → 新增 {"name":"张三", "phone":"13800000001"}
3. GET  /user/list          → 查所有（应该有数据了）
4. GET  /user/1             → 按ID查
5. PUT  /user               → 修改 {"id":1, "name":"张三改", "phone":"13999999999"}
6. DELETE /user/1           → 删除
7. GET  /user/list          → 确认删除成功
```

---

# 第二部分：排坑指南

> 下面这些都是实际开发中遇到的真实问题，每个坑都踩过一遍，理解之后就不会再犯了。

---

## 踩坑一：Lombok 报错 "requires enabled annotation processing"

### 现象

IDEA 提示：

```
Lombok requires enabled annotation processing
```

或者写 `result.setCode(200)` 时提示 `Cannot resolve method setCode()`。

### 原因

IDEA 不知道 Lombok 帮你生成了哪些方法。`@Data` 注解在编译时生成 getter/setter，但 IDEA 默认不开启注解处理。

### 解决

**第一步**：File → Settings → Build, Execution, Deployment → Compiler → **Annotation Processors**

勾选 ✅ **Enable annotation processing**

**第二步**：Build → **Rebuild Project**（或重启 IDEA）

**第三步**（检查）：Settings → Plugins → 搜索 **Lombok**，确认已安装。

---

## 踩坑二：查不到用户时返回 200 success(null) 而不是 500 fail

### 现象

访问 `GET /user/999`（不存在的ID），期望返回：

```json
{ "code": 500, "message": "用户不存在", "data": null }
```

实际却返回：

```json
{ "code": 200, "message": "success", "data": null }
```

### 排查步骤

**第一步：检查 Service 有没有抛异常**

你的 Service 必须是这样的：

```java
// ✅ 正确写法
public User getUserById(Long id) {
    User user = userMapper.findById(id);
    if (user == null) {
        throw new RuntimeException("用户不存在");  // ⭐ 必须抛！
    }
    return user;
}
```

**不是这样的：**

```java
// ❌ 错误写法 —— 没有抛异常
public User getUserById(Long id) {
    return userMapper.findById(id);  // 查不到就返回 null
}
```

**第二步：检查 Controller catch 有没有进入**

在 catch 里加一行打印：

```java
} catch (RuntimeException e) {
    System.out.println("进入异常处理");  // 确认是否打印
    return Result.fail(e.getMessage());
}
```

访问 `/user/999`，看控制台有没有输出：

- **有输出** → 异常被正确捕获，问题在 Result 类
- **没输出** → Service 根本没抛异常，问题在 Service

### 根因总结

```
Mapper 返回 null → Service 没检查直接返回 null → Controller 执行 Result.success(null) → 返回 200
```

**一句话**：Service 里必须判断 null 并 `throw new RuntimeException()`。

---

## 踩坑三：新增用户后返回的 id 是 null

### 现象

```json
{
  "code": 200,
  "data": {
    "id": null,         // ← 明明数据库里有 id=1，这里却是 null
    "name": "six",
    "phone": "6666"
  },
  "message": "success"
}
```

### 原因

数据库确实生成了自增 id，但 MyBatis 不知道要把这个 id 塞回 Java 对象的哪个属性。

### 排查清单

**① 数据库 id 必须是自增的**

```sql
-- 检查表结构
DESC user;

-- id 字段应该显示：Extra = auto_increment
-- 如果没有，执行：
ALTER TABLE user MODIFY id BIGINT AUTO_INCREMENT PRIMARY KEY;
```

**② Mapper 必须有 `@Options`**

```java
// ✅ 正确：有 @Options
@Insert("insert into user(name, phone) values(#{name}, #{phone})")
@Options(useGeneratedKeys = true, keyProperty = "id")  // ⭐ 必须！
int insert(User user);

// ❌ 错误：没有 @Options，id 不会回填
@Insert("insert into user(name, phone) values(#{name}, #{phone})")
int insert(User user);
```

**③ `keyProperty` 必须和实体类属性名一致**

```java
// User 实体类
private Long id;         // 属性名是 "id"

// Mapper
@Options(..., keyProperty = "id")  // keyProperty 也必须是 "id"
```

如果你的实体类是 `private Long userId;`，那 keyProperty 就该写 `"userId"`。

**④ Controller 返回的必须是 insert 后的那个对象**

```java
// ✅ 正确：返回被 @Options 修改过的 user
userService.addUser(user);
return Result.success(user);  // 此时 user.getId() 已经被回填了

// ❌ 错误：返回了一个新的对象
userService.addUser(user);
return Result.success("添加成功");  // 看不到 id
```

### 调试技巧

在 Service 的 insert 后面加一行：

```java
int result = userMapper.insert(user);
System.out.println(user);  // 看控制台：id 是 null 还是有值？
```

---

# 第三部分：深入理解

---

## Controller 和 Service 的职责到底怎么分？

这是初学者最容易困惑的问题。一个简单的判断标准：

### Controller 负责「请求层面的合法性」

也就是：这个请求能不能被系统正常接收？

- ✅ 参数有没有传？（如 `@RequestBody` 是否为空）
- ✅ 参数格式对不对？（如 age 传了 "abc" 但定义的是 Integer）
- ✅ 有没有权限访问？（如 `/admin/delete` 需要管理员权限）

这些校验**不需要查数据库**，Controller 就能做。

```java
// Controller 层可以做的基础校验
@PostMapping
public Result addUser(@RequestBody User user) {
    if (user.getName() == null) {
        return Result.fail("用户名不能为空");  // ← 这只是参数存在性检查
    }
    // ...
}
```

### Service 负责「业务是否允许」

也就是：这个操作合不合业务规则？

- ✅ 用户名是否已存在？（需要查数据库）
- ✅ 密码要加密成什么格式？（业务规则）
- ✅ 新用户默认分配什么角色？（业务规则）
- ✅ 删除用户前要不要检查关联数据？（业务规则）

这些逻辑**需要查数据库或涉及业务规则**，必须在 Service 里处理。

```java
// Service 层做的业务校验
@Override
public void addUser(User user) {
    // 检查用户名是否重复（需要查数据库 → 业务逻辑）
    User exist = userMapper.findByName(user.getName());
    if (exist != null) {
        throw new RuntimeException("用户名已存在");
    }
    // 执行添加
    userMapper.insert(user);
}
```

### 你可能会在 OA 项目里看到的反例

```java
// ❌ 不太好的写法：Controller 里做业务校验
@PostMapping("/create")
public Result create(UserVO userVO) {
    // Controller 直接查数据库判断用户名是否重复
    User exist = userService.getUserByLoginName(userVO.getLoginName());
    if (exist != null) {
        return Result.fail("用户名已存在");
    }
    // ...
}
```

**为什么会有这种代码？**

1. **历史演变**：最早所有代码写在 Controller，后来加了 Service 但旧代码没搬干净
2. **习惯问题**：有些开发者习惯在 Controller 入口把所有校验做了
3. **Controller 越来越胖**：今天加一个 if，明天加一个 if，最后 Controller 3000 行

> **💡 你看 OA 源码时，不要只学"它怎么写"，还要判断"哪些是合理设计，哪些是历史包袱"。**

### 一个完整的请求流程

```
用户注册请求
      │
      ▼
Controller ── 参数有没有？格式对不对？权限有没有？
      │
      ▼
Service ──── 用户名重复吗？密码怎么加密？默认角色是什么？
      │
      ▼
Mapper ───── insert into user...
      │
      ▼
MySQL 数据库
```

> 💡 **Controller → Service 是一次普通的方法调用（JVM 内存调用），不是网络通信，成本极低。**  
> 所以不用担心"校验放 Service 会增加来回通信成本"。

---

## 三层架构：请求是如何流转的？

```
浏览器/Postman 发 HTTP 请求
        │
        ▼
┌──────────────────────────────────────────────┐
│  Controller  （接待员）                        │
│  - 接收 HTTP 请求                             │
│  - 参数校验（有没有传？格式对不对？）            │
│  - 调用 Service                               │
│  - 返回 Result 给前端                          │
└──────────────────┬───────────────────────────┘
                   │
                   ▼
┌──────────────────────────────────────────────┐
│  Service     （经理）                          │
│  - 业务校验（用户名重复吗？权限够吗？）          │
│  - 数据转换（密码加密、设置默认值）             │
│  - 调用 Mapper                                │
│  - 组合多个 Mapper 完成复杂操作                │
└──────────────────┬───────────────────────────┘
                   │
                   ▼
┌──────────────────────────────────────────────┐
│  Mapper      （数据库操作员）                   │
│  - 执行 SQL                                   │
│  - 返回查询结果                                │
└──────────────────┬───────────────────────────┘
                   │
                   ▼
              MySQL 数据库
```

### 看 OA 项目代码的三个问题

每看到一个模块，问自己：

1. **Controller 为什么这样设计？**  
   为什么用 `@GetMapping("/{id}")` 而不是 `@GetMapping("/query")`？

2. **Service 里面真正的业务是什么？**  
   为什么 `userMapper.delete(id)` 前面还有 `checkUserExist()`？

3. **Mapper 对应哪个数据库操作？**  
   看到 `selectById()` → 马上去数据库看对应哪张表。

这三个问题比死记代码重要 100 倍。

---

## MyBatis 和 MyBatis-Plus 有什么区别？

### MyBatis（你现在学的）

SQL 自己写在 Mapper 接口的注解或 XML 里：

```java
@Mapper
public interface UserMapper {
    @Select("select * from user where id = #{id}")
    User findById(Long id);
}
```

你要手动写每一个 SQL 语句。

### MyBatis-Plus（OA 项目里常见）

继承 `BaseMapper` 后，自动拥有常用的增删改查方法：

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 什么都不用写！BaseMapper 自带了：
    // selectById(), selectList(), selectOne(),
    // insert(), updateById(), deleteById() ...
}
```

### 对比

| | MyBatis | MyBatis-Plus |
|------|---------|--------------|
| SQL 位置 | Mapper 注解里手写 | 框架自动生成 |
| 简单 CRUD | 要写 SQL | `baseMapper.selectById(id)` 即可 |
| 复杂查询 | 手写 SQL（灵活） | 用 QueryWrapper 动态拼条件 |
| 学习建议 | **先学这个**，理解 SQL 怎么执行 | 入门后再学，OA 项目大量使用 |

---

## QueryWrapper 是什么？为什么出现在 ServiceImpl 里？

你在 OA 项目里看到这样的代码：

```java
QueryWrapper<User> qw = new QueryWrapper<>();
qw.eq("id", 5);
User user = userMapper.selectOne(qw);
```

### 字面理解

- **Query** = 查询
- **Wrapper** = 包装器

`QueryWrapper` = **一个装查询条件的容器**

### 为什么会出现在 ServiceImpl？

因为 **Service 负责根据业务需求决定查什么条件**，而 MyBatis-Plus 的 BaseMapper 已经帮你写好了 `selectOne()`、`selectList()` 等基础方法，Service 只需要告诉 Mapper"我要什么条件"。

```
// MyBatis 写法（你现在学的）
Service:    userMapper.findById(id)           // 调用 Mapper 自定义方法
Mapper:     @Select("select * from user where id=#{id}")   // Mapper 里有 SQL

// MyBatis-Plus 写法（OA 项目）
Service:    QueryWrapper qw = ...;            // 组装条件
            userMapper.selectOne(qw)           // 调用 BaseMapper 自带方法
Mapper:     extends BaseMapper<User>           // 不用写 SQL
```

### 常见用法

```java
// 等于：where name = 'six'
qw.eq("name", "six");

// 模糊：where name like '%六%'
qw.like("name", "六");

// 多条件：where name='six' and phone='123'
qw.eq("name", "six").eq("phone", "123");

// 排序：order by id desc
qw.orderByDesc("id");

// 动态条件（企业代码最常见）
QueryWrapper<User> qw = new QueryWrapper<>();
if (name != null) { qw.like("name", name); }
if (phone != null) { qw.eq("phone", phone); }
if (status != null) { qw.eq("status", status); }
List<User> list = userMapper.selectList(qw);
```

> **💡 一句话总结**：`QueryWrapper` = "我要开始组装 User 表的查询条件了"。  
> Service 组装条件 → 传给 Mapper → Mapper 执行 SQL。真正执行 SQL 的仍然是 Mapper。

---

# 第四部分：企业项目进阶

---

## Swagger 接口文档注解详解

OA 项目 Controller 上常看到这些注解，它们**不影响程序运行，只是给 Swagger 生成接口文档用的**。

```
@ApiOperation(value = "创建用户", notes = "创建用户")
@ApiImplicitParams({
    @ApiImplicitParam(name = "userVO", value = "用户VO", dataType = "UserVO"),
    @ApiImplicitParam(name = "photo", value = "照片", dataType = "MultipartFile"),
})
@ApiResponses({@ApiResponse(code = 200, message = "操作成功")})
@ResponseBody
@RequestMapping(value = "/user/create", produces = {"application/json;charset=UTF-8;"})
```

### 逐个拆解

| 注解 | 分类 | 作用 |
|------|------|------|
| `@ApiOperation(value="创建用户", notes="创建用户")` | Swagger 文档 | 给接口起个名字，`value` 是标题，`notes` 是详细描述 |
| `@ApiImplicitParam(name="userVO", ...)` | Swagger 文档 | 描述这个接口接收哪些参数 |
| `@ApiResponse(code=200, message="操作成功")` | Swagger 文档 | 描述可能返回的 HTTP 状态码 |
| `@ResponseBody` | Spring MVC | 返回值直接作为 HTTP 响应体，不找页面 |
| `@RequestMapping(value="/user/create", produces=...)` | Spring MVC | 接口地址 + 返回格式 |

### 重点：`@RequestMapping` 详解

```java
@RequestMapping(
    value = "/user/create",                       // 接口地址
    produces = {"application/json;charset=UTF-8;"} // 返回 JSON，编码 UTF-8
)
```

- `value`：接口的 URL 路径
- `produces`：返回数据格式，`application/json` 表示返回 JSON

如果类上还有 `@RequestMapping("/admin")`，那完整路径就是：`POST /admin/user/create`

### 分类速记

```
真正影响程序运行的：
    @RequestMapping   → 决定 URL
    @ResponseBody     → 返回 JSON（@RestController 已包含）
    @GetMapping / @PostMapping / @PutMapping / @DeleteMapping

只是 Swagger 文档用的（删掉也能跑）：
    @ApiOperation     → 接口名称
    @ApiImplicitParam → 参数说明
    @ApiResponse      → 返回说明
```

---

## 后续学习路线图

你现在已经完成了：

```
✅ 查询单个用户   GET  /user/{id}
✅ 查询所有用户   GET  /user/list
✅ 新增用户       POST /user
✅ 删除用户       DELETE /user/{id}
✅ 修改用户       PUT /user
```

**CRUD 已经全部跑通了！** 接下来的学习路线：

### 第一优先级：分页查询 ⭐⭐⭐⭐⭐

真实项目里几乎没有 `select * from user` 这种全表查询，都是分页：

```
GET /user/page?page=1&size=10

返回：
{
  "code": 200,
  "data": {
    "total": 100,          // 总共 100 条
    "records": [           // 当前页 10 条
      { "id": 1, "name": "张三" },
      { "id": 2, "name": "李四" }
    ]
  }
}
```

你会学到：`LIMIT`、`OFFSET`、总条数查询、Page 对象设计。

### 第二优先级：全局异常处理 ⭐⭐⭐⭐⭐

你现在的 Controller 每个方法都有 `try-catch`，实际项目不会这样：

```java
// 现在（每个方法都有 try-catch）
@GetMapping("/{id}")
public Result<User> getUser(@PathVariable Long id) {
    try { ... } catch (RuntimeException e) { ... }
}

// 以后（用 @RestControllerAdvice 统一处理）
@GetMapping("/{id}")
public Result<User> getUser(@PathVariable Long id) {
    return Result.success(userService.getUserById(id));  // 干干净净
}
```

### 后续技能树

```
分页查询 ──→ 全局异常处理 ──→ 参数校验(@Valid) ──→ DTO/VO分层
                                                          │
                                                          ▼
                                    回头看OA源码 ←── 权限控制 ←── 登录JWT
```

### 学习建议

> 🎯 **你现在完全可以重新打开 OA 项目的 UserController 了。**
>
> 以前看到：
> - `@ApiOperation`、`@Autowired`、`@RequestMapping` → 全是迷雾
>
> 现在你知道：
> - Controller 入口 → Service 业务 → Mapper 数据库 → 返回 Result
>
> 直接找 `@GetMapping`、`@PostMapping` 这几个注解，挑几个接口顺着调用链往下读。

---

## 项目结构总览

```
testdemo/
├── .gitignore
├── README.md
├── pom.xml                             # 项目配置（依赖管理）
├── mvnw / mvnw.cmd                     # Maven 包装器
├── testCreate.http                     # 测试：新增用户
├── testUpdate.http                     # 测试：修改用户
├── testDelete.http                     # 测试：删除用户
└── src/
    ├── main/
    │   ├── java/com/example/testdemo/
    │   │   ├── TestdemoApplication.java      # 启动类
    │   │   ├── controller/
    │   │   │   └── UserController.java       # 控制器（接收请求）
    │   │   ├── service/
    │   │   │   ├── UserService.java          # Service 接口
    │   │   │   └── impl/
    │   │   │       └── UserServiceImpl.java  # Service 实现
    │   │   ├── mapper/
    │   │   │   └── UserMapper.java           # Mapper（SQL）
    │   │   ├── entity/
    │   │   │   └── User.java                 # 实体类
    │   │   └── utils/
    │   │       └── Result.java               # 统一返回结果
    │   └── resources/
    │       └── application.properties        # 应用配置
    └── test/
```

---

## 常见问题 FAQ

### 数据库相关

**Q：启动报错 `Access denied for user 'root'@'localhost'`？**  
用户名或密码不对。检查 `application.properties` 里的配置。

**Q：启动报错 `Unknown database 'springboot_test'`？**  
数据库还没创建：`CREATE DATABASE springboot_test DEFAULT CHARACTER SET utf8mb4;`

**Q：启动报错 `Table 'springboot_test.user' doesn't exist`？**  
表还没建，执行建表 SQL。

**Q：端口被占用 `Port 8081 was already in use`？**  
换个端口，改 `application.properties` 里的 `server.port=8082`。

### Lombok 相关

**Q：IDEA 提示 Lombok 不生效 / 方法找不到？**  
看[踩坑一](#踩坑一lombok-报错-requires-enabled-annotation-processing)：Settings → Annotation Processors → 勾选 Enable。

### 注解相关

**Q：`@Autowired` 和 `@Resource` 有什么区别？**  
`@Autowired` 是 Spring 的（按类型注入），`@Resource` 是 Java 标准的（按名称注入）。日常差别不大。

**Q：`@RestController` 和 `@Controller` 有什么区别？**  
`@RestController` = `@Controller` + `@ResponseBody`。用 `@RestController` 时方法返回值自动转 JSON；用 `@Controller` 默认返回页面。

### MyBatis 相关

**Q：新增用户后 id 是 null？**  
看[踩坑三](#踩坑三新增用户后返回的-id-是-null)：检查数据库自增 + `@Options` 注解。

**Q：查询不存在的用户返回 200 而不是 500？**  
看[踩坑二](#踩坑二查不到用户时返回-200-successnull-而不是-500-fail)：Service 没抛异常。

**Q：`#{xxx}` 和 `${xxx}` 有什么区别？**  
`#{}` 会加引号防 SQL 注入（预编译），`${}` 直接拼接字符串。**永远优先用 `#{}`。**

---

> 📝 这个 README 会跟着项目进展持续更新。后续的分页查询、全局异常处理等步骤完成后，再补充进来！
