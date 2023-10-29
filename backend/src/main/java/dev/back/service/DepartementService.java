package dev.back.service;

import dev.back.entite.Departement;
import dev.back.entite.Employe;
import dev.back.entite.JoursOff;
import dev.back.repository.DepartementRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartementService {

    @Autowired
    DepartementRepo departementRepo;

    public List<Departement> listDepartements() {
        return departementRepo.findAll();
    }


    public Departement getDepartementById(Integer id){
       return departementRepo.findById(id).orElseThrow();

    }

    public void deleteDepartement(int id){departementRepo.delete(departementRepo.findById(id).orElseThrow());}


    /**
     * utilise .save donc permet de créer ET de modifier
     * @param departement
     */
    @Transactional
    public void addDepartement(Departement departement) {
        departementRepo.save(departement);
    }

}
