<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" data-context-path="${pageContext.request.contextPath}" data-app-version="${initParam['releaseNumber']}"/>
    <title>JAWS Admin GUI</title>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico"/>
    <c:url value="/resources/css/site.css" var="siteCssUrl">
        <c:param name="v" value="${initParam['releaseNumber']}"/>
    </c:url>
    <link rel="stylesheet" href="${siteCssUrl}"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/libs/jquery-ui-1.12.1.smoothness/jquery-ui.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/toast-ui-3.1.3/toastui-editor.css"/>
</head>
<body>
<header>
    <img src="${pageContext.request.contextPath}/resources/img/logo128x128.png"/>
    <h1>JAWS Admin GUI</h1>
</header>
<div id="tabs">
    <ul>
        <li><a href="#registrations-panel">Registrations</a></li>
        <li><a href="#classes-panel">Classes</a></li>
        <li><a href="#instances-panel">Instances</a></li>
        <li><a href="#locations-panel">Locations</a></li>
        <li><a href="#categories-panel">Categories</a></li>
    </ul>
    <t:panel id="registrations" title="Registrations" fields="${registrationFields}" editable="false"/>
    <t:panel id="classes" title="Classes" fields="${classFields}"/>
    <t:panel id="instances" title="Instances" fields="${instanceFields}"/>
    <t:panel id="locations" title="Locations" fields="${locationFields}"/>
    <t:panel id="categories" title="Categories" fields="${categoryFields}"/>
</div>
<div id="version-div">v<c:out value="${initParam['releaseNumber']}"/></div>
<script src="${pageContext.request.contextPath}/resources/libs/jquery-ui-1.12.1.smoothness/external/jquery/jquery.js"></script>
<script src="${pageContext.request.contextPath}/resources/libs/jquery-ui-1.12.1.smoothness/jquery-ui.min.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/modules/toast-ui-3.1.3/toastui-all.min.mjs"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/modules/page-1.11.6/page.min.mjs"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/modules/dexie-3.2.1/dexie.min.mjs"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/modules/jaws-admin-gui-${initParam['releaseNumber']}/main.mjs"></script>
</body>
</html>