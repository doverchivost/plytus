package com.plytus.plytus.repository;

import com.plytus.plytus.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findById(long id);
}
