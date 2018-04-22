package org.tcat.service.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tcat.parent.mybaits.annotation.EnforceSlave;
import org.tcat.service.controller.user.UserServiceRemote;
import org.tcat.service.domain.user.dao.UserPoMapper;
import org.tcat.service.domain.user.entity.UserPo;

/**
 * Created by Lin on 2017/4/19.
 */
//@Transactional(value = "MRDB", rollbackFor = Exception.class)
@Service("userService")
@RestController
@RequestMapping
public class UserService implements UserServiceRemote {

    @Autowired
    private UserPoMapper userPoMapper;

    @GetMapping("/test")
    public UserPo get(Integer id) {
        return userPoMapper.selectByPrimaryKey(id);
    }

    @GetMapping("/update")
    public Integer update(Integer id, String name) {
        UserPo userPo = new UserPo();
        userPo.setId(id);
        userPo.setName(name);
        return userPoMapper.updateByPrimaryKey(userPo);
    }

    @Override
    public Integer deleteById(Integer id) {
        return null;
    }
}
