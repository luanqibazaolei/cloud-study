package com.shen.cloud.controller;


import com.shen.cloud.pojo.Result;
import com.shen.cloud.pojo.User;

import com.shen.cloud.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@ResponseBody
@RequestMapping("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/insert")
    public Result insert(@RequestBody User user) {
        userService.insert(user);
        return new Result("操作成功", 200);
    }

    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        User user = userService.getUser(id);
        LOGGER.info("根据id获取用户信息，用户名称为：{}",user.getUsername());
        return new Result<User>(user);
    }

    @GetMapping("/listUsersByIds")
    public Result<List<User>> listUsersByIds(@RequestParam List<Long> ids) {
        List<User> userList= userService.listUsersByIds(ids);
        LOGGER.info("根据ids获取用户信息，用户列表为：{}",userList);
        return new Result<List<User>>(userList);
    }

    @GetMapping("/getByUsername")
    public Result<User> getByUsername(@RequestParam String username) {
        User user = userService.getByUsername(username);
        return new Result<User>(user);
    }

    @PostMapping("/update")
    public Result update(@RequestBody User user) {
        userService.update(user);
        return new Result("操作成功", 200);
    }

    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        userService.delete(id);
        return new Result("操作成功", 200);
    }

}


