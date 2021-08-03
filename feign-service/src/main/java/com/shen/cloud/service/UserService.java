package com.shen.cloud.service;

import com.shen.cloud.pojo.Result;
import com.shen.cloud.pojo.User;
import com.shen.cloud.service.Impl.UserFallbackService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "user-service", fallback = UserFallbackService.class)
public interface UserService {

    @PostMapping("/user/insert")
    Result insert(@RequestBody User user);

    @GetMapping("/user/{id}")
    Result<User> getUser(@PathVariable(value = "id") Long id);

    @GetMapping("/user/listUsersByIds")
    Result<List<User>> listUsersByIds(@RequestParam(value = "ids") List<Long> ids);

    @GetMapping("/user/getByUsername")
    Result<User> getByUsername(@RequestParam(value = "username") String username);

    @PostMapping("/user/update")
    Result update(@RequestBody User user);

    /*
    * @PathVariable 必须写value ！
    */
    @PostMapping("/user/delete/{id}")
    Result delete(@PathVariable(value = "id") Long id);

}

