package org.tcat.service.domain.user;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.tcat.service.JunitBaseTest;
import org.tcat.service.controller.user.UserService;

/**
 * Created by Lin on 2017/4/20.
 */
public class TestUserService extends JunitBaseTest {

    @Autowired
    private UserService userService;

    @Test
    public void test_get() {
        show(userService.get(1));
    }

    @Test
    public void test_update() {
        show(userService.update(1,"eee"));
    }

}
