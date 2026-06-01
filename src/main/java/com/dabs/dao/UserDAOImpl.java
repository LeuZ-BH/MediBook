package com.dabs.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dabs.model.User;

@Repository
public class UserDAOImpl implements UserDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void save(User user) {
        session().save(user);
    }

    @Override
    public User findById(int userId) {
        return session().get(User.class, userId);
    }

    @Override
    public User findByEmail(String email) {
        return session()
                .createQuery("FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .uniqueResult();
    }

    @Override
    public List<User> findAll() {
        return session().createQuery("FROM User", User.class).getResultList();
    }

    @Override
    public void update(User user) {
        session().update(user);
    }

    @Override
    public void delete(User user) {
        session().delete(user);
    }
}

