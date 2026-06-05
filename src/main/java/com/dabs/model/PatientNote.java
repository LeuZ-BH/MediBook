package com.dabs.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_notes")
public class PatientNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer noteId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    private User patient;

    @Column(columnDefinition = "TEXT")
    private String noteText;

    private LocalDateTime createdAt;

    public PatientNote() {
    }

    public Integer getNoteId() {
        return noteId;
    }

    public void setNoteId(Integer noteId) {
        this.noteId = noteId;
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}