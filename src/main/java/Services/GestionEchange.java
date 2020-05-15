package Services;

import ConnectionClases.ConnectionManager;
import Entries.Carte;
import Entries.Echange;
import Entries.Invitation;
import Entries.Utilisateur;
import OtherClasses.Reponses;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


import static Services.GestionAmis.recupererInfoAmi;
import static Services.GestionCartes.recupererCarteInfo;

public class GestionEchange extends HttpServlet {
    static Utilisateur utilisateurCourant = null;
    static ArrayList<Echange> echangesDeUtilisateurCourant;
    Gson gson;

    public GestionEchange(){super(); }

    public void init(){
        this.gson = new GsonBuilder().serializeNulls().create();
        ServletContext cont = getServletContext();

        if(cont.getAttribute("utilisateurCourant")!=null){
            utilisateurCourant = new Utilisateur();
            utilisateurCourant =(Utilisateur) cont.getAttribute("utilisateurCourant");
        }

        echangesDeUtilisateurCourant = recupererEchangesUtilisateur(utilisateurCourant.getLogin()); 
    }

/*    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String state = request.getParameter("state");
        response.setContentType("application/json");

        if(state.equals("recupererEchanges")){

            if(echangesDeUtilisateurCourant.isEmpty())response.getWriter().write(gson.toJson(null));
            else response.getWriter().write(gson.toJson(echangesDeUtilisateurCourant));
        }
    }*/

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String state = request.getParameter("state");
        response.setContentType("application/json");

        if(state.equals("recupererEchanges")){

            if(echangesDeUtilisateurCourant.isEmpty())response.getWriter().write(gson.toJson(null));
            else response.getWriter().write(gson.toJson(echangesDeUtilisateurCourant));
        }

