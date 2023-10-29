package dev.back.DTO;

import dev.back.entite.TypeJour;

import java.time.LocalDate;

public class JourOffDTO {
    LocalDate jour;
    TypeJour typeJour;
String description;

    public LocalDate getJour() {
        return jour;
    }

    public void setJour(LocalDate jour) {
        this.jour = jour;
    }

    public TypeJour getTypeJour() {
        return typeJour;
    }

    public void setTypeJour(TypeJour typeJour) {
        this.typeJour = typeJour;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public JourOffDTO(LocalDate jour, TypeJour typeJour, String description) {
        this.jour = jour;
        this.typeJour = typeJour;
        this.description = description;
    }
}
