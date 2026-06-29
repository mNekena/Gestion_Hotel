package gestion_hotel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Facture {
    private int id;
    private Reservation reservation;
    private double montant;
    private boolean payee = false;
    private String modePaiement;

    public Facture(int id, Reservation reservation, double montant) {
        this.id = id;
        this.reservation = reservation;
        this.montant = montant;
    }

    public void payer(String modePaiement) {
        this.payee = true;
        this.modePaiement = modePaiement;
    }
}
