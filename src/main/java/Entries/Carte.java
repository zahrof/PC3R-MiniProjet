package Entries;

import io.pokemontcg.model.Card;

import java.util.Objects;

public class Carte {



    private int idCard;
    /**
     * Le code de reference de la carte.
     */
    private String codeCarte;

    /**
     * Le prix d'achat de la carte.
     */
    private int prix;

    /**
     * Le nombre de points pour obtenir la carte. Afin de rendre le projet plus interessant,
     * le prix sera le double des points, et les points representera l'attribut 'number' de cards,
     * sinon nous serions obligé de stocker toutes les cartes de l'api et leur attribuer
     * statiquement une prix et le nombre de points
     */
    private int points;

    public Card getCard() {
        return card;
    }

    private Card card;

    public Carte(String codeCarte, int prix, int points, Card card) {
        this.codeCarte = codeCarte;
        this.prix = prix;
        this.points = points;
        this.card = card;
    }

    public Carte(String codeCarte) {
        this.codeCarte = codeCarte;
        this.prix=10;
        this.points=10;
    }

    public String getCodeCarte() {
        return codeCarte;
    }

    public void setCodeCarte(String codeCarte) {
        this.codeCarte = codeCarte;
    }

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getIdCard() {
        return idCard;
    }

    public void setIdCard(int idCard) {
        this.idCard = idCard;
    }

}
