package gestion_hotel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestGestionHotel {
    private Hotel hotel;
    private Chambre chambre101;
    private Chambre chambre102;
    private Client client;

    @BeforeEach
    void setUp() {
        var hotelTest = new Hotel("Hotel Bonbon", "Antananarivo");
        var chambreA = new Chambre(101, TypeChambre.FAMILIALE, Map.of(TypeLit.DOUBLE, 1, TypeLit.SIMPLE, 2), 18f, StatutChambre.LIBRE);
        var chambreB = new Chambre(102, TypeChambre.COUPLE, Map.of(TypeLit.SIMPLE, 1), 25f, StatutChambre.LIBRE);
        var chambreC = new Chambre(103, TypeChambre.SUITE, Map.of(TypeLit.DOUBLE, 1), 20f, StatutChambre.LIBRE);
        var clientTest = new Client("Rakoto", "Jean", "0341234567");

        hotelTest.ajouterChambre(chambreA);
        hotelTest.ajouterChambre(chambreB);
        hotelTest.ajouterChambre(chambreC);

        this.hotel = hotelTest;
        this.chambre101 = chambreA;
        this.chambre102 = chambreB;
        this.client = clientTest;
    }

    @Test
    void ajouterChambre() {
        assertEquals(3, hotel.getChambres().size());
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

        assertEquals(2, disponibles.size());
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
    void trouverReservation_existante() {
        var arrivee = LocalDate.of(2026, 7, 11);
        var depart = LocalDate.of(2026, 7, 15);
        var reservation = hotel.faireReservation(client, chambre101, arrivee, depart);

        var trouvee = hotel.trouverReservation(reservation.getId());

        assertNotNull(trouvee);
        assertEquals(reservation.getId(), trouvee.getId());
    }

    @Test
    void trouverReservation_inexistante() {
        var trouvee = hotel.trouverReservation(999);
        assertNull(trouvee);
    }

    @Test
    void reservationsParClient() {
        var arrivee1 = LocalDate.of(2026, 7, 11);
        var depart1 = LocalDate.of(2026, 7, 15);
        var arrivee2 = LocalDate.of(2026, 8, 1);
        var depart2 = LocalDate.of(2026, 8, 5);

        hotel.faireReservation(client, chambre101, arrivee1, depart1);
        hotel.faireReservation(client, chambre102, arrivee2, depart2);

        var historique = hotel.reservationsParClient(client);

        assertEquals(2, historique.size());
    }

    @Test
    void estActiveAujourdhui_reservationEnCours() {
        var arrivee = LocalDate.of(2026, 7, 1);
        var depart = LocalDate.of(2026, 7, 10);
        var reservation = hotel.faireReservation(client, chambre101, arrivee, depart);

        assertTrue(reservation.estActiveAujourdhui());
    }

    @Test
    void estActiveAujourdhui_reservationPassee() {
        var arrivee = LocalDate.of(2026, 6, 1);
        var depart = LocalDate.of(2026, 6, 10);
        var reservation = hotel.faireReservation(client, chambre101, arrivee, depart);

        assertFalse(reservation.estActiveAujourdhui());
    }

    @Test
    void estActiveAujourdhui_reservationAnnulee() {
        var arrivee = LocalDate.of(2026, 7, 1);
        var depart = LocalDate.of(2026, 7, 10);
        var reservation = hotel.faireReservation(client, chambre101, arrivee, depart);
        hotel.annulerReservation(reservation.getId());

        assertFalse(reservation.estActiveAujourdhui());
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
    void voirFacturesImpayees() {
        var arrivee = LocalDate.of(2026, 7, 11);
        var depart = LocalDate.of(2026, 7, 15);
        hotel.faireReservation(client, chambre101, arrivee, depart);

        var impayees = hotel.getFactures().stream()
                .filter(f -> !f.isPayee())
                .toList();

        assertEquals(1, impayees.size());
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

    @Test
    void peutAccueillir_capaciteSuffisante() {
        assertTrue(chambre102.peutAccueillir(1));
    }

    @Test
    void peutAccueillir_capaciteInsuffisante() {
        assertFalse(chambre102.peutAccueillir(2));
    }

    @Test
    void peutAccueillir_chambresFamiliale() {
        assertTrue(chambre101.peutAccueillir(4));
        assertFalse(chambre101.peutAccueillir(5));
    }
}