        if(state.equals("repondeEchange")){
            int idEmeteur = Integer.parseInt(request.getParameter("ideEmeteur"));
            int idEchange = Integer.parseInt(request.getParameter("idEchange"));
            if(contientEchange(echangesDeUtilisateurCourant, idEmeteur, utilisateurCourant.getLogin(),idEchange)){
                boolean accepte = Boolean.parseBoolean(request.getParameter("accepte"));
                if(modifierEchange(idEmeteur, utilisateurCourant.getLogin(),accepte,idEchange)){
                    response.getWriter().write(gson.toJson(new Reponses(true)));
                    System.out.println("Echange réussi");
                }else{
                    response.getWriter().write(gson.toJson(new Reponses(false,
                            "Echange échoué")));
                    System.out.println("Echange échoué");

                }

            }else response.getWriter().write(gson.toJson(new Reponses(false,
                    "Désolé, l'échange n'existe pas ")));
        }
    }

    private boolean modifierEchange(int idEmeteur, int login, boolean accepte, int idEchange) {
        Statement stmt;
        String query = "update Echange set status=";
        Connection currentCon = null;
        for(Echange e : echangesDeUtilisateurCourant){
            if(e.getIdEchange()==idEchange){
                if(accepte){
                    faireEchange(idEchange,idEmeteur,login);
                    query += "'accepte'";
                }else query += "'refuse'";
            }
        }
        query += " where idEchange="+idEchange;
        try {
            currentCon = ConnectionManager.getConnection();
            stmt=currentCon.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return true;

    }

    /**
     * Effectue les changement sur la session Courante et dans le base de données pour effectuer l'echange
     * correctement
     * @param idEchange
     * @param idEmeteur
     * @param login
     */
    private void faireEchange(int idEchange, int idEmeteur, int login) {
        ServletContext cont = getServletContext();
        Echange e = recupererEchange(idEchange,echangesDeUtilisateurCourant);

        // Echange PO dans la session
        int poUtilCourantApresEchange = utilisateurCourant.getPiecesOr();
        poUtilCourantApresEchange += e.getPiecesOrProposees();
        poUtilCourantApresEchange -= e.getPiecesOrDemandees();
        utilisateurCourant.setPiecesOr(poUtilCourantApresEchange);
        cont.setAttribute("utilisateurCourant",utilisateurCourant);
        // Echange PO dans la BDD
        Utilisateur receveur = recupererInfoAmi(login);
        Utilisateur envoyeur = recupererInfoAmi(idEmeteur);
        receveur.majPiecesOrBDD(poUtilCourantApresEchange);
        int poEnvoyeurApresEchange = envoyeur.getPiecesOr();
        poEnvoyeurApresEchange += e.getPiecesOrDemandees();
        poEnvoyeurApresEchange -= e.getPiecesOrProposees();
        envoyeur.majPiecesOrBDD(poEnvoyeurApresEchange);


        //Echange Cartes dans la BDD
        for(Carte c : e.getCartesDemandees()) majProprietaireCarte(idEmeteur,login,c.getCodeCarte());

        for(Carte c : e.getCartesProposees()) majProprietaireCarte(login,idEmeteur,c.getCodeCarte());


    }

    public void majProprietaireCarte(int nouveauProprietaire, int ancienProprio, String cardCode){
        Connection currentCon = null;
        Statement stmt;
        String query = "update CardOwners set login="+nouveauProprietaire+"where login="+ancienProprio+" and cardCode="
                + "'"+cardCode+"'";
        try {
            currentCon = ConnectionManager.getConnection();
            stmt = currentCon.createStatement();
            stmt.executeUpdate(query);


        } catch (Exception ex) {
            System.out.println("update failed: An Exception has occurred! " + ex);
        }
    }


    private Echange recupererEchange(int idEchange, ArrayList<Echange> echangesDeUtilisateurCourant) {
        Echange e =null;
        for(Echange ec : echangesDeUtilisateurCourant){
            if(ec.getIdEchange()==idEchange) e=ec;
        }
        return e;
    }

    private boolean contientEchange(ArrayList<Echange> echangesDeUtilisateurCourant, int idEmeteur, int login,
                                    int idEchange) {
        boolean contient = false;
        for(Echange e : echangesDeUtilisateurCourant){
            if(e.getEnvoyeur()==idEmeteur && e.getRecepteur()==login && e.getIdEchange()==idEchange)
                contient = true;
        }
        return  contient;
    }

    private ArrayList<Echange> recupererEchangesUtilisateur(int login) {
        ArrayList<Echange> echanges = new ArrayList<>();
        ResultSet resultatEchanges;
        Statement stmt;
        String query =" select t1.receptor,t1.sender, t1.idEchange,t1.goldCoinsAsked,t1.goldCoinsOffered," +
                "CartesDemandeEchange.cardCodeDemande, CartesOffertesEchange.cardCodeOffered from  Echange t1 " +
                "inner join CartesDemandeEchange on t1.idEchange=CartesDemandeEchange.idEchange and( t1.receptor="+
                login+ " or t1.sender="+login+")inner join CartesOffertesEchange on t1.idEchange=CartesOffertesEchange.idEchange " +
                "and CartesDemandeEchange.idEchange=CartesOffertesEchange.idEchange and( t1.receptor=" +
                        login+ " or t1.sender="+login+")";

        Connection currentCon = null;
        currentCon = ConnectionManager.getConnection();
        try {
            stmt=currentCon.createStatement();
            resultatEchanges = stmt.executeQuery(query);

            while(resultatEchanges.next()) {
                int idEchange = resultatEchanges.getInt("idEchange");
                Echange echangeAux = new Echange(idEchange);

                // si on a pas encore crée cet echange on en crée un tout neuf
                if (!echanges.contains(echangeAux)){
                    //maybe factoriser
                    ArrayList<Carte> cartesDemandees = new ArrayList<>();
                    String codeCarteDemande = resultatEchanges.getString("cardCodeDemande");
                    if(codeCarteDemande!=null) {
                        Carte carteDemande=null;
                         while (carteDemande==null)carteDemande = recupererCarteInfo(codeCarteDemande);
                        if (carteDemande!= null) cartesDemandees.add(carteDemande);
                    }

                    ArrayList<Carte> cartesProposees = new ArrayList<>();
                    String codeCarteProposee = resultatEchanges.getString("cardCodeOffered");
                    if(codeCarteProposee!=null) {
                        Carte carteProposee = null;
                        while (carteProposee==null) carteProposee= recupererCarteInfo(codeCarteProposee);
                        if (carteProposee!= null) cartesProposees.add(carteProposee);
                    }

                    boolean utilCourantRecepteur = false;
                    if(resultatEchanges.getInt("receptor")==login) utilCourantRecepteur=true;

                    echangeAux.setPiecesOrProposees(resultatEchanges.getInt("goldCoinsOffered"));
                    echangeAux.setPiecesOrDemandees( resultatEchanges.getInt("goldCoinsAsked"));
                    echangeAux.setRecepteur(resultatEchanges.getInt("receptor"));
                    echangeAux.setEnvoyeur(resultatEchanges.getInt("sender"));
                    echangeAux.setCartesDemandees(cartesDemandees);
                    echangeAux.setCartesProposees(cartesProposees);
                    echangeAux.setUtilCourantRecepteur(utilCourantRecepteur);

                }
                // echange déjà crée donc on vérifie qu'il contient pas déjà la carte offerte ou la carte demandé
                else{
                    // Si la carte y est pas on l'ajoute
                    int indexEchange = echanges.indexOf(echangeAux);
                    if(resultatEchanges.getString("cardCodeDemande")!=null) {
                        Carte carteDemandeAux = new Carte(resultatEchanges.getString("cardCodeDemande"));
                        if (!echanges.get(indexEchange).getCartesDemandees().contains(carteDemandeAux)) {
                            while (carteDemandeAux==null)carteDemandeAux = recupererCarteInfo(resultatEchanges.getString("cardCodeAsked"));
                            if (carteDemandeAux!=null)echanges.get(indexEchange).getCartesDemandees().add(carteDemandeAux);
                        }
                    }

                    if(resultatEchanges.getString("cardCodeOffered")!=null) {
                        Carte carteProposeAux = new Carte(resultatEchanges.getString("cardCodeOffered"));
                        if (!echanges.get(indexEchange).getCartesDemandees().contains(carteProposeAux)) {
                            while (carteProposeAux==null)carteProposeAux = recupererCarteInfo(resultatEchanges.getString("cardCodeOffered"));
                            if (carteProposeAux!=null) echanges.get(indexEchange).getCartesDemandees().add(carteProposeAux);
                        }
                    }
                }
                echanges.add(echangeAux);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return echanges;
    }
}
