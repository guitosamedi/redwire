package dev.back.control;

import dev.back.DTO.*;
import dev.back.entite.Absence;
import dev.back.entite.Departement;
import dev.back.entite.Employe;
import dev.back.service.DepartementService;
import dev.back.service.EmployeService;

import org.apache.tomcat.util.json.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("employe")
public class EmployeControl {

    EmployeService employeService;


    DepartementService departementService;
    PasswordEncoder passwordEncoder;


    public EmployeControl(EmployeService employeService, DepartementService departementService, PasswordEncoder passwordEncoder) {
        this.employeService = employeService;
        this.departementService = departementService;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping("active")
    public ResponseEntity<?> listActive(){
        Employe activeUser = employeService.getActiveUser();
        Employe employe = new Employe();
        employe.setSoldeRtt(activeUser.getSoldeRtt());
        employe.setSoldeConge(activeUser.getSoldeConge());
        employe.setRoles(activeUser.getRoles());
        employe.setFirstName(activeUser.getFirstName());
        employe.setLastName(activeUser.getLastName());
        employe.setId(activeUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(employe);
    }

    @GetMapping
    public List<Employe> listAll(){
        return employeService.listEmployes();
    }



    /**
     * permet d'ajouter un employé en base de donnée
     * seul un admin est autorisé
     *
     * @param employeDTO
     * @return ResponseEntity created - 201
     *
     *
     */
    @PostMapping
    public ResponseEntity<?> addEmploye(@RequestBody EmployeDTO employeDTO){

        System.out.println(employeDTO.toString());

        String pswEncoded = passwordEncoder.encode(employeDTO.getPassword());
        Employe manager = employeService.getEmployeById(employeDTO.getManagerId());
        Departement departement = departementService.getDepartementById(employeDTO.getDepartementId());

        Employe employe = new Employe(
                employeDTO.getFirstName(),
                employeDTO.getLastName(),
                pswEncoded,
                employeDTO.getSoldeConge(),
                employeDTO.getSoldeRtt(),
                employeDTO.getEmail(),
                employeDTO.getRoles(),
                departement,
                manager);

        employeService.addEmploye(employe);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * permet à l'employé connecté de changer son mot de passe s'il le souhaite
     *
     * @param employe
     * @param employeId
     * @return  ResponseEntity :
     *                      ok - 200
     *                      unauthorized - 401
     */

    @RequestMapping("/newpassword/{id}")
    @PostMapping
    public ResponseEntity<?> changeEmployePassword(@RequestBody EmployeDTO employe,@PathVariable("id") String employeId){
        Employe authEmploye = employeService.getActiveUser();

        if (authEmploye.getId() == Integer.parseInt(employeId)) {

        String pswEncoded = passwordEncoder.encode(employe.getPassword());
        Employe employe1= employeService.getEmployeById(Integer.parseInt(employeId));
        employe1.setPassword(pswEncoded);
        employeService.addEmploye(employe1);
        return   ResponseEntity.status(HttpStatus.OK).build();
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("vous n'êtes pas autorisé à changer le mot de passe de quelqu'un d'autre");}
        }

    /**
     * permet à un admin de modifier les information personnelles (hors mdp) d'un employe
     *
     * @param employeDTO
     * @param employeId
     * @return  ResponseEntity
     *                      ok - 200
     */


    @PutMapping("/{id}")
    public ResponseEntity<?> testPut(@RequestBody EmployeDTO employeDTO, @PathVariable("id") String employeId) {

            Employe employe1 = employeService.getEmployeById(Integer.parseInt(employeId));
            employe1.setFirstName(employeDTO.getFirstName());
            employe1.setLastName(employeDTO.getLastName());
            employe1.setManager(employeService.getEmployeById(employeDTO.getManagerId()));
            employe1.setDepartement(departementService.getDepartementById(employeDTO.getDepartementId()));
            employe1.setRoles(employeDTO.getRoles());
            employe1.setSoldeConge(employeDTO.getSoldeConge());
            employe1.setSoldeRtt(employeDTO.getSoldeRtt());
            employeService.addEmploye(employe1);

            return ResponseEntity.status(HttpStatus.OK).build();
    }



    /**
     * permet à un admin de supprimer un employe de la base de donnée
     *
     * @param employeId
     * @return  ResponseEntity
     *                      ok - 200
     */

    @RequestMapping("/{id}")
    @DeleteMapping
    public ResponseEntity<?> deleteEmploye(@PathVariable("id") String employeId){
        employeService.deleteEmploye(Integer.parseInt(employeId));
        return   ResponseEntity.status(HttpStatus.OK).build();
    }


}

