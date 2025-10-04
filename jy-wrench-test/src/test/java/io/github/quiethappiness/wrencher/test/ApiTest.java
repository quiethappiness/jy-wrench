package io.github.quiethappiness.wrencher.test;

import com.alibaba.fastjson.JSON;
import io.github.quiethappiness.wrench.dynamic.config.center.domain.model.valobj.AttributeVO;
import io.github.quiethappiness.wrench.dynamic.config.center.types.annotations.DCCValue;
import io.github.quiethappiness.wrencher.infrastructure.dao.IUserDao;
import io.github.quiethappiness.wrencher.infrastructure.po.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RTopic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest()
public class ApiTest {

    @DCCValue("0")
    private String downgradeSwitch;

    @Resource
    private RTopic dynamicConfigCenterRedisTopic;

    @Test
    public void test_get() throws InterruptedException {
        log.info("测试结果:{}", downgradeSwitch);
    }

    @Test
    public void test_publish() throws InterruptedException {
        // 推送
        dynamicConfigCenterRedisTopic.publish(new AttributeVO("downgradeSwitch", "4"));

        new CountDownLatch(1).await();
    }
    @Resource
    private IUserDao userDao;
    
    @Test
    public void test_queryUserInfoByUserId() {
        User user = userDao.queryUserInfoByUserId(new User("980765512"));
        log.info("测试结果：{}", JSON.toJSONString(user));
    }
    
    @Test
    public void test_insertUser() {
        User user = new User();
        user.setUserId("480765132");
        user.setUserNickName("小傅哥");
        user.setUserHead("01_50");
        user.setUserPassword("123456");
        
        userDao.insertUser(user);
    }
}