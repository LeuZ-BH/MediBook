package com.dabs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dabs.dao.PatientNoteDAO;
import com.dabs.model.PatientNote;
import com.dabs.model.User;

@Service
@Transactional
public class PatientNoteServiceImpl implements PatientNoteService {

    @Autowired
    private PatientNoteDAO patientNoteDAO;

    @Override
    public void saveNote(PatientNote note) {
        patientNoteDAO.save(note);
    }

    @Override
    public List<PatientNote> getPatientNotes(Integer patientId) {
        return patientNoteDAO.findByPatient(patientId);
    }

    @Override
    public PatientNote getNote(Integer noteId) {
        return patientNoteDAO.findById(noteId);
    }

    @Override
    public void deleteNote(Integer noteId) {
        patientNoteDAO.delete(noteId);
    }

    @Override
    public void updateNote(PatientNote note) {
        patientNoteDAO.update(note);
    }
}