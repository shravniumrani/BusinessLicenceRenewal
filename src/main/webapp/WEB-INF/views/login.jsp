<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Sign in | Business Licence Renewal</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css"></head>
<body><main class="card"><p class="eyebrow">BUSINESS LICENCE RENEWAL</p><h1>Welcome back</h1><p>Sign in to access your workspace.</p>
<c:if test="${not empty error}"><p id="login-error" class="error" role="alert"><c:out value="${error}"/></p></c:if>
<form method="post" action="${pageContext.request.contextPath}/login">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
<label for="username">Username</label><input id="username" name="username" autocomplete="username" maxlength="50" required>
<label for="password">Password</label><input type="password" id="password" name="password" autocomplete="current-password" maxlength="128" required>
<button id="login-button" type="submit">Sign in</button></form><a class="back" href="${pageContext.request.contextPath}/">Back to home</a>
</main></body></html>
