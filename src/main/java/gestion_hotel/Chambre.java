package gestion_hotel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Chambre {
    private int numChambre;
    private String type;
    private float prixParNuit;
    private int nombreLit;
    private float superficie;
    private StatutChambre statut;
}
