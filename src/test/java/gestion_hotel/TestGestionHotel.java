package gestion_hotel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TestGestionHotel {
    private Hotel hotel;
    private Chambre chambre101;
    private Chambre chambre102;
    private Client client;

    @BeforeEach
    void setUp() {
        var hotelTest = new Hotel("Hotel Carlton", "Antananarivo");
        var chambreA = new Chambre(101, "Simple", 50000f, 1, 18f, StatutChambre.LIBRE);
        var chambreB = new Chambre(102, "Double", 80000f, 2, 25f, StatutChambre.LIBRE);
        var clientTest = new Client("Rakoto", "Jean", "0341234567");

        hotelTest.ajouterChambre(chambreA);
        hotelTest.ajouterChambre(chambreB);

        this.hotel = hotelTest;
        this.chambre101 = chambreA;
        this.chambre102 = chambreB;
        this.client = clientTest;
    }

    @Test
    void ajouterChambre() {
        assertEquals(2, hotel.getChambres().size());
    }

    @Test
    void estDisponible_sansReservation() {
        var arrivee = LocalDate.of(2026, 7, 11);
        var depart = LocalDate.of(2026, 7, 15);

        assertTrue(hotel.estDisponible(chambre101, arrivee, depart));
    }

    @Test
    void faireReservation_succes() {
        var arrivee = LocalDate.of(2026, 7, 11);
        var depart = LocalDate.of(2026, 7, 15);

        var reservation = hotel.faireReservation(client, chambre101, arrivee, depart);

        assertNotNull(reservation);
        assertEquals(StatutReservation.CONFIRMEE, reservation.getStatut());
        assertEquals(StatutChambre.RESERVEE, chambre101.getStatut());
        assertEquals(1, hotel.getFactures().size());
    }

    @Test
    void faireReservation_chevauchement_refuse() {
        var arriveeInitiale = LocalDate.of(2026, 7, 11);
        var departInitial = LocalDate.of(2026, 7, 15);
        hotel.faireReservation(client, chambre101, arriveeInitiale, departInitial);

        var arriveeConflit = LocalDate.of(2026, 7, 13);
        var departConflit = LocalDate.of(2026, 7, 16);

        var deuxiemeReservation = hotel.faireReservation(client, chambre101, arriveeConflit, departConflit);

        assertNull(deuxiemeReservation);
    }

    @Test
    void faireReservation_datesAdjacentes_accepte() {
        var arriveeInitiale = LocalDate.of(2026, 7, 11);
        var departInitial = LocalDate.of(2026, 7, 15);
        hotel.faireReservation(client, chambre101, arriveeInitiale, departInitial);

        var arriveeSuivante = LocalDate.of(2026, 7, 15);
        var departSuivant = LocalDate.of(2026, 7, 18);

        var reservationSuivante = hotel.faireReservation(client, chambre101, arriveeSuivante, departSuivant);

        assertNotNull(reservationSuivante);
    }

    @Test
    void chambresDisponibles() {
        var arrivee = LocalDate.of(2026, 7, 11);
        var depart = LocalDate.of(2026, 7, 15);

        hotel.faireReservation(client, chambre101, arrivee, depart);

        var disponibles = hotel.chambresDisponibles(arrivee, depart);

        assertEquals(1, disponibles.size());
        assertEquals(chambre102, disponibles.get(0));
    }

    @Test
    void annulerReservation() {
        var arrivee = LocalDate.of(2026, 7, 11);
        var depart = LocalDate.of(2026, 7, 15);
        var reservation = hotel.faireReservation(client, chambre101, arrivee, depart);

        var resultat = hotel.annulerReservation(reservation.getId());

        assertTrue(resultat);
        assertEquals(StatutReservation.ANNULEE, reservation.getStatut());
        assertEquals(StatutChambre.LIBRE, chambre101.getStatut());
    }

    @Test
    void annulerReservation_idInexistant() {
        var resultat = hotel.annulerReservation(999);
        assertFalse(resultat);
    }

    @Test
    void reservationApresAnnulation_disponible() {
        var arrivee = LocalDate.of(2026, 7, 11);
        var depart = LocalDate.of(2026, 7, 15);
        var premiereReservation = hotel.faireReservation(client, chambre101, arrivee, depart);
        hotel.annulerReservation(premiereReservation.getId());

        var nouvelleReservation = hotel.faireReservation(client, chambre101, arrivee, depart);

        assertNotNull(nouvelleReservation);
    }

    @Test
    void genererFacture_montantCorrect() {
        var arrivee = LocalDate.of(2026, 7, 11);
        var depart = LocalDate.of(2026, 7, 15); // 4 nuits
        hotel.faireReservation(client, chambre101, arrivee, depart);

        var facture = hotel.getFactures().get(0);

        assertEquals(4 * chambre101.getPrixParNuit(), facture.getMontant());
        assertFalse(facture.isPayee());
    }

    @Test
    void payerFacture_succes() {
        var arrivee = LocalDate.of(2026, 7, 11);
        var depart = LocalDate.of(2026, 7, 15);
        hotel.faireReservation(client, chambre101, arrivee, depart);
        var facture = hotel.getFactures().get(0);

        var resultat = hotel.payerFacture(facture.getId(), "Mobile");

        assertTrue(resultat);
        assertTrue(facture.isPayee());
        assertEquals("Mobile", facture.getModePaiement());
    }

    @Test
    void payerFacture_dejaPayee_refuse() {
        var arrivee = LocalDate.of(2026, 7, 11);
        var depart = LocalDate.of(2026, 7, 15);
        hotel.faireReservation(client, chambre101, arrivee, depart);
        var facture = hotel.getFactures().get(0);
        hotel.payerFacture(facture.getId(), "Cash");

        var deuxiemePaiement = hotel.payerFacture(facture.getId(), "Bancaire");

        assertFalse(deuxiemePaiement);
    }

    @Test
    void enregistrerEtSupprimerPersonnel() {
        var gerant = new Gerant(1, "Razafy", "Marie", 1500000);

        hotel.enregistrerPersonnel(gerant);
        var resultat = hotel.supprimerPersonnel(1);

        assertTrue(resultat);
    }

    @Test
    void supprimerPersonnel_idInexistant() {
        var resultat = hotel.supprimerPersonnel(999);
        assertFalse(resultat);
    }
}
