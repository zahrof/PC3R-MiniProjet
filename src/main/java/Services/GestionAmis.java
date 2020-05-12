package Services;


import ConnectionClases.ConnectionManager;
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
import java.util.HashSet;
import java.util.Set;


//peut etre renommer à amitiés
public class GestionAmis extends HttpServlet {
    static Utilisateur utilisateurCourant = null;
    static Set<Invitation> invitations;
    Gson gson;


    public GestionAmis(){super();}


    public void init(){
        this.gson = new GsonBuilder().serializeNulls().create();
        ServletContext cont = getServletContext();

        if (cont.getAttribute("utilisateurCourant")!= null){
            utilisateurCourant = new Utilisateur();
            utilisateurCourant =(Utilisateur) cont.getAttribute("utilisateurCourant");
            utilisateurCourant.setAmis(recupererAmisUtilisateur(utilisateurCourant));
        }

        invitations = recupererInvitations(utilisateurCourant.getLogin());
    }

    /**
     * Cette methode permet de recuperer tous les amis d'un utilisateur ou de rechercher un utilisateur
     * @param request
     * @param response
     * @throws IOException
     */
    /*public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String state = request.getParameter("state");
        response.setContentType("application/json");

        if(state.equals("amisUtilisateurCourant")) {
            response.getWriter().write(gson.toJson(amis));
        }
        if(state.equals("rechercherUtilisateur")) {
            String moyenRecherche = request.getParameter("moyenRecherche");
            String finRequete="";
            switch(moyenRecherche){
                case "login":
                    finRequete += "login="+ request.getParameter("loginAmi");
                    break;
                case "nom":
                    finRequete += "surname='"+request.getParameter("nomAmi")+"'";
                    break;
                case "prenom":
                    finRequete += "userName='"+request.getParameter("prenomAmi")+"'";
                    break;
                case "nomEtPrenom" :
                    finRequete += "userName='"+request.getParameter("prenomAmi")+"' and surname='"+
                            request.getParameter("nomAmi")+"'";
                    break;
                case "mail" :
                    finRequete += "mail='"+request.getParameter("mail")+"'";
                    break;
            }
            Set<Utilisateur>  amiRecherche = rechercheAmi(finRequete);
            if(amiRecherche.isEmpty())response.getWriter().write(gson.toJson(null));
            else response.getWriter().write(gson.toJson(amiRecherche));
        }
        if(state.equals("recupererInvitationsUtilisateur")){

            if(invitations.isEmpty())response.getWriter().write(gson.toJson(null));
            else response.getWriter().write(gson.toJson(invitations));
        }

    }*/


    /**
     * Dans cette methode on ejoute des amis et des invitations
     * @param request
     * @param response
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String state = request.getParameter("state");
        response.setContentType("application/json");
        int idAmi = Integer.parseInt(request.getParameter("idAmi")); 

        if(state.equals("creationInvitation")){
            if(contientAmi(idAmi,utilisateurCourant.getAmis())){
                response.getWriter().write(gson.toJson(new Reponses(false, "Désolée vous êtes " +
                        "déjà ami avec cette personne!")));
                System.out.println("Utilisateur est déjà ami avec la personne");
            }else {
                if (contientInvitation(idAmi,invitations)){
                    response.getWriter().write(gson.toJson(new Reponses(false, "Désolée une " +
                            "invitation est déjà en cours avec cette personne!")));
                    System.out.println("Invitation déjà crée ami avec la personne");
                }else{
                    if(creationInvitation(utilisateurCourant.getLogin(), idAmi)){
                        response.getWriter().write(gson.toJson(new Reponses(true, "Désolee  " +
                                "il y a eu une erreur lors de la creation de l'inviation!")));
                    }else {
                        response.getWriter().write(gson.toJson(new Reponses(false, "Désolee  " +
                                "il y a eu une erreur lors de la creation de l'inviation!")));
                        System.out.println("Erreur création invitation");
                    }
                }
            }
            if(invitations.isEmpty())response.getWriter().write(gson.toJson(null));
            else response.getWriter().write(gson.toJson(new Reponses(true,
                    "Bravo, la création de l'invitation a bien été effectué! ")));
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String state = request.getParameter("state");
        response.setContentType("application/json");
        if(state.equals("repondreInvitation")){
            int idEmeteur = Integer.parseInt(request.getParameter("idEmeteur"));
            if(contientInvitationEtEstRecepteur(invitations,idEmeteur)){
                boolean accepte = Boolean.parseBoolean(request.getParameter("accepte"));
                if(modifierInvitation(idEmeteur, utilisateurCourant.getLogin(),accepte)){
                    response.getWriter().write(gson.toJson(new Reponses(true)));
                    System.out.println("Modification invitation réussi");
                }else{
                    response.getWriter().write(gson.toJson(new Reponses(false,
                            "il y a eu une erreur lors de la creation de l'inviation!")));
                    System.out.println("Modification invitation échoué");

                }
            }else response.getWriter().write(gson.toJson(new Reponses(false,
                    "Désolé, vous etes pas le recepteur de cette invitation ")));
        }
        if(state.equals("amisUtilisateurCourant")) {
            response.getWriter().write(gson.toJson(utilisateurCourant.getAmis()));
        }

        if(state.equals("recupererInvitationsUtilisateur")){

            if(invitations.isEmpty())response.getWriter().write(gson.toJson(null));
            else response.getWriter().write(gson.toJson(invitations));
        }
    }

    private boolean modifierInvitation(int idEmeteur, int recepteur, boolean accepte) {
        Set<Invitation> temp = new HashSet<>();
        Statement stmt;
        String query = "update Invitation set status=";
        Connection currentCon = null;
        java.util.Date dt = new java.util.Date();

        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String currentTime = sdf.format(dt);
        for(Invitation i : invitations){
            if(i.getEmetteur().getLogin()==idEmeteur && i.getRecepteur().getLogin()==utilisateurCourant.getLogin()){

                if(accepte){
                temp.add(new Invitation(recupererInfoAmi(idEmeteur),
                        recupererInfoAmi(recepteur), "accepte",
                        currentTime));
                ajoutAmi(idEmeteur); 
                query += "'accepte'";
                }else{
                    temp.add(new Invitation(recupererInfoAmi(idEmeteur),
                            recupererInfoAmi(recepteur), "refuse",
                            currentTime));
                    query += "'refuse'";
                }
            }else temp.add(i);
        }
        invitations=temp;

        query += ", date='"+currentTime+"' where receptor="+recepteur+ " and sender="+idEmeteur;
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

    private void ajoutAmi(int idEmeteur) {
        utilisateurCourant.getAmis().add(recupererInfoAmi(idEmeteur));
        ServletContext cont = getServletContext();
        cont.setAttribute("utilisateurCourant",utilisateurCourant);
        Statement stmt;
        Connection currentCon  = ConnectionManager.getConnection();
        String searchQuery =
                "insert into Friends (idFriend1,idFriend2) values ('"+idEmeteur+"','"+utilisateurCourant.getLogin()+"')";
        try {
            currentCon = ConnectionManager.getConnection();
            stmt = currentCon.createStatement();
            stmt.executeUpdate(searchQuery);


        } catch (Exception ex) {
            System.out.println("Log In failed: An Exception has occurred! " + ex);
        }
    }

    private boolean contientInvitationEtEstRecepteur(Set<Invitation> invitations, int idEmeteur) {
        boolean contient = false;
        for(Invitation i : invitations){
            if((i.getEmetteur().getLogin()==idEmeteur && i.getRecepteur().getLogin()==utilisateurCourant.getLogin()))
                contient = true;
        }

        return contient;

    }

    private boolean creationInvitation(int login, int idAmi) {
        java.util.Date dt = new java.util.Date();

        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String currentTime = sdf.format(dt);
        Invitation nouvelInvitation = new Invitation (recupererInfoAmi(utilisateurCourant.getLogin()),
                recupererInfoAmi(idAmi), "en attente",currentTime);
        Statement stmt;
        String query = "insert  into Invitation (status, date, receptor,sender) " +
                "select * from(select 'en attente','"+nouvelInvitation.getDateCreation()+"',"+nouvelInvitation.getRecepteur().getLogin()+
        ","+login +") as tmp where not exists (select invitationId from Invitation where receptor="+nouvelInvitation.getRecepteur().getLogin()
                +" and sender="+nouvelInvitation.getEmetteur().getLogin()+" limit 1 );";
        Connection currentCon = null;

        try {
            currentCon = ConnectionManager.getConnection();
            stmt=currentCon.createStatement();
            stmt.executeUpdate(query);
                invitations.add(nouvelInvitation);
            stmt.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
       return true;

    }

    private boolean contientInvitation(int idAmi, Set<Invitation> invitations) {
        boolean contient = false; 
        for(Invitation i : invitations){
            if((i.getEmetteur().getLogin()==idAmi && i.getRecepteur().getLogin()==utilisateurCourant.getLogin()) 
            || i.getEmetteur().getLogin()==utilisateurCourant.getLogin() && i.getRecepteur().getLogin()==idAmi)
                contient = true;
        }
        
        return contient;
    }

    private boolean contientAmi(int idAmi, Set<Utilisateur> amis) {
        boolean contient = false;
        for (Utilisateur c : amis) {
            if (c.getLogin()==idAmi) contient = true;
        }
        return contient;
    }

    /**
     * Dans cette methode on supprime des amis et invitations
     * @param request
     * @param response
     */
    public void doDelete(HttpServletRequest request, HttpServletResponse response){

    }

    private Set<Invitation> recupererInvitations(int login) {
        Set<Invitation> invitations = new HashSet<>();
        ResultSet resultatsInvitations;
        Statement stmt;
        String query = "select * from Invitation where receptor="+login+" or sender="+login;
        Connection currentCon = null;
        currentCon = ConnectionManager.getConnection();
        try {
            stmt=currentCon.createStatement();
            resultatsInvitations = stmt.executeQuery(query);
            boolean more = resultatsInvitations.next();
            if(more) {
                invitations.add(new Invitation(recupererInfoAmi( resultatsInvitations.getInt("sender")),
                        recupererInfoAmi(resultatsInvitations.getInt("receptor")), resultatsInvitations.getString("status"),
                        resultatsInvitations.getString("date")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return invitations;

    }


    private Set<Utilisateur> rechercheAmi(String finRequete) {
        Set<Utilisateur> amis = new HashSet<>();
        ResultSet resultatsRecherche=null;
        Statement stmt;
        String query = "select * from User where "+finRequete;

        try{
            Connection currentCon = null;
            currentCon = ConnectionManager.getConnection();
            stmt=currentCon.createStatement();
            resultatsRecherche = stmt.executeQuery(query);
            boolean more = resultatsRecherche.next();
            if(more){
                amis.add(new Utilisateur(resultatsRecherche.getInt("login"),resultatsRecherche.getString("birthDate"),
                        resultatsRecherche.getString("userName"), resultatsRecherche.getString("surname"),
                        resultatsRecherche.getString("mail")));
            }

        } catch (SQLException throwables) {
            //renvoyer une erreur?
            throwables.printStackTrace();
        }
        return amis;

    }



    public static Utilisateur recupererInfoAmi(int idAmi) {
        String query = "select * from User where login="+idAmi;
        ResultSet resultatInfoAmi = null;
        Utilisateur ami = null;
        Statement stmt;


        try{
            Connection currentCon = null;
            currentCon = ConnectionManager.getConnection();
            stmt=currentCon.createStatement();
            resultatInfoAmi= stmt.executeQuery(query);
            boolean more = resultatInfoAmi.next();
        if(more){
            ami = new Utilisateur(idAmi,resultatInfoAmi.getString("birthDate"),
                    resultatInfoAmi.getString("userName"), resultatInfoAmi.getString("surname"),
                    resultatInfoAmi.getString("mail"));
        }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return  ami;
    }

    private Set<Utilisateur> recupererAmisUtilisateur(Utilisateur utilisateur) {
        Set<Utilisateur> amis = new HashSet<>();
        String query = " select * from Friends where idFriend1="+utilisateur.getLogin()+" or idFriend2="+
                utilisateur.getLogin();
        ResultSet resultatAmis = null;
        Statement stmt;
        try{
            Connection currentCon = null;
            currentCon = ConnectionManager.getConnection();
            stmt=currentCon.createStatement();
            resultatAmis = stmt.executeQuery(query);
            boolean more = resultatAmis.next();
            if(more){
                int idAmi;
                if(resultatAmis.getInt("idFriend1")!=utilisateur.getLogin())
                    idAmi = resultatAmis.getInt("idFriend1");
                else idAmi = resultatAmis.getInt("idFriend2");
                amis.add(recupererInfoAmi(idAmi));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return amis;
    }
}
