<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="jaws" uri="http://jlab.org/jaws/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Sync Rule"/>
<t:setup-page title="${title}">
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css"
              href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/sync.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>
    <jsp:body>
        <div class="banner-breadbox">
            <ul>
                <li>
                    <span>Sync Rules</span>
                </li>
                <li>
                    <h2 id="page-header-title"><c:out value="${param.syncRuleId}"/></h2>
                </li>
            </ul>
        </div>
        <section>
            <div class="dialog-content">
                <div class="dialog-links">
                    <c:url value="/setup/syncs/${jaws:urlEncodePath(param.syncRuleId)}" var="url">
                    </c:url>
                    <a class="dialog-only-link"
                       href="${url}">Link</a>
                    <c:set var="editable" value="${pageContext.request.isUserInRole('jaws-admin')}"/>
                    <c:if test="${editable}">
                        <c:url var="url" value="/setup/syncs">
                            <c:param name="syncRuleId" value="${param.syncRuleId}"/>
                        </c:url>
                        <span class="dialog-only-link">|</span> <a href="${url}">Modify</a>
                    </c:if>
                </div>
                <c:choose>
                    <c:when test="${rule ne null}">
                        <h3>Configuration</h3>
                        <dl>
                            <dt>Action</dt>
                            <dd><c:out value="${rule.action.name}"/></dd>
                            <dt>Deployment</dt>
                            <dd><c:out value="${rule.deployment}"/></dd>
                            <dt>Query</dt>
                            <dd><c:out value="${rule.query}"/></dd>
                            <dt>Screen Command</dt>
                            <dd><c:out value="${rule.screenCommand}"/></dd>
                            <dt>PV</dt>
                            <dd><c:out value="${rule.pv}"/></dd>
                        </dl>
                        <h3>Results</h3>
                        <button type="button" disabled="disabled">Merge</button>
                    </c:when>
                    <c:otherwise>
                        <div>Sync Rule with ID <c:out value="${param.syncRuleId}"/> not found!
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </section>
    </jsp:body>
</t:setup-page>