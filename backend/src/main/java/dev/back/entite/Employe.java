package dev.back.entite;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Employe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firstName;
    private String lastName;
    private String password;
    private int soldeConge;
    private int soldeRtt;

    @Email
    @Column(unique = true)
    //un email ne peut être utilisé que par un seul employé
    private String email;


    @ManyToOne
    private Departement departement;


    //chaque employé à un manager, il existe un unique superManager qui a manager=null
    @ManyToOne
    private Employe manager;


    @ElementCollection(fetch = FetchType.EAGER)
    //role = null : juste un employé
    private List<String> roles;

    public Employe(String firstName, String lastName, String password, int soldeConge, int soldeRtt, String email, List<String> roles, Departement departement, Employe manager) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.soldeConge = soldeConge;
        this.soldeRtt = soldeRtt;
        this.email = email;
        this.departement = departement;
        this.manager = manager;
        this.roles = roles;

    }


}
