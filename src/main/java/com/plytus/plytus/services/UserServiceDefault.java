package com.plytus.plytus.services;

import com.plytus.plytus.model.User;
import com.plytus.plytus.repository.ExpenseRepository;
import com.plytus.plytus.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class UserServiceDefault implements UserService {

    private final UserRepository userRepository;
    @Autowired
    public  UserServiceDefault(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public List<User> getUsers() {
        List<User> result = new ArrayList<>();
        userRepository.findAll().forEach(result::add);
        return result;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).get();
    }

    @Override
    public User saveNewUser(User user) {
        return userRepository.save(user);
    }
}
