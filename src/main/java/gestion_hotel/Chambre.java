package gestion_hotel;

import java.util.Map;
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
    private TypeChambre type;
    private Map<TypeLit, Integer> lits;
    private float superficie;
    private StatutChambre statut;

    public float getPrixParNuit() {
        return type.getPrixParNuit();
    }

    public boolean peutAccueillir(int nombrePersonnes) {
        int capacite = 0;
        for (Map.Entry<TypeLit, Integer> entry : lits.entrySet()) {
            if (entry.getKey() == TypeLit.DOUBLE) capacite += entry.getValue() * 2;
            if (entry.getKey() == TypeLit.SIMPLE) capacite += entry.getValue();
        }
        return capacite >= nombrePersonnes;
    }
}
