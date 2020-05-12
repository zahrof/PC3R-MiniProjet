<%@ page import="Entries.Utilisateur" %>
<%@ page import="jdk.nashorn.internal.ir.debug.JSONWriter" %><%--
  Created by IntelliJ IDEA.
  User: Marek
  Date: 13/04/2017
  Time: 02:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Pokemon Quizz</title>
  </head>
  <body>


  <h1>Register</h1>
  <form action="ServletIdentification" method="get">
      <table>
          <tr>
              <td>adresse mail:</td>
              <td><input type="email" name="mail" /></td>
          <tr>
              <td>Password:</td>
              <td><input type="password" name="pw" /></td>
          <tr>
              <td></td>
              <td><input type="submit" value="login" /></td>
          </tr>
      </table>
  </form>


  <h2>Not already a member? </h2>
  <form action="ServletIdentification" method="post">
      <table>
          <tr>
              <td>Name:</td>
              <td><input type="text" name="name-register" /></td>
          <tr>
              <td>Surname:</td>
              <td><input type="text" name="surname-register" /></td>
          <tr>
              <td>Birthday:</td>
              <td><input type="date" name="birthDay-register" /></td>
          <tr>
              <td>Mail:</td>
              <td><input type="email" name="mail-register" /></td>
          <tr>
              <td>Password:</td>
              <td><input type="password" name="password-register" /></td>
          <tr>
              <td></td>
              <td><input type="submit" value="register" /></td>
          </tr>
      </table>
  </form>

  </body>
</html>
