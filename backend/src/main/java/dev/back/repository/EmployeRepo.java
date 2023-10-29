package dev.back.repository;

import dev.back.entite.Employe;
import dev.back.entite.JoursOff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeRepo extends JpaRepository<Employe,Integer> {
    Optional<Employe> findByEmail(String email);
    Optional<Employe> findById(int id);

    List<Employe> findAllByManager_Id(int id);

}
