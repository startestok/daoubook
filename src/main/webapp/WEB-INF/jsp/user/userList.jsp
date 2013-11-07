<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<h1><spring:message code="user.list.page.title"/></h1>
<c:if test="${not empty users}">
    <table>
        <thead>
        <tr>
            <td><spring:message code="user.label.lastName"/></td>
            <td><spring:message code="user.label.firstName"/></td>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${users}" var="user">
            <tr>
                <td><c:out value="${user.lastName}"/></td>
                <td><c:out value="${user.firstName}"/></td>
                <td><a href="/user/edit/<c:out value="${user.id}"/>"><spring:message code="user.edit.link.label"/></a></td>
                <td><a href="/user/delete/<c:out value="${user.id}"/>"><spring:message code="user.delete.link.label"/></a></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</c:if>
<c:if test="${empty users}">
    <p>
        <spring:message code="user.list.page.label.no.users.found"/>
    </p>
</c:if>