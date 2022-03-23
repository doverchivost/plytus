package com.plytus.plytus.services;

import com.plytus.plytus.model.User;

import java.util.List;

public interface UserService {

    List<User> getUsers();

    User getUserById(Long id);

    User saveNewUser(User user);

    long userExists(long tg_id);
}
