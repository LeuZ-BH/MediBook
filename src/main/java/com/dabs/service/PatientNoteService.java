package com.dabs.service;

import java.util.List;
import com.dabs.model.PatientNote;
import com.dabs.model.User;

public interface PatientNoteService {

    void saveNote(PatientNote note);

    List<PatientNote> getPatientNotes(Integer patientId);

    void updateNote(PatientNote note);

    void deleteNote(Integer noteId);

    PatientNote getNote(Integer noteId);
}