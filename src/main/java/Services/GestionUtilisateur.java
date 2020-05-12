package Services;

import ConnectionClases.ConnectionManager;
import Entries.Utilisateur;
import OtherClasses.Reponses;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mysql.jdbc.Blob;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@MultipartConfig
public class GestionUtilisateur extends HttpServlet{

    static Connection currentCon = null;
    static ResultSet rs = null;
    static Utilisateur utilisateurCourant = null;
    Gson gson;

    public GestionUtilisateur(){ super(); }

    public void init(){
        this.gson = new GsonBuilder().serializeNulls().create();
        ServletContext cont = getServletContext();
        if (cont.getAttribute("utilisateurCourant")!= null){
            utilisateurCourant = new Utilisateur();
            utilisateurCourant =(Utilisateur) cont.getAttribute("utilisateurCourant");
        }
    }


    /**
     * Cette methode retourne les informations de l'utilisateur courant afin de lui afficher son profil.
     * @param request
     * @param response
     * @throws IOException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String state = request.getParameter("state");
        response.setContentType("application/json");
        if(state.equals("profil")) {
            if (utilisateurCourant != null) response.getWriter().write(gson.toJson(utilisateurCourant));
            else response.setStatus(404);
        }
        if(state.equals("piecesOr")){
            if (utilisateurCourant != null) response.getWriter().write(gson.toJson(utilisateurCourant.getPiecesOr()));
            else response.setStatus(404);
        }
    }

    /**
     * Cette methode va mettre à jour la base de donnée avec la modification du profil
     * @param utilisateurModifie
     */
    public static boolean majProfile(Utilisateur utilisateurModifie) {
        boolean res = false;
        Statement stmt;

        String searchQuery =
                "update  QuizzApp.User set userName = '" + utilisateurModifie.getName() + "', surname= '" +
                        utilisateurModifie.getSurname() + "', birthDate ='" + utilisateurModifie.getBirthDate() +
                        "', mail = '" + utilisateurModifie.getMail() + "', profile_image" +
                        "= '" + "' where login =" + utilisateurModifie.getLogin();

        try {
            currentCon = ConnectionManager.getConnection();
            stmt = currentCon.createStatement();
            stmt.executeUpdate(searchQuery);
            res=true;

        } catch (Exception ex) {
            System.out.println("Log In failed: An Exception has occurred! " + ex);
        }
        return res;
    }


    /**
     * Cette methode mettera à jour le profil de l'utilisateur
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException{
        ServletContext cont = getServletContext();
        String nameUser = request.getParameter("nameUser");
        String surname = request.getParameter("surname");
        String birthdate = request.getParameter("birthdate");
        String email = request.getParameter("email");


        if(nameUser!=null)
            if(!nameUser.equals("")) utilisateurCourant.setName(nameUser);
        if( surname!=null){
            if(!surname.equals("")) utilisateurCourant.setSurname(surname);
        }
        if( birthdate!=null) {
            if(!birthdate.equals("")) utilisateurCourant.setBirthDate(birthdate);
        }
        if( email!=null ){
            if(!email.equals(""))utilisateurCourant.setMail(email);
        }

        cont.setAttribute("currentSessionUser",utilisateurCourant);

        // la misa à jour a bien été effectué
        if(majProfile(utilisateurCourant)){
            response.getWriter().write(gson.toJson(new Reponses(true)));
            cont.setAttribute("utilisateurCourant", utilisateurCourant);
            System.out.println("Modification compte réussite");
        }else {
            response.getWriter().write(gson.toJson(new Reponses(false)));
            System.out.println("Erreur lors de la modification du compte");
        }

    }


}