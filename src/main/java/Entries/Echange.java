package Entries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Echange {

    private int idEchange;
    private int piecesOrProposees;
    private int piecesOrDemandees;
    private Status status;
    private int recepteur;
    private int envoyeur;
    private ArrayList<Carte> cartesDemandees;
    private ArrayList<Carte> cartesProposees;
    private boolean utilCourantRecepteur;

    public Echange(int idEchange, int piecesOrProposees, int piecesOrDemandees, int recepteur, int envoyeur,
                   ArrayList<Carte> cartesDemandees, ArrayList<Carte> cartesProposees, boolean utilCourantRecepteur) {
        this.idEchange = idEchange;
        this.piecesOrProposees = piecesOrProposees;
        this.piecesOrDemandees = piecesOrDemandees;
        this.recepteur = recepteur;
        this.envoyeur = envoyeur;
        this.status=Status.en_attente;
        this.cartesDemandees = cartesDemandees;
        this.cartesProposees = cartesProposees;
        this.utilCourantRecepteur = utilCourantRecepteur;
    }

    public Echange(int idEchange) {
        this.idEchange = idEchange;
        piecesOrProposees = 0;
        piecesOrDemandees=0;
        status=Status.en_attente;
        recepteur=0;
        envoyeur=0;
        cartesDemandees= new ArrayList<>();
        cartesProposees= new ArrayList<>();
        utilCourantRecepteur= false;
    }

    public int getIdEchange() {
        return idEchange;
    }

    public void setIdEchange(int idEchange) {
        this.idEchange = idEchange;
    }

    public int getPiecesOrProposees() {
        return piecesOrProposees;
    }

    public void setPiecesOrProposees(int piecesOrProposees) {
        this.piecesOrProposees = piecesOrProposees;
    }

    public int getPiecesOrDemandees() {
        return piecesOrDemandees;
    }

    public void setPiecesOrDemandees(int piecesOrDemandees) {
        this.piecesOrDemandees = piecesOrDemandees;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getRecepteur() {
        return recepteur;
    }

    public void setRecepteur(int recepteur) {
        this.recepteur = recepteur;
    }

    public int getEnvoyeur() {
        return envoyeur;
    }

    public void setEnvoyeur(int envoyeur) {
        this.envoyeur = envoyeur;
    }

    public ArrayList<Carte> getCartesDemandees() {
        return cartesDemandees;
    }

    public void setCartesDemandees(ArrayList<Carte> cartesDemandees) {
        this.cartesDemandees = cartesDemandees;
    }

    public ArrayList<Carte> getCartesProposees() {
        return cartesProposees;
    }

    public void setCartesProposees(ArrayList<Carte> cartesProposees) {
        this.cartesProposees = cartesProposees;
    }

    public boolean isUtilCourantRecepteur() {
        return utilCourantRecepteur;
    }

    public void setUtilCourantRecepteur(boolean utilCourantRecepteur) {
        this.utilCourantRecepteur = utilCourantRecepteur;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Echange echange = (Echange) o;
        return idEchange == echange.idEchange;
    }


}
