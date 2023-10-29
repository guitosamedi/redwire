package dev.back.control;

import dev.back.DTO.AbsenceDTO;
import dev.back.component.SchedulingComponent;
import dev.back.entite.*;
import dev.back.service.AbsenceService;
import dev.back.service.EmailService;
import dev.back.service.EmployeService;
import dev.back.service.JoursOffService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@CrossOrigin
@RestController
@RequestMapping("absence")
public class AbsenceControl {
    AbsenceService absenceService;
    EmployeService employeService;

    JoursOffService joursOffService;

    EmailService emailService;
    SchedulingComponent schedulingComponent;


    public AbsenceControl(AbsenceService absenceService, EmployeService employeService, JoursOffService joursOffService, EmailService emailService, SchedulingComponent schedulingComponent) {
        this.absenceService = absenceService;
        this.employeService = employeService;
        this.joursOffService = joursOffService;
        this.emailService = emailService;
        this.schedulingComponent = schedulingComponent;
    }

    /**
     *
     * @return liste de toutes les absences de tous les employes de l'entreprise
     */
    //TODO
    @GetMapping
    public List<Absence> listAll(){
        return  absenceService.listAbsences();
    }


    @PostMapping("/traitement")
    public void traitementNuit(){
        this.schedulingComponent.TraitementDeNuit();
    }




    /**
     * permet de d'ajouter une absence en base de donnée
     * seul l'employé connecté peut faire une demande d'absence pour lui-même
     *
     * @param absenceDTO
     * @return ResponseEntity :
     *      *                  Created - 201 si ça marche
     *      *                  Unauthorized - 401 sinon
     * @throws Exception
     */
    @PostMapping
    public ResponseEntity<?> addAbsence(@RequestBody AbsenceDTO absenceDTO) throws Exception {
        Employe authEmploye = employeService.getActiveUser();

            if(absenceDTO.getTypeAbsence() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("le type d'absence est obligatoire ");
            }

            if (TypeAbsence.CONGE_SANS_SOLDE.equals(absenceDTO.getTypeAbsence())) {
                if (Objects.equals(absenceDTO.getMotif(), "")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("le motif est obligatoire pour un congé sans solde ");
                }
            }
            Absence absence = new Absence(LocalDateTime.now(), absenceDTO.getDateDebut(), absenceDTO.getDateFin(), Statut.INITIALE, absenceDTO.getTypeAbsence(), absenceDTO.getMotif(), authEmploye);

            List<JoursOff> joursOffList = joursOffService.listJoursOff();

            for (JoursOff joursOff : joursOffList) {
                if (absence.getDateDebut().isEqual(joursOff.getJour()) || absence.getDateFin().isEqual(joursOff.getJour())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("la date de debut ou de fin ne peut pas être un jour férié ou un rtt employeur");
                }
            }

            if(absenceDTO.getDateDebut().getDayOfWeek() == DayOfWeek.SATURDAY
                    || absenceDTO.getDateDebut().getDayOfWeek() == DayOfWeek.SUNDAY
                    ||absenceDTO.getDateFin().getDayOfWeek() == DayOfWeek.SATURDAY
                    || absenceDTO.getDateFin().getDayOfWeek() == DayOfWeek.SUNDAY ){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("la date de début ou de fin ne peut pas être en weekend ");
            }

            List<Absence> absenceList = absenceService.listAbsenceByEmploye(absence.getEmploye().getId());
            boolean superpositionDeDate = false;
            List<LocalDate> datesDemandes = new ArrayList<>();
            try {
                datesDemandes = absenceDTO.getDateDebut().datesUntil(absenceDTO.getDateFin().plusDays(1)).toList();
            } catch (Exception ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("la date de fin ne peut pas être avant celle de début ");
            }

            for (Absence absenceTempo : absenceList) {
                List<LocalDate> datesPrises = absenceTempo.getDateDebut().datesUntil(absenceTempo.getDateFin().plusDays(1)).toList();
                for (LocalDate jour : datesPrises) {
                    for (LocalDate jourDemande : datesDemandes) {

                        if (jourDemande.equals(jour)) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("vous avez déjà une demande de congé sur cette période ");

                        }
                    }
                }
            }

            if (absence.getDateCreation().isAfter(absenceDTO.getDateDebut().atTime(LocalTime.now()))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("la date de debut ne peut pas être passée ");
            }

            absence.setStatut(Statut.INITIALE);
            absenceService.addAbsence(absence);
            return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     *
     *
     * @return liste de tous les employes
     */
    @GetMapping("/employe")
    public List<Absence> listAllByEmploye(){
        Employe authEmploye = employeService.getActiveUser();
        return  absenceService.listAbsenceByEmploye(authEmploye.getId());
    }

    /**
     *
     * @param departementid
     * @return liste de tous les employes pour un departement donné
     */
    @RequestMapping("/departement")
    @GetMapping
    public List<Absence> listAllByEmployeDepartement(
            @RequestParam(name = "id", required = true) int departementid){
        return  absenceService.listAbsenceByEmployeDepartement(departementid);
    }

    /**
     *
     * @param int managerId
     * @return liste de tous les employes ayant le même manager d'id donné
     */
    @RequestMapping("/manager")
    @GetMapping
    public List<Absence> listAllByEmployeManager(){
        Employe authEmploye = employeService.getActiveUser();
        if(authEmploye.getManager()==null){
            List<Absence> absenceList= absenceService.getAbsenceByEmployeManagaerId(authEmploye.getId());

            absenceList.addAll(absenceService.listAbsenceByEmploye(authEmploye.getId()));

            return absenceList;
        }

        return  absenceService.getAbsenceByEmployeManagaerId(authEmploye.getId());
    }


    /**
     * change le statut d'une demande d'absence,
     * seul le manager connecté de l'employe qui a fait la demande est autorisé
     *
     * un mail est envoyé à l'employé pour l'informer que sa demande a changé de statut
     *
     * @param absence
     * @param id
     * @return ResponseEntity :
     *                  OK - 200 si ça marche
     *                  Unauthorized - 401 sinon
     */

    @PutMapping("/statut/{id}")
    public ResponseEntity<?> ChangeAbsenceStatut(@RequestBody AbsenceDTO absence, @PathVariable("id") String id) {

        Employe authManager = employeService.getActiveUser();
        Employe employe = employeService.getEmployeById(absence.getEmployeId());

        int authManagerId=0;
        int managerId=0;
        try{
         authManagerId=authManager.getId();
         managerId=employe.getManager().getId();

        }catch (Exception exception){
            authManagerId=1;
            managerId=1;
        }



        //vérifie que la personne connectée est bien le manager de l'employé qui a fait la demande
        if (authManagerId == managerId) {

            int absenceIdInt = Integer.parseInt(id);

            Absence absence1 = absenceService.getAbsenceById(absenceIdInt);

            int jourtotal = absenceService.nbJourOuvre(absence1);

            int nbRttNeeded = 0;
            int nbCongeNeeded = 0;

            absence1.setStatut(absence.getStatut());
            if (absence1.getStatut().equals(Statut.REJETEE)) {
                if (absence1.getTypeAbsence().equals(TypeAbsence.RTT)) {
                    nbRttNeeded = jourtotal;
                }
                if (absence1.getTypeAbsence().equals(TypeAbsence.CONGE_PAYE)) {
                    nbCongeNeeded = jourtotal;
                }
                employe.setSoldeConge(employe.getSoldeConge() + nbCongeNeeded);
                employe.setSoldeRtt(employe.getSoldeRtt() + nbRttNeeded);
            }
            employeService.addEmploye(employe);
            absenceService.addAbsence(absence1);




            emailService.sendSimpleMail(employe.getEmail(), "le statut de votre demande de congé à été modifié, veuillez vous connnecter a votre compte pour vérifier"
                    + "\n " + "nouveau statut = " + absence.getStatut(), "le statut de votre absence à changé");
            return ResponseEntity.status(HttpStatus.OK).build();
        }else {return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("seul le manager de cet employé peut changer le statut de son absence");}
    }


    /**
     * change une demande d'absence Initiale ou une absence refusée
     * seul l'employé connecté peut modifier sa demande, il n'a pas le droit d'en modifier le statut.
     *
     *
     *
     * @param absenceDTO
     * @param id
     * @return ResponseEntity :
     *      *                  OK - 200 si ça marche
     *      *                  Unauthorized - 401 sinon
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> modifyAbsence(@RequestBody AbsenceDTO absenceDTO, @PathVariable("id") String id) {
        int absenceIdInt = Integer.parseInt(id);
        Absence absence = absenceService.getAbsenceById(absenceIdInt);

        Employe authEmploye = employeService.getActiveUser();
        Employe employe = absence.getEmploye();


        if (!absence.getDateDebut().isBefore(LocalDate.now())) {

            if (authEmploye.getId() == employe.getId()) {

                if (TypeAbsence.CONGE_SANS_SOLDE.equals(absenceDTO.getTypeAbsence())) {
                    if (Objects.equals(absenceDTO.getMotif(), "")) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("le motif est obligatoire pour un congé sans solde ");
                    }
                }

                List<JoursOff> joursOffList = joursOffService.listJoursOff();

                for (JoursOff joursOff : joursOffList) {
                    if (absence.getDateDebut().isEqual(joursOff.getJour()) || absence.getDateFin().isEqual(joursOff.getJour())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("la date de debut ou de fin ne peut pas être un jour férié ou un rtt employeur");
                    }
                }

                if (absenceDTO.getDateDebut().getDayOfWeek() == DayOfWeek.SATURDAY
                        || absenceDTO.getDateDebut().getDayOfWeek() == DayOfWeek.SUNDAY
                        || absenceDTO.getDateFin().getDayOfWeek() == DayOfWeek.SATURDAY
                        || absenceDTO.getDateFin().getDayOfWeek() == DayOfWeek.SUNDAY) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("la date de début ou de fin ne peut pas être en weekend ");
                }

                List<Absence> absenceList = absenceService.listAbsenceByEmploye(absence.getEmploye().getId());
                boolean superpositionDeDate = false;
                List<LocalDate> datesDemandes = new ArrayList<>();
                try {
                    datesDemandes = absenceDTO.getDateDebut().datesUntil(absenceDTO.getDateFin().plusDays(1)).toList();
                } catch (Exception ex) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("la date de fin ne peut pas être avant celle de début ");
                }


                if (absence.getDateCreation().isAfter(absenceDTO.getDateDebut().atTime(LocalTime.now()))) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("la date de debut ne peut pas être passée ");
                }


                absence.setTypeAbsence(absenceDTO.getTypeAbsence());
                absence.setMotif(absenceDTO.getMotif());
                absence.setDateDebut(absenceDTO.getDateDebut());
                absence.setDateFin(absenceDTO.getDateFin());
                absence.setStatut(Statut.INITIALE);
                absence.setDateCreation(LocalDateTime.now());

                absenceService.addAbsence(absence);

                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("seul l'employé peut modifier ses demandes d'absence'");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("vous ne pouvez pas modifier une absence passée");
        }
    }
    /**supprime une absence,
     * seul l'employé connecté peut supprimer ses absences
     *
     * @param absenceId
     * @return ResponseEntity :
     *      *                  OK - 200 si ça marche
     *      *                  Unauthorized - 401 sinon
     */
    @RequestMapping(value="/{id}", method={RequestMethod.DELETE, RequestMethod.GET})
//    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAbsence(@PathVariable("id") String absenceId) {
        Employe authEmploye = employeService.getActiveUser();
        Absence absence = absenceService.getAbsenceById(Integer.parseInt(absenceId));
        Employe employe = absence.getEmploye();
        if (!absence.getDateDebut().isBefore(LocalDate.now())) {
            if (authEmploye.getId() == employe.getId()) {

                int jourTotal = absenceService.nbJourOuvre(absenceService.getAbsenceById(Integer.parseInt(absenceId)));
                if (absence.getStatut().equals(Statut.VALIDEE) || absence.getStatut().equals(Statut.EN_ATTENTE)) {
                    if (absence.getTypeAbsence().equals(TypeAbsence.RTT)) {
                        System.out.println(jourTotal+"absenceRTT  "+absence.getStatut() );
                        employe.setSoldeRtt(employe.getSoldeRtt() + jourTotal);
                    }
                    if (absence.getTypeAbsence().equals(TypeAbsence.CONGE_PAYE)) {
                        employe.setSoldeConge(employe.getSoldeConge() + jourTotal);
                    }
                }
                employeService.addEmploye(employe);
                absenceService.deleteAbsence(Integer.parseInt(absenceId));
                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("seul l'employé concerné peut supprimer une demande d'absence");
            }
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("vous ne pouvez pas retirer une absence passée");
        }
    }

}
