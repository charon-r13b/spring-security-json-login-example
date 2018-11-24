package com.github.charon.r13b.spring.dao;

import java.util.Optional;

import com.github.charon.r13b.spring.entity.User;
import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

@ConfigAutowireable
@Dao
public interface UserDao {
    @Select
    Optional<User> findByEmail(String email);
}
