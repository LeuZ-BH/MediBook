package com.dabs.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dabs.dao.UserDAO;
import com.dabs.model.User;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    private String hash(String plain) {
        return com.dabs.util.PasswordUtil.hash(plain);
    }


    @Override
    @Transactional
    public void registerPatient(User user) {
        if (user.getPasswordHash() != null) {
            // Expect caller to already hash, but keep safe.
            // If it's already a hash, double-hashing would break login.
            // So do not re-hash here.
        }
        userDAO.save(user);
    }

    @Override
    @Transactional
    public User findById(int userId) {
        return userDAO.findById(userId);
    }

    @Override
    @Transactional
    public User findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    @Override
    @Transactional
    public User authenticate(String email, String passwordPlain) {
        User user = userDAO.findByEmail(email);
        if (user == null) return null;
        String hashed = hash(passwordPlain);
        if (user.getPasswordHash() == null) return null;
        if (!user.getPasswordHash().equals(hashed)) return null;
        return user;
    }

    @Override
    @Transactional
    public List<User> findAll() {
        return userDAO.findAll();
    }

    @Override
    @Transactional
    public void delete(int userId) {
        User u = userDAO.findById(userId);
        if (u != null) {
            userDAO.delete(u);
        }
    }
}

