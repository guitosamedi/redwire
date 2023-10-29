package dev.back.repository;

import dev.back.entite.Absence;
import dev.back.entite.JoursOff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AbsenceRepo extends JpaRepository<Absence,Integer> {

    List<Absence> getAbsenceByEmploye_Id(int idEmploye);
    List<Absence> getAbsenceByEmploye_Departement_Id(int idDepartement);
    List<Absence> getAbsenceByEmploye_Manager_Id(int idManager);

    List<Absence> getAbsenceByDateDebutAndDateFin(LocalDate dateDebut, LocalDate dateFin);
}
