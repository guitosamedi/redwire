package dev.back.onStartUp;


import dev.back.entite.*;
import dev.back.service.AbsenceService;
import dev.back.service.DepartementService;
import dev.back.service.EmployeService;
import dev.back.service.JoursOffService;
import jakarta.annotation.PostConstruct;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class StartUp{

    EmployeService employeService;
    DepartementService departementService;
    AbsenceService absenceService;

    JoursOffService joursOffService;

    PasswordEncoder passwordEncoder;

    public StartUp(EmployeService employeService, DepartementService departementService, AbsenceService absenceService, JoursOffService joursOffService, PasswordEncoder passwordEncoder) {
        this.employeService = employeService;
        this.departementService = departementService;
        this.absenceService = absenceService;
        this.joursOffService = joursOffService;
        this.passwordEncoder = passwordEncoder;
    }
    /**
     * on rajoute des objets dans notre base de donnée au démarrage
     */
    @EventListener(ContextRefreshedEvent.class)
    public void init() {

        LocalDate date = LocalDate.parse("2024-02-24");

        joursOffService.addJourOff(new JoursOff(date,TypeJour.RTT_EMPLOYEUR,"RTTEMPLOYEUR"));

        departementService.addDepartement(new Departement("ressources humaines"));
        departementService.addDepartement(new Departement("informatique"));
        departementService.addDepartement(new Departement("commercial"));
        departementService.addDepartement(new Departement("magasinier"));


        List<String> roles = new ArrayList<>();
        roles.add("MANAGER");
        roles.add("ADMIN");
        employeService.addEmploye(new Employe("passwordtout","lastname1",passwordEncoder.encode("passwordtout"),10,10,"redwireback@gmail.com",roles,departementService.getDepartementById(1),null));


        roles.remove(1);
        employeService.addEmploye(new Employe("passwordmanager","lastname2",passwordEncoder.encode("passwordmanager"),10,10,"examplemanagermanager@gmail.com",roles,departementService.getDepartementById(1),employeService.getEmployeById(1)));
        roles.remove(0);
        employeService.addEmploye(new Employe("passwordemploye","lastnam3",passwordEncoder.encode("passwordemploye"),10,10,"exampleemployeemploye@gmail.com",null,departementService.getDepartementById(1),employeService.getEmployeById(2)));
        roles.add("ADMIN");
        employeService.addEmploye(new Employe("passwordadmin","lastnam4",passwordEncoder.encode("passwordadmin"),10,10,"exampleadminadmni@gmail.com",roles,departementService.getDepartementById(2),employeService.getEmployeById(1)));
        employeService.addEmploye(new Employe("passwordemploye1","lastname5",passwordEncoder.encode("passwordemploye1"),10,10,"exampleemployeemploye1@gmail.com",null,departementService.getDepartementById(1),employeService.getEmployeById(2)));
        employeService.addEmploye(new Employe("passwordemploye2","lastname6",passwordEncoder.encode("passwordemploye2"),10,10,"exampleemployeemploye2@gmail.com",null,departementService.getDepartementById(1),employeService.getEmployeById(1)));



        joursOffService.fetchAndSaveJoursFeries(2023);
        joursOffService.fetchAndSaveJoursFeries(2024);
        joursOffService.fetchAndSaveJoursFeries(2025);
        joursOffService.fetchAndSaveJoursFeries(2019);



        absenceService.addAbsence(new Absence(LocalDateTime.parse("2023-10-10T01:02:04"),LocalDate.parse("2023-09-01"),LocalDate.parse("2023-09-04"),Statut.EN_ATTENTE,TypeAbsence.RTT,"",employeService.getEmployeById(3)));
        absenceService.addAbsence(new Absence(LocalDateTime.parse("2023-10-10T01:02:04"),LocalDate.parse("2023-09-05"),LocalDate.parse("2023-09-06"),Statut.VALIDEE,TypeAbsence.CONGE_PAYE,"",employeService.getEmployeById(1)));
        absenceService.addAbsence(new Absence(LocalDateTime.parse("2023-12-10T01:02:04"),LocalDate.parse("2023-09-08"),LocalDate.parse("2023-09-08"),Statut.VALIDEE,TypeAbsence.CONGE_PAYE,"",employeService.getEmployeById(1)));
        absenceService.addAbsence(new Absence(LocalDateTime.parse("2023-10-10T01:02:04"),LocalDate.parse("2023-09-18"),LocalDate.parse("2023-09-19"),Statut.EN_ATTENTE,TypeAbsence.CONGE_SANS_SOLDE,"testmotif",employeService.getEmployeById(2)));
        absenceService.addAbsence(new Absence(LocalDateTime.parse("2023-10-10T01:02:04"),LocalDate.parse("2023-09-03"),LocalDate.parse("2023-09-03"),Statut.EN_ATTENTE,TypeAbsence.CONGE_SANS_SOLDE,"testmotif",employeService.getEmployeById(3)));
        absenceService.addAbsence(new Absence(LocalDateTime.parse("2023-10-10T01:02:04"),LocalDate.parse("2023-09-25"),LocalDate.parse("2023-09-27"),Statut.INITIALE,TypeAbsence.RTT,"testmotif",employeService.getEmployeById(4)));
        absenceService.addAbsence(new Absence(LocalDateTime.parse("2023-10-10T01:02:04"),LocalDate.parse("2023-09-28"),LocalDate.parse("2023-09-29"),Statut.INITIALE,TypeAbsence.RTT,"testmotif",employeService.getEmployeById(3)));
        absenceService.addAbsence(new Absence(LocalDateTime.parse("2023-10-10T01:02:04"),LocalDate.parse("2023-12-05"),LocalDate.parse("2023-12-05"),Statut.EN_ATTENTE,TypeAbsence.RTT,"testmotif",employeService.getEmployeById(2)));
        absenceService.addAbsence(new Absence(LocalDateTime.parse("2023-10-10T01:02:04"),LocalDate.parse("2023-12-06"),LocalDate.parse("2023-12-06"),Statut.EN_ATTENTE,TypeAbsence.RTT,"testmotif",employeService.getEmployeById(1)));
        absenceService.addAbsence(new Absence(LocalDateTime.parse("2023-10-10T01:02:04"),LocalDate.parse("2023-12-06"),LocalDate.parse("2023-12-06"),Statut.EN_ATTENTE,TypeAbsence.CONGE_SANS_SOLDE,"testmotif",employeService.getEmployeById(3)));
        absenceService.addAbsence(new Absence(LocalDateTime.parse("2023-10-10T01:02:04"),LocalDate.parse("2023-12-09"),LocalDate.parse("2023-12-09"),Statut.EN_ATTENTE,TypeAbsence.RTT,"testmotif",employeService.getEmployeById(4)));
        absenceService.addAbsence(new Absence(LocalDateTime.parse("2023-10-10T01:02:04"),LocalDate.parse("2023-12-09"),LocalDate.parse("2023-12-09"),Statut.EN_ATTENTE,TypeAbsence.CONGE_SANS_SOLDE,"testmotif",employeService.getEmployeById(2)));
        absenceService.addAbsence(new Absence(LocalDateTime.parse("2023-10-10T01:02:04"),LocalDate.parse("2023-12-10"),LocalDate.parse("2023-12-10"),Statut.EN_ATTENTE,TypeAbsence.CONGE_PAYE,"testmotif",employeService.getEmployeById(1)));
        absenceService.addAbsence(new Absence(LocalDateTime.parse("2023-10-10T01:02:04"),LocalDate.parse("2023-12-11"),LocalDate.parse("2023-12-11"),Statut.EN_ATTENTE,TypeAbsence.CONGE_PAYE,"testmotif",employeService.getEmployeById(1)));
        absenceService.addAbsence(new Absence(LocalDateTime.parse("2023-10-10T01:02:04"),LocalDate.parse("2023-12-12"),LocalDate.parse("2023-12-12"),Statut.EN_ATTENTE,TypeAbsence.RTT,"testmotif",employeService.getEmployeById(4)));
        }
}