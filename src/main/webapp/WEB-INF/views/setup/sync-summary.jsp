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
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/sync-summary.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/sync-summary.js"></script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <h2 id="page-header-title"><c:out value="${title}"/></h2>
            <c:forEach items="${ruleSetList}" var="rs">
                <h3><c:out value="${rs.name}"/></h3>
                <c:choose>
                    <c:when test="${fn:length(rs.ruleList) > 0}">
                        <table>
                            <tbody>
                            <c:forEach items="${rs.ruleList}" var="rule">
                                <tr class="rule-row" data-id="${rule.syncRuleId}">
                                    <td><c:out value="${rule.syncRuleId} - ${rule.action.name} - ${rule.syncServer.name} - ${rule.description}"/></td>
                                    <td><div class="status">Pending</div></td>
                                    <td>
                                        <!-- Use onclick to avoid https://bugs.webkit.org/show_bug.cgi?id=30103 -->
                                        <c:url value="/setup/syncs/${jaws:urlEncodePath(rule.syncRuleId)}" var="url">
                                        </c:url>
                                        <form method="get"
                                              action="${pageContext.request.contextPath}/setup/syncs/${fn:escapeXml(rule.syncRuleId)}">
                                        <button class="single-char-button" type="button" onclick="window.location.href = '${url}';  return false;">&rarr;</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        None
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </section>
    </jsp:body>         
</t:setup-page>