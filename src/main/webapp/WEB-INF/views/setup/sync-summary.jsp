<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="jaws" uri="http://jlab.org/jaws/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="title" value="Sync Summary"/>
<t:setup-page title="${title}">
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/sync-servers.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/sync-servers.js"></script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <h2 id="page-header-title"><c:out value="${title}"/></h2>
            <c:forEach items="${ruleSetList}" var="rs">
                <h3><c:out value="${rs.name}"/></h3>
                <c:choose>
                    <c:when test="${fn:length(rs.ruleList) > 0}">
                        <ul>
                            <c:forEach items="${rs.ruleList}" var="rule">
                                <li><c:out value="${rule.syncRuleId} - ${rule.syncServer.name} - ${rule.action.name} - ${rule.description}"/></li>
                            </c:forEach>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        None
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </section>
    </jsp:body>         
</t:setup-page>