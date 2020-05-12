package Entries;

public class Invitation {
    public enum Status {
        en_attente,
        accepte,
        refuse,
    }

    private Utilisateur emetteur;
    private Utilisateur recepteur;
    private Status status;
    private String dateCreation;

    public Invitation(Utilisateur emetteur, Utilisateur recepteur, String status, String dateCreation) {
        this.emetteur = emetteur;
        this.recepteur = recepteur;
        switch(status){
            case "en attente":
                this.status = Status.en_attente;
                break;
            case "accepte":
                this.status = Status.accepte;
                break;
            case "refuse":
                this.status = Status.refuse;
                break;
        }

        this.dateCreation = dateCreation;
    }

    public Utilisateur getEmetteur() {
        return emetteur;
    }

    public void setEmetteur(Utilisateur emetteur) {
        this.emetteur = emetteur;
    }

    public Utilisateur getRecepteur() {
        return recepteur;
    }

    public void setRecepteur(Utilisateur recepteur) {
        this.recepteur = recepteur;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }
}
