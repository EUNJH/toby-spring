package org.toby.user.service;

import org.toby.user.domain.User;

public interface UserService {

    void add(User user);
    void upgradeLevels();
}
