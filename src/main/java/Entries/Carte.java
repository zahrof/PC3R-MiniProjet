package Entries;

import ConnectionClases.ConnectionManager;
import io.pokemontcg.model.Card;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Objects;

public class Carte {


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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Carte carte = (Carte) o;
        return Objects.equals(codeCarte, carte.codeCarte) ;
    }




}
