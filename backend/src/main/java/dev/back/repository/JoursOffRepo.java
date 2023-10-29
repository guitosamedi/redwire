package dev.back.repository;

import dev.back.entite.JoursOff;
import dev.back.entite.TypeJour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JoursOffRepo extends JpaRepository<JoursOff,Integer> {

    Optional<JoursOff> findByJour(LocalDate dateJour);

    List<JoursOff> findAllByTypeJour(TypeJour typeJour);



}
