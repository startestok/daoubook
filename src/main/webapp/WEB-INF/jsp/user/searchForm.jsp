<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<form:form action="/user/search" commandName="searchCriteria" method="POST">
    <fieldset>
        <legend><spring:message code="user.search.form.title"/></legend>
        <div>
            <form:label path="searchTerm"><spring:message code="user.search.searchterm.label"/></form:label>
            <form:input path="searchTerm" type="text"/>
        </div>
        <div>
            <form:label path="searchType"><spring:message code="user.search.searchtype.label"/></form:label>
            <form:select path="searchType">
                <form:option value="METHOD_NAME"><spring:message code="SearchType.METHOD_NAME"/></form:option>
                <form:option value="NAMED_QUERY"><spring:message code="SearchType.NAMED_QUERY"/></form:option>
                <form:option value="QUERY_ANNOTATION"><spring:message code="SearchType.QUERY_ANNOTATION"/></form:option>
            </form:select>
        </div>
        <div>
            <input type="submit" value="<spring:message code="user.search.form.submit.label"/>"/>
        </div>
    </fieldset>
</form:form>