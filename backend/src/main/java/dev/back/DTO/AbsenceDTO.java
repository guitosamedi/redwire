package dev.back.DTO;

import dev.back.entite.Employe;
import dev.back.entite.Statut;
import dev.back.entite.TypeAbsence;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AbsenceDTO {
    LocalDate dateDebut;
    LocalDate dateFin;

    Statut statut;
    TypeAbsence typeAbsence;

    String motif;


    int employeId;


    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public TypeAbsence getTypeAbsence() {
        return typeAbsence;
    }

    public void setTypeAbsence(TypeAbsence typeAbsence) {
        this.typeAbsence = typeAbsence;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public int getEmployeId() {
        return employeId;
    }

    public void setEmployeId(int employeId) {
        this.employeId = employeId;
    }

    public AbsenceDTO(LocalDate dateDebut, LocalDate dateFin, Statut statut, TypeAbsence typeAbsence, String motif, int employeId) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.typeAbsence = typeAbsence;
        this.motif = motif;
        this.employeId = employeId;
    }
}
