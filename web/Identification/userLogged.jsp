<%@ page language="java"
         contentType="text/html; charset=windows-1256"
         pageEncoding="windows-1256"
         import="Entries.Utilisateur"
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
    <meta http-equiv="Content-Type"
          content="text/html; charset=windows-1256">
    <title>   User Logged Successfully   </title>
</head>

<body>


    <% Utilisateur currentUser = (Utilisateur) session.getAttribute("currentSessionUser");
        boolean userModified = (Boolean) session.getAttribute("userModified");
        if(currentUser!=null ) {
            if (userModified) {
                out.println(currentUser.getName()+ " your profile has been modified");
                session.setAttribute("userModified",false);
            }
        }%>


<h1>Welcome <%= currentUser.getSurname() %> how are u ? </h1>

<h2>QUIZZ</h2>
<p>Pour accèder au quizz appuyer <a href='SimpleServlet'> ici</a></p>

<h2>ZONE D'ECHANGES</h2>
<p>Pour accèder à la zone d'échanges appuyer </p>

<h2>GALLERIE DE CARTE</h2>
<p>Pour accèder à la zone d'échanges appuyer </p>

<h2>MAGASIN</h2>
<p>Pour accèder à la zone d'échanges appuyer </p>

<h2>PROFIL</h2>
    <p>Pour voir ton profile appuie <a href='../GestionUtilisateur'> ici</a> </p>
    <p>Pour modifier ton profile appuie <a href='../modifyProfile.jsp'> ici</a> </p>

<h2>ESPACE D'AMIS</h2>
<p>Pour accèder à la zone d'échanges appuyer </p>

</body>

</html>