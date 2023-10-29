package dev.back.service;

import dev.back.entite.JoursOff;
import dev.back.entite.TypeJour;
import dev.back.repository.JoursOffRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class JoursOffService {
    @Autowired
    JoursOffRepo joursOffRepo;
    public List<JoursOff> listJoursOff() {
        return joursOffRepo.findAll();
    }
    public JoursOff jourOffByDatee(LocalDate jour){
        return  joursOffRepo.findByJour(jour).orElseThrow();
    }
    /**
     * utilise .save donc permet de créer ET de modifier
     * @param joursOff
     */
    @Transactional
    public void addJourOff(JoursOff joursOff) {
        joursOffRepo.save(joursOff);
    }

    public void deleteJourOff(JoursOff joursOff){joursOffRepo.delete(joursOff);}

    public JoursOff getJourOffById( int id){return joursOffRepo.findById(id).orElseThrow();}

    public void deleteJourOff(int id){joursOffRepo.delete(joursOffRepo.findById(id).orElseThrow());}

    /**
     * utilise l'api du gouvernement pour ajouter les jours fériés
     * pour la métropole est pour une année donnée en base de donnée
     * s'il existe déja quelque chose à cette date, rien n'est ajouté.
     *
     * @param year
     */
    public void fetchAndSaveJoursFeries(int year) {
        RestTemplate restTemplate = new RestTemplate();
        //on suppose que tous nos employés sont en france metropolitaine
        String apiUrl = "https://calendrier.api.gouv.fr/jours-feries/metropole/{annee}.json";
        String apiUrlWithYear = apiUrl.replace("{annee}", String.valueOf(year));
        Map<String, String> joursFeriesData = restTemplate.getForObject(apiUrlWithYear, Map.class);
        List<JoursOff> joursOffList = joursOffRepo.findAllByTypeJour(TypeJour.JOUR_FERIE);
        for (Map.Entry<String, String> entry : joursFeriesData.entrySet()) {
            JoursOff jourFerie = new JoursOff();
            if(joursOffRepo.findByJour(LocalDate.parse(entry.getKey())).isEmpty()){
                jourFerie.setTypeJour(TypeJour.JOUR_FERIE);
                jourFerie.setJour(LocalDate.parse(entry.getKey()));
                jourFerie.setDescription(entry.getValue());

                joursOffRepo.save(jourFerie);
            }
        }
    }
}