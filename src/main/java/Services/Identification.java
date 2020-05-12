package Services;

import ConnectionClases.ConnectionManager;
import Entries.Utilisateur;
import OtherClasses.AuthReponse;
import OtherClasses.Reponses;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.Optional;


public class Identification extends HttpServlet {

    static Connection currentCon = null;
    static ResultSet rs = null;
    Gson gson;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Identification() {
        super();
    }

    public void init() {
        this.gson = new GsonBuilder().serializeNulls().create();
    }
 // à changer par doPost plus tard
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletContext cont = getServletContext();
        String login = request.getParameter("login");
        String mdp = request.getParameter("mdp");
        String state = request.getParameter("state");


        // l'utilisateur veut s'authentifier
        if(state.equals("authentification")) {
            if (login != null && mdp != null) {
                Optional<Utilisateur> utilisateur = identifierUtilisateur(login);
                response.setContentType("application/json");
                System.out.println("Authentification par mail: " + login + "mdp "+mdp);
                if (utilisateur.isPresent()) {
                    if(mdp.equals(utilisateur.get().getPassword())){
                        response.getWriter().write(gson.toJson(new AuthReponse("ok",login)));
                        System.out.println("R?ussie.");
                        cont.setAttribute("utilisateurCourant", utilisateur.get());

                    } else {
                        response.getWriter().write(gson.toJson(new AuthReponse("mdp", login)));
                        System.out.println("Mauvais Mdp.");}
                } else {
                    response.getWriter().write(gson.toJson(new AuthReponse("login", login)));
                    System.out.println("Mauvais login");
                }
            } else {
                System.out.println("Pas de login ou de mdp!");
            }
        }

        //l'utilisateur veut créer un compte
        if(state.equals("creationUtilisateur")) {
            Utilisateur nouvelUtilisateur = new Utilisateur(
                    request.getParameter("birthDate-register"),
                    request.getParameter("name-register"),
                    request.getParameter("surname-register"),
                    request.getParameter("password-register"),
                    request.getParameter("mail-register"));
            nouvelUtilisateur.setLogin(prochainNumeroLogin());
            if(creationUtilisateur(nouvelUtilisateur)){
                response.getWriter().write(gson.toJson(new Reponses(true)));
                cont.setAttribute("utilisateurCourant", nouvelUtilisateur);
                System.out.println("Création compte réussite");
            }else {
                response.getWriter().write(gson.toJson(new Reponses(false)));
                System.out.println("Erreur lors de la création du compte");
            }
        }
    }

    /**
     * Cette methode récupère la connection à la base de donnée et demande s'il existe un utilisateur
     * avec comme login le login passé en parametre. Si oui elle renvoie un objet representant
     * l'utilisateur de la base de donnée. Sinon elle renvoie l'Optional à null.
     * @param login
     * @return
     */
    private Optional<Utilisateur> identifierUtilisateur(String login) {
        Utilisateur res = null;
        Statement stmt;
        String query = "select * from User where mail='"+login+"'";

        try {
            //connecto to DB
            currentCon = ConnectionManager.getConnection();
            stmt=currentCon.createStatement();
            rs = stmt.executeQuery(query);
            boolean more = rs.next();

            if (more)
            {
                res = new Utilisateur(rs.getString("birthDate"), rs.getString("userName"),
                        rs.getString("surname"), rs.getString("password"), login,
                        Integer.parseInt(rs.getString("goldCoins")));
                res.setLogin(Integer.parseInt(rs.getString("login")));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Optional.ofNullable(res);
    }

    /**
     * Cette methode fait un appel à la base de donnée pour récuperer l'identifiant du dernier
     * utilisateur inscrit et renvoie un nouvel identifiant réprésentant l'identifiant du
     * dernier utilisateur plus 1.
     * @return Un numéro d'utilisateur unique.
     */
    private int prochainNumeroLogin() {
        Statement stmt;

        String searchQuery =
                "select login from User order by login DESC LIMIT 1";
        try
        {
            //connect to DB
            currentCon = ConnectionManager.getConnection();
            stmt=currentCon.createStatement();
            rs = stmt.executeQuery(searchQuery);
            boolean more = rs.next();

            // if there wasn't any user,we set the newLogin to 1
            if (!more) return 1;

            //if user exists set the isValid variable to true
            else if (more)
            {
                return rs.getInt("login")+1;
            }
        }

        catch (Exception ex)
        {
            System.out.println("Log In failed: An Exception has occurred! " + ex);
        }

        return 1;

    }

    private boolean creationUtilisateur(Utilisateur nouvelUtilisateur) {
        Statement stmt;

        String searchQuery =
                "insert into QuizzApp.User (login,userName, surname, birthDate, mail, password) values ('"+
                       nouvelUtilisateur.getLogin() + "','"+ nouvelUtilisateur.getName()+
                        "','"+ nouvelUtilisateur.getSurname()+"','"+nouvelUtilisateur.getBirthDate()+
                        "','"+nouvelUtilisateur.getMail()+"','"+ nouvelUtilisateur.getPassword()+"')";
        try
        {
            currentCon = ConnectionManager.getConnection();
            stmt=currentCon.createStatement();
            stmt.executeUpdate(searchQuery);
            return true;
        }

        catch (Exception ex)
        {
            System.out.println("Log In failed: An Exception has occurred! " + ex);
            return false;
        }

    }


}