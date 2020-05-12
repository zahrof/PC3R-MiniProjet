<%@ page import="Entries.Utilisateur" %><%--
  Created by IntelliJ IDEA.
  User: zahrof
  Date: 07/05/2020
  Time: 18:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Your profile </title>
</head>
<body>

<% Utilisateur currentUser = (Utilisateur) session.getAttribute("currentSessionUser"); %>

<h1> Le profile de <%= currentUser.getName() %></h1>

<table>
    <tr>
        <td>Prénom:</td>
        <td> <%= currentUser.getName() %></td>
    </tr>
    <tr>
        <td>Nom:</td>
        <td>  <%= currentUser.getSurname() %></td>
    </tr>
    <tr>
        <td>Date de naissance:</td>
        <td>  <%= currentUser.getBirthDate() %></td>
    </tr>
    <tr>
        <td>Adresse mail:</td>
        <td>   <%= currentUser.getMail() %></td>
    </tr>
</table>

</body>
</html>
