package com.shen.springcloud;

import com.shen.cloud.pojo.Result;
import com.shen.cloud.pojo.User;
import com.shen.cloud.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = com.shen.cloud.FeignServiceApplication.class)
class FeignServiceApplicationTests {


    @Autowired
    UserService userService;

    @Test
    void contextLoads() {
        Result<User> user = userService.getUser(1L);

        System.out.println(user.toString());
    }

}
