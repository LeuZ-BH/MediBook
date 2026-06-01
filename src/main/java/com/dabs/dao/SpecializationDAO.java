package com.dabs.dao;

import java.util.List;

import com.dabs.model.Specialization;

public interface SpecializationDAO {
    void save(Specialization spec);

    Specialization findById(int specId);

    Specialization findByName(String name);

    List<Specialization> findAll();

    void update(Specialization spec);

    void delete(Specialization spec);
}

