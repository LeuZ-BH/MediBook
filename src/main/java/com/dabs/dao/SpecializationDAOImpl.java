package com.dabs.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.dabs.model.Specialization;

@Repository
@Transactional
public class SpecializationDAOImpl implements SpecializationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session session() {
        return sessionFactory.openSession();
    }

    @Override
    public void save(Specialization spec) {
        session().save(spec);
    }

    @Override
    public Specialization findById(int specId) {
        return session().get(Specialization.class, specId);
    }

    @Override
    public Specialization findByName(String name) {
        return session()
                .createQuery("FROM Specialization s WHERE s.name = :name", Specialization.class)
                .setParameter("name", name)
                .uniqueResult();
    }

    @Override
    public List<Specialization> findAll() {
        return session().createQuery("FROM Specialization", Specialization.class).getResultList();
    }

    @Override
    public void update(Specialization spec) {
        session().update(spec);
    }

    @Override
    public void delete(Specialization spec) {
        session().delete(spec);
    }
}

