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
    <title>   Mon profile   </title>
</head>

<body>


    <% Utilisateur currentUser = (Utilisateur) session.getAttribute("currentSessionUser"); %>

    <h1>Update your profile</h1>
    <form action="GestionUtilisateur" method="post" enctype='multipart/form-data'>
        <table>
            <tr>
                <td>Prénom:</td>
                <td> <input type="text" name="nameUser" placeholder=<%= currentUser.getName() %>></td>
            <tr>
                <td>Nom:</td>
                <td>  <input type="text" name="surname" placeholder=<%= currentUser.getSurname() %>></td>
            <tr>
            <tr>
                <td>Date de naissance:</td>
                <td>   <input type="date" name="birthdate" placeholder=<%= currentUser.getBirthDate() %>></td>
            <tr>
                <td>Photo de profil:</td>
                <td>   <input type="file" name="profilPhoto"></td>
            <tr>
                <td>Adresse mail:</td>
                <td>   <input type="email" name="email"  placeholder=<%= currentUser.getMail() %>></td>
            <tr>
             </tr>
        <tr>
                <td></td>
                <td><input type="submit" value="Update" /></td>
            </tr>
        </table>
    </form>

</body>

</html>