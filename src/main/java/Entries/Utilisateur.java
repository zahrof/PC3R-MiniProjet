package Entries;

import ConnectionClases.ConnectionManager;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import static Services.GestionAmis.recupererInfoAmi;

public class Utilisateur {

    private int login;
    private String birthDate;
    private String name;
    private String surname;
    private String password;
    private String mail;
    private int piecesOr;

    public Set<Utilisateur> getAmis() {
        return amis;
    }

    public void setAmis(Set<Utilisateur> amis) {
        this.amis = amis;
    }

    private Set<Utilisateur> amis = new HashSet<>();

    public Utilisateur(String birthDate, String name, String surname, String password, String mail) {
        this.birthDate = birthDate;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.mail = mail;
        this.piecesOr=10;

    }



    public Utilisateur(String birthDate, String name, String surname, String password, String mail, int piecesOr) {
        this.birthDate = birthDate;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.mail = mail;
        this.piecesOr=piecesOr;

    }

    public Utilisateur() {
        this.login=0;
        this.birthDate="";
        this.name="";
        this.surname="";
        this.password="";
        this.mail="";
        this.piecesOr=10;
        majPiecesOrBDD(10);
    }

    public Utilisateur(int login, String birthDate, String name, String surname, String mail) {
        this.login = login;
        this.birthDate = birthDate;
        this.name = name;
        this.surname = surname;
        this.mail = mail;
    }


    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean valid;

    public int getLogin() {
        return login;
    }

    public void setLogin(int login) {
        this.login = login;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getPiecesOr() {
        return piecesOr;
    }

    public void setPiecesOr(int piecesOr) {
        this.piecesOr = piecesOr;
    }

    public void majPiecesOrBDD(int piecesOr){
        Connection currentCon = null;
        Statement stmt;
        String query = "update User set goldenCoins="+piecesOr+"where login="+this.login;
        try {
            currentCon = ConnectionManager.getConnection();
            stmt = currentCon.createStatement();
            stmt.executeUpdate(query);


        } catch (Exception ex) {
            System.out.println("update failed: An Exception has occurred! " + ex);
        }
    }







}
