<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Workspace | Business Licence Renewal</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css"></head>
<body><main class="card"><p class="eyebrow"><c:out value="${roleLabel}"/> WORKSPACE</p>
<h1 id="welcome-name">Welcome, <c:out value="${sessionScope.currentUser.fullName}"/></h1>
<p id="user-role">Role: <c:out value="${sessionScope.currentUser.role}"/></p>
<p>Your account is signed in. Licence application features will be added in the next project stage.</p>
<form method="post" action="${pageContext.request.contextPath}/logout"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
<button id="logout-button" type="submit">Sign out</button></form></main></body></html>
