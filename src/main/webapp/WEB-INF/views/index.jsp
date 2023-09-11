<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:url var="domainRelativeReturnUrl" scope="request" context="/" value="${requestScope['javax.servlet.forward.request_uri']}${requestScope['javax.servlet.forward.query_string'] ne null ? '?'.concat(requestScope['javax.servlet.forward.query_string']) : ''}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" data-context-path="${pageContext.request.contextPath}" data-app-version="${initParam['releaseNumber']}"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="JLab Alarm Warning System Admin GUI">
    <title>JAWS</title>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico"/>
    <link rel="manifest" href="${pageContext.request.contextPath}/manifest.json">
    <c:url value="/resources/css/site.css" var="siteCssUrl">
        <c:param name="v" value="${initParam['releaseNumber']}"/>
    </c:url>
    <link rel="stylesheet" href="${siteCssUrl}"/>
</head>
<body>
<header>
    <img width="128" height="128" alt="Logo" src="${pageContext.request.contextPath}/resources/img/logo-128.png"/>
    <h1>JAWS</h1>
    <div id="auth">
        <c:choose>
            <c:when test="${pageContext.request.userPrincipal ne null}">
                <div id="username-container">
                    <c:out value="${pageContext.request.userPrincipal.name}"/>
                </div>
                <form id="logout-form" action="${pageContext.request.contextPath}/logout" method="post">
                    <button type="submit" value="Logout">Logout</button>
                    <input type="hidden" name="returnUrl" value="${fn:escapeXml(domainRelativeReturnUrl)}"/>
                </form>
            </c:when>
            <c:otherwise>
                <c:set var="absHostUrl" value="${env['FRONTEND_SERVER_URL']}"/>
                <c:url value="/sso" var="loginUrl">
                    <c:param name="returnUrl" value="${absHostUrl.concat(domainRelativeReturnUrl)}"/>
                </c:url>
                <a id="login-link" href="${loginUrl}">Login</a>
            </c:otherwise>
        </c:choose>
    </div>
</header>
<section>
    <table id="alarm-table">
        <thead>
            <tr>
                <th>name</th>
                <th>priority</th>
                <th>state</th>
                <th>type</th>
                <th>error</th>
                <th>stat</th>
                <th>sevr</th>
                <th>epicspv</th>
                <th>location</th>
            </tr>
        </thead>
        <tbody>
        </tbody>
    </table>
    <a href="debug">Debug</a>
</section>
<c:url value="/resources/js/notifications.js" var="notificationsJsUrl">
    <c:param name="v" value="${initParam['releaseNumber']}"/>
</c:url>
<script src="${notificationsJsUrl}"></script>
</body>
</html>