package Services;

import ConnectionClases.ConnectionManager;
import Entries.Carte;
import Entries.Utilisateur;
import OtherClasses.Reponses;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.pokemontcg.PokemonTcg;
import io.pokemontcg.model.Card;
import io.pokemontcg.model.Cards;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GestionCartes extends HttpServlet {
    static Connection currentCon = null;
    static ResultSet rs = null;
    static Utilisateur utilisateurCourant = null;
    Gson gson;
    Statement stmt;

    public GestionCartes(){super();}

    public void init(){
        this.gson = new GsonBuilder().serializeNulls().create();
        ServletContext cont = getServletContext();
        if (cont.getAttribute("utilisateurCourant")!= null){
            utilisateurCourant = new Utilisateur();
            utilisateurCourant =(Utilisateur) cont.getAttribute("utilisateurCourant");
        }
    }

    /**
     * Cette methode renvoi un json contenant toutes les cartes que l'utilisateur ne possède ou qu'il ne possède pas,
     * où renvoie l'information concernant une seule carte.
     * @param request
     * @param response
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String state = request.getParameter("state");

        response.setContentType("application/json");

        if(state.equals("cartesUtilisateurPossedePas")) {
            // On recupere toutes les cartes de l'api
            Optional<Cards> cards = PokemonTcg.cards().all();

            Set<Card> cartesUtilisateurPossedePas = tri(cards, utilisateurCourant.getLogin());
            if (cartesUtilisateurPossedePas.isEmpty()) System.out.println("Ensemble vide");
            response.getWriter().write(gson.toJson(cartesUtilisateurPossedePas));
        }

        if(state.equals("cartesUtilisateurPossede")){
            // On recupere toutes les cartes de l'api
            Optional<Cards> cards = PokemonTcg.cards().all();
            Set<Card> cartesUtilisateurPossede = cartesUtilisateurPossede(cards, utilisateurCourant.getLogin());
            if (cartesUtilisateurPossede.isEmpty()) System.out.println("Ensemble vide");
            response.getWriter().write(gson.toJson(cartesUtilisateurPossede));
        }

        if(state.equals("informationCarte")){
            Card cardDemande = PokemonTcg.cards().find(request.getParameter("carte")).get().getCard();
            int prixCarte = Integer.parseInt(cardDemande.getNumber());
            Carte carteDemande = new Carte(cardDemande.getId(),2*prixCarte,prixCarte,cardDemande);

        response.getWriter().write(gson.toJson(carteDemande));
        }

    }

    /**
     * Cette methode ajoute à la base de données l'achat d'une nouvelle carte
     * @param request
     * @param response
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String state = request.getParameter("state");
        ServletContext cont = getServletContext();

        response.setContentType("application/json");

        if(state.equals("achatCarte")) {
            String codeCarte = request.getParameter("codeCarte");
            int pointsCarte = Integer.parseInt(PokemonTcg.cards().find(codeCarte).get().getCard().getNumber());
            int prixCarte = 2*pointsCarte;
            if(prixCarte>utilisateurCourant.getPiecesOr()){
                response.getWriter().write(gson.toJson(new Reponses(false, "Désolée vous n'avez pas " +
                        "assez d'argent!")));
                System.out.println("Utilisateur a pas assez d'argent pour cette carte");
            }else{
                if(achatCarte(codeCarte, pointsCarte, prixCarte, utilisateurCourant.getLogin())){
                    response.getWriter().write(gson.toJson(new Reponses(true)));
                    System.out.println("achat carte réussite");
                    utilisateurCourant.setPiecesOr(utilisateurCourant.getPiecesOr()-prixCarte);
                    cont.setAttribute("utilisateurCourant", utilisateurCourant);
                    utilisateurCourant.majPiecesOrBDD(utilisateurCourant.getPiecesOr());
                    //changer information dans bdd
                }
                else {
                    response.getWriter().write(gson.toJson(new Reponses(false)));
                    System.out.println("Erreur lors de l'achat du compte");
                }
            }

        }


    }



    private boolean achatCarte(String codeCarte, int pointsCarte, int prixCarte, int login) {

        String query1 = "insert ignore into Card (cost, points, cardCode) values ('"+prixCarte+"','"+pointsCarte+"','"+
        codeCarte+"');";
        String query2= "insert into CardOwners (login,cardCode)  values ('"+login+"','"+codeCarte+"');";
        //insert into CardOwners (login,cardCode)  values ('"+login+"','"+codeCarte+"');
        try
        {
            currentCon = ConnectionManager.getConnection();
            stmt=currentCon.createStatement();
            stmt.executeUpdate(query1);
            stmt.executeUpdate(query2);
            return true;
        }

        catch (Exception ex)
        {
            System.out.println("l'achat de la carte a échoué! " + ex);
            return false;
        }

    }

    private Set<Card> cartesUtilisateurPossede(Optional<Cards> cards, int login) {
        Set<Card> cartesUtilisateurPossede = new HashSet<>();
        String query = "select cardCode from QuizzApp.User INNER JOIN CardOwners using (login) where login='"+login+"'";
        try {
            //connecto to DB
            currentCon = ConnectionManager.getConnection();
            stmt=currentCon.createStatement();
            rs = stmt.executeQuery(query);

            while(rs.next()) {
                cartesUtilisateurPossede.add(PokemonTcg.cards().find(rs.getString("cardCode")).get().getCard());
            }
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
            // renvoyer un message d'erreur au client
        }
        return cartesUtilisateurPossede;
    }


    private Set<Card> tri(Optional<Cards> cartesApi, int loginUtilisateurCourant) {
        Set<Card> cartesUtilisateurPossedePas = new HashSet<>();
        Set<Card> cartesUtilisateurPossede = cartesUtilisateurPossede(cartesApi,loginUtilisateurCourant);
            boolean possede=false;
            // pour chaque carte dans l'api
            for (int i=0; i < cartesApi.get().getCards().size(); i++) {
                Carte aux = new Carte(cartesApi.get().getCards().get(i).getId());
                if(contientCarte(aux,cartesUtilisateurPossede) )possede = true;

                if(!possede) cartesUtilisateurPossedePas.add(cartesApi.get().getCards().get(i));

                possede=false;
            }


        return cartesUtilisateurPossedePas;
    }

    private boolean contientCarte(Carte aux, Set<Card> cartesUtilisateurPossede) {
        boolean contient = false;
        for (Card c : cartesUtilisateurPossede) {
            if (c.getId().equals(aux.getCodeCarte())) contient = true;
        }
        return contient;

    }

}