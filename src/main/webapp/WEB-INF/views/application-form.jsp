<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Business Licence Renewal</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/workflow.css"></head><body>
<header><a class="brand" href="${pageContext.request.contextPath}/dashboard">Business Licence Renewal</a><form method="post" action="${pageContext.request.contextPath}/logout"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"><button id="logout-button" class="secondary" type="submit">Sign out</button></form></header>
<main class="narrow"><a href="${pageContext.request.contextPath}/owner/dashboard">← Your applications</a><section class="panel"><h1><c:choose><c:when test="${editing}">Edit submitted application</c:when><c:otherwise>New renewal application</c:otherwise></c:choose></h1>
<p>Enter your existing licence and business details. All fields are required.</p>
<c:if test="${not empty error}"><p id="form-error" class="error" role="alert"><c:out value="${error}"/></p></c:if>
<c:set var="formPath" value="/owner/renew"/><c:if test="${editing}"><c:set var="formPath" value="/owner/records/edit"/></c:if>
<form id="application-form" method="post" action="${pageContext.request.contextPath}${formPath}"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"><c:if test="${editing}"><input type="hidden" name="id" value="<c:out value='${param.id}'/>"></c:if>
<div class="form-grid">
<label for="businessName">Business name<input id="businessName" name="businessName" type="text" maxlength="150"  value="<c:out value='${form.businessName}'/>" required></label>
<label for="registrationNumber">Registration number<input id="registrationNumber" name="registrationNumber" type="text" maxlength="40" placeholder="REG-2024-8841" pattern="[A-Za-z0-9][A-Za-z0-9-]{2,39}" value="<c:out value='${form.registrationNumber}'/>" required></label>
<label for="tradeCategory">Trade category<input id="tradeCategory" name="tradeCategory" type="text" maxlength="80" placeholder="General Retail" value="<c:out value='${form.tradeCategory}'/>" required></label>
<label for="annualTurnover">Annual turnover (INR)<input id="annualTurnover" name="annualTurnover" type="number" min="0" max="9999999999999.99" step="0.01" value="<c:out value='${form.annualTurnover}'/>" required></label>
<label for="expiryDate">Current licence expiry date<input id="expiryDate" name="expiryDate" type="date"  value="<c:out value='${form.expiryDate}'/>" required></label>
<label for="contactEmail">Contact email<input id="contactEmail" name="contactEmail" type="email" maxlength="120"  value="<c:out value='${form.contactEmail}'/>" required></label>
<label for="contactPhone">Contact phone<input id="contactPhone" name="contactPhone" type="tel" maxlength="20" placeholder="9876543210" value="<c:out value='${form.contactPhone}'/>" required></label>
</div><button id="submit-application" type="submit"><c:choose><c:when test="${editing}">Save changes</c:when><c:otherwise>Submit renewal application</c:otherwise></c:choose></button></form></section></main></body></html>
