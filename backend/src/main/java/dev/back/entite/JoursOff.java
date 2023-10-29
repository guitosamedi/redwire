package dev.back.entite;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


/**
 * Entite qui représente les jours de RTT obligatoire fournit par l'employeur
 * et les jours fériés fournit par l'état
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
public class JoursOff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    LocalDate jour;
    TypeJour typeJour;
    String description;

    public JoursOff(LocalDate jour, TypeJour typeJour, String description) {
        this.jour = jour;
        this.typeJour = typeJour;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id=id;
    }

    public LocalDate getJour() {
        return jour;
    }

    public void setJour( LocalDate jour ) {
        this.jour=jour;
    }

    public TypeJour getTypeJour() {
        return typeJour;
    }

    public void setTypeJour( TypeJour typeJour ) {
        this.typeJour=typeJour;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description=description;
    }
}