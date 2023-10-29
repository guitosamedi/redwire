package dev.back.control;


import dev.back.DTO.DepartementDTO;
import dev.back.entite.Absence;
import dev.back.entite.Departement;
import dev.back.service.DepartementService;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("departement")
public class DepartementControl {

    DepartementService departementService;



    public DepartementControl(DepartementService departementService) {
        this.departementService = departementService;
    }


    /**
     *
     * @return liste de tous les departements de l'entrprise
     */
    @GetMapping
    public List<Departement> listAll(){
       return departementService.listDepartements();
    }


    /**
     * permet de creer de departements dans l'entreprise, seul un admin connecté est autorisé.
     *
     * @param departementDTO
     * @return ResponseEntity created - 201
     */
    @PostMapping
    public ResponseEntity<?> addDepartement(@RequestBody DepartementDTO departementDTO){
        Departement departement= new Departement(departementDTO.getName());
        departementService.addDepartement(departement);

        return   ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.TEXT_PLAIN).build();
    }

    /**
     * permet de changer le nom d'un departement dans l'entreprise, seul un admin connecté est autorisé.
     *
     * @param departement
     * @return ResponseEntity ok - 200
     */
    @PutMapping
    public ResponseEntity<?>  ChangeDepartement(@RequestBody Departement departement) {
        Departement departement1= departementService.getDepartementById(departement.getId());
        departement1.setName(departement.getName());
        departementService.addDepartement(departement);
        return   ResponseEntity.status(HttpStatus.OK).build();

  }


    /**
     * permet de supprimer un departement dans l'entreprise, seul un admin connecté est autorisé.
     *
     * @param departementId
     * @return ResponseEntity ok - 200
     */

    @RequestMapping("/{id}")
    @DeleteMapping
    public ResponseEntity<?> deleteDepartement(@PathVariable("id") String departementId){
       departementService.deleteDepartement(Integer.parseInt(departementId));
        return   ResponseEntity.status(HttpStatus.OK).build();
    }

}


