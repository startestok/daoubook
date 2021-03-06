<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
    <title><spring:message code="daou.book.example.title"/></title>
    <link rel="stylesheet" href="/resources/css/styles.css" type="text/css"/>
</head>
<body>
<jsp:include page="navigation.jsp"/>
<h1><spring:message code="user.edit.page.title"/></h1>
<div>
    <form:form action="/user/edit" commandName="user" method="POST">
        <form:hidden path="id"/>
        <div>
            <form:label path="firstName"><spring:message code="user.label.firstName"/>:</form:label>
            <form:input path="firstName" size="20"/>
            <form:errors path="firstName" cssClass="error" element="div"/>
        </div>
        <div>
            <form:label path="lastName"><spring:message code="user.label.lastName"/>:</form:label>
            <form:input path="lastName" size="20"/>
            <form:errors path="lastName" cssClass="error" element="div"/>
        </div>
        <div>
            <input type="submit" value="<spring:message code="user.edit.page.submit.label"/>"/>
        </div>
    </form:form>
</div>
</body>
</html>