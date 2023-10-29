package dev.back.service;

import dev.back.entite.*;
import dev.back.repository.AbsenceRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class AbsenceService {
    @Autowired
    AbsenceRepo absenceRepo;
    @Autowired
    JoursOffService joursOffService;
    public List<Absence> listAbsences() {
        return absenceRepo.findAll();
    }


    /**
     * utilise .save donc permet de créer ET de modifier
     * @param absence
     */
    @Transactional
    public void addAbsence(Absence absence) {
        absenceRepo.save(absence);
    }


    public List<Absence> listAbsenceByEmploye(int id){
        return absenceRepo.getAbsenceByEmploye_Id(id);
    }


    /**
     *
     * @param departementId
     * @return List d'absence de tout un departement
     */
    public List<Absence> listAbsenceByEmployeDepartement(int departementId){
        return absenceRepo.getAbsenceByEmploye_Departement_Id(departementId);
    }

    public Absence getAbsenceById(Integer id){
        Optional<Absence> absenceOp = absenceRepo.findById(id);
        return  absenceOp.orElseThrow();
    }

    public List<Absence> getAbsenceByDate(LocalDate date){
       return absenceRepo.getAbsenceByDateDebutAndDateFin(date,date);
    }


    public void deleteAbsence(int id){absenceRepo.delete(absenceRepo.findById(id).orElseThrow());}

    public List<Absence> getAbsenceByEmployeManagaerId(int id){
        return absenceRepo.getAbsenceByEmploye_Manager_Id(id);
    }




    /**
     * prend en parametre une absence, et retourne un entier
     * qui correspond au nombre de jour d'absence, sans les weekends et les jours feriés/RTT_employeur
     * surtout utile pour connaître le nombre de jour à retirer du solde de RTT/congé
     *
     * @param absence
     * @return entier
     */
    public int nbJourOuvre(Absence absence){
        int jourTotal =0;
        long nombreDeJour = DAYS.between(absence.getDateDebut(), absence.getDateFin());//nb jour au total
        List<LocalDate> listeJourAbsence = absence.getDateDebut().datesUntil(absence.getDateFin().plusDays(1)).toList();
        //liste de tous les jours dans l'absence (weekend et joursOff inclus)

        for(LocalDate jour : listeJourAbsence) {

            //si c'est ni un samedi ni un dimanche
            if (!jour.getDayOfWeek().equals(DayOfWeek.SUNDAY) && !jour.getDayOfWeek().equals(DayOfWeek.SATURDAY)){

                List<JoursOff> jourOffs= joursOffService.listJoursOff();
                List<LocalDate> listeDateFerie= new ArrayList<>();

                for(JoursOff joursOff:jourOffs){
                    listeDateFerie.add(joursOff.getJour());
                }

                //si ce jour n'est pas dans la liste des jours feries ou des rtt employeur
                if(!listeDateFerie.contains(jour)){

                    //un conge sans solde ne compte pas non plus

                    if(absence.getTypeAbsence().equals(TypeAbsence.RTT)){
                        jourTotal++;

                    }
                    if(absence.getTypeAbsence().equals(TypeAbsence.CONGE_PAYE)){
                        jourTotal++;
                        //un même congé ne peut pas être un amalgame de RTT et de conge_paye,
                        //pas besoin d'avoir deux variables differentes pour compter le nombre de jour.
                    }
                }
            }
        }

        return  jourTotal;
    }
}
