package com.github.charon.r13b.spring.service;

import com.github.charon.r13b.spring.dao.UserDao;
import com.github.charon.r13b.spring.security.MyUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    UserDao userDao;

    public MyUserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDao
                .findByEmail(username)
                .map(u -> new MyUserDetails(u))
                .orElseThrow(() -> new UsernameNotFoundException("not found"));
    }
}
