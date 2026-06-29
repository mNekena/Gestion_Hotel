package gestion_hotel;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class Gerant extends Personnel {
    public Gerant(int id, String nom, String prenom, double salaire) {
        super(id, nom, prenom, "Gérant", salaire);
    }
}
