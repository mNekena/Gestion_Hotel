package gestion_hotel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Reservation {
    private int id;
    private Client client;
    private Chambre chambre;
    private LocalDate dateArrivee;
    private LocalDate dateDepart;
    private StatutReservation statut;

    public boolean chevauche(LocalDate autreArrivee, LocalDate autreDepart) {
        return autreArrivee.isBefore(this.dateDepart) && autreDepart.isAfter(this.dateArrivee);
    }

    public long getNombreNuits() {
        return java.time.temporal.ChronoUnit.DAYS.between(dateArrivee, dateDepart);
    }
}
