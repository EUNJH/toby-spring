package org.toby.user.service;

import org.springframework.transaction.annotation.Transactional;
import org.toby.user.domain.User;

import java.util.List;

@Transactional
public interface UserService {

    void add(User user);
    void upgradeLevels();
    @Transactional(readOnly = true)
    User get(String id);
    @Transactional(readOnly = true)
    List<User> getAll();
    void deleteAll();
    void update(User user);
}
