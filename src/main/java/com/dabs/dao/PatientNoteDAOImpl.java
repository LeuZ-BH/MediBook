package com.dabs.dao;

import com.dabs.model.PatientNote;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PatientNoteDAOImpl implements PatientNoteDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void save(PatientNote note) {
        session().save(note);
    }

    @Override
    public void delete(Integer noteId) {

        PatientNote note =
                session().get(PatientNote.class, noteId);

        if (note != null) {
            session().delete(note);
        }
    }

    @Override
    public PatientNote findById(Integer noteId) {
        return session().get(PatientNote.class, noteId);
    }

    @Override
    public void update(PatientNote note) {
        session().update(note);
    }

    @Override
    public List<PatientNote> findByPatient(Integer patientId) {

        return session().createQuery(
                "FROM PatientNote n " +
                "WHERE n.patient.userId = :pid " +
                "ORDER BY n.createdAt DESC",
                PatientNote.class)
                .setParameter("pid", patientId)
                .getResultList();
    }
}