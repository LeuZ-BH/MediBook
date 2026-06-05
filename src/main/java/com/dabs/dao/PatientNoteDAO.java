package com.dabs.dao;

import com.dabs.model.PatientNote;

import java.util.List;

public interface PatientNoteDAO {

    void save(PatientNote note);

    void delete(Integer noteId);

    List<PatientNote> findByPatient(Integer patientId);

    void update(PatientNote note);

    PatientNote findById(Integer noteId);

}