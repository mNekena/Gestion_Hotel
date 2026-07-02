package gestion_hotel;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@AllArgsConstructor
@Getter
public class Hotel {
    private String nom;
    private String adresse;
    private List<Chambre> chambres = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();
    private List<Personnel> personnels = new ArrayList<>();
    private List<Facture> factures = new ArrayList<>();

    private int prochainIdReservation = 1;
    private int prochainIdFacture = 1;

    public Hotel(String nom, String adresse) {
        this.nom = nom;
        this.adresse = adresse;
    }

    public void ajouterChambre(Chambre c) {
        chambres.add(c);
    }

    //COTE CLIENT
    public void voirDetailsChambres() {
        for (Chambre c : chambres) {
            System.out.println(c);
        }
    }

    public boolean estDisponible(Chambre chambre, LocalDate arrivee, LocalDate depart) {
        for (Reservation r : reservations) {
            if (r.getChambre().getNumChambre() == chambre.getNumChambre()
                    && r.getStatut() != StatutReservation.ANNULEE
                    && r.chevauche(arrivee, depart)) {
                return false;
            }
        }
        return true;
    }

    public List<Chambre> chambresDisponibles(LocalDate arrivee, LocalDate depart) {
        List<Chambre> disponibles = new ArrayList<>();
        for (Chambre c : chambres) {
            if (estDisponible(c, arrivee, depart)) {
                disponibles.add(c);
            }
        }
        return disponibles;
    }

    public Reservation faireReservation(Client client, Chambre chambre, LocalDate arrivee, LocalDate depart) {
        if (!estDisponible(chambre, arrivee, depart)) {
            System.out.println("Chambre " + chambre.getNumChambre() + " indisponible sur cette période.");
            return null;
        }

        Reservation res = new Reservation(
                prochainIdReservation++,
                client,
                chambre,
                arrivee,
                depart,
                StatutReservation.CONFIRMEE
        );
        reservations.add(res);
        chambre.setStatut(StatutChambre.RESERVEE);
        genererFacture(res);

        System.out.println("Réservation " + res.getId() + " créée avec succès.");
        return res;
    }

    public boolean annulerReservation(int idReservation) {
        for (Reservation r : reservations) {
            if (r.getId() == idReservation) {
                r.setStatut(StatutReservation.ANNULEE);
                r.getChambre().setStatut(StatutChambre.LIBRE);
                System.out.println("Réservation " + idReservation + " annulée.");
                return true;
            }
        }
        System.out.println("Réservation non trouvée.");
        return false;
    }

    private void genererFacture(Reservation res) {
        double montant = res.getNombreNuits() * res.getChambre().getPrixParNuit();
        Facture f = new Facture(prochainIdFacture++, res, montant);
        factures.add(f);
        System.out.println("Facture " + f.getId() + " générée : " + montant);
    }

    public boolean payerFacture(int idFacture, String modePaiement) {
        for (Facture f : factures) {
            if (f.getId() == idFacture) {
                if (f.isPayee()) {
                    System.out.println("Cette facture est déjà payée.");
                    return false;
                }
                f.payer(modePaiement);
                System.out.println("Facture " + idFacture + " payée par " + modePaiement + ".");
                return true;
            }
        }
        System.out.println("Facture non trouvée.");
        return false;
    }

    //COTE GERANT
    public void voirReservations() {
        for (Reservation r : reservations) {
            System.out.println("Resa " + r.getId()
                    + " - " + r.getClient().getNom() + " " + r.getClient().getPrenom()
                    + " - Chambre " + r.getChambre().getNumChambre()
                    + " du " + r.getDateArrivee() + " au " + r.getDateDepart()
                    + " [" + r.getStatut() + "]");
        }
    }

    public Reservation trouverReservation(int idReservation) {
        for (Reservation r : reservations) {
            if (r.getId() == idReservation) return r;
        }
        System.out.println("Réservation non trouvée.");
        return null;
    }

    public void voirFacturesImpayees() {
        boolean found = false;
        for (Facture f : factures) {
            if (!f.isPayee()) {
                System.out.println("Facture #" + f.getId()
                        + " - " + f.getReservation().getClient().getNom()
                        + " - " + f.getMontant() + " Ar");
                found = true;
            }
        }
        if (!found) System.out.println("Aucune facture impayée.");
    }

    public void voirPersonnels() {
        for (Personnel p : personnels) {
            System.out.println(p);
        }
    }

    public void enregistrerPersonnel(Personnel p) {
        personnels.add(p);
        System.out.println("Personnel ajouté : " + p.getNom() + " " + p.getPrenom());
    }

    public boolean supprimerPersonnel(int idPersonnel) {
        boolean supprime = personnels.removeIf(p -> p.getId() == idPersonnel);
        if (supprime) {
            System.out.println("Personnel " + idPersonnel + " supprimé.");
        } else {
            System.out.println("Personnel non trouvé.");
        }
        return supprime;
    }

    public List<Reservation> reservationsParClient(Client client) {
        List<Reservation> resultat = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getClient().getTelephone().equals(client.getTelephone())) {
                resultat.add(r);
            }
        }
        return resultat;
    }
}

