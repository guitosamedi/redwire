package dev.back.DTO;

import dev.back.entite.Departement;
import dev.back.entite.Employe;
//import dev.back.entite.Role;
import lombok.Data;

import java.util.List;
@Data
public class EmployeDTO {
    String firstName;
    String lastName;
    String password;

    int soldeConge;
    int soldeRtt;

    String email;

    List<String> roles;

    Integer departementId;

    Integer managerId;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSoldeConge() {
        return soldeConge;
    }

    public void setSoldeConge(int soldeConge) {
        this.soldeConge = soldeConge;
    }

    public int getSoldeRtt() {
        return soldeRtt;
    }

    public void setSoldeRtt(int soldeRtt) {
        this.soldeRtt = soldeRtt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Integer getDepartementId() {
        return departementId;
    }

    public void setDepartementId(Integer departementId) {
        this.departementId = departementId;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public EmployeDTO(String firstName, String lastName, String password, int soldeConge, int soldeRtt, String email, List<String> roles, Integer departementId, Integer managerId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.soldeConge = soldeConge;
        this.soldeRtt = soldeRtt;
        this.email = email;
        this.roles = roles;
        this.departementId = departementId;
        this.managerId = managerId;
    }
}
