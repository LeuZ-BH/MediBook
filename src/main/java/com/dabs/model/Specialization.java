package com.dabs.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "specializations")
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spec_id")
    private Integer specId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    public Specialization() {
    }

    public Specialization(Integer specId, String name, String description) {
        this.specId = specId;
        this.name = name;
        this.description = description;
    }

    public Integer getSpecId() {
        return specId;
    }

    public void setSpecId(Integer specId) {
        this.specId = specId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Specialization))
            return false;
        Specialization that = (Specialization) o;
        return Objects.equals(specId, that.specId);
    }

    @Override
    public int hashCode() {
        return specId != null ? specId.hashCode() : 0;
    }
}

