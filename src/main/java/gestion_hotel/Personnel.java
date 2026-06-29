package gestion_hotel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Personnel {
    private int id;
    private String nom;
    private String prenom;
    private String poste;
    private double salaire;
}
