<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="title" value="Alarm Action"/>
<t:inventory-page title="${title}">
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/action.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>        
    <jsp:body>
        <div class="banner-breadbox">
            <ul>
                <li>
                    <span>Actions</span>
                </li>
                <li>
                    <h2 id="page-header-title"><c:out value="${action.name}"/></h2>
                </li>
            </ul>
        </div>
        <section>
            <div class="dialog-content">
                <div class="dialog-links">
                    <a class="dialog-only-link" href="${pageContext.request.contextPath}/inventory/actions/detail?actionId=${action.actionId}">Link</a>
                    <c:set var="editable" value="${pageContext.request.isUserInRole('jaws-admin')}"/>
                    <c:if test="${editable}">
                        <c:url var="url" value="/inventory/actions">
                            <c:param name="actionName" value="${action.name}"/>
                        </c:url>
                        <span class="dialog-only-link">|</span> <a href="${url}">Modify</a>
                    </c:if>
                </div>
                <dl>
                    <dt>Taxonomy:</dt>
                    <dd>
                        <c:out value="${action.component.team.name}"/> &gt;
                        <c:out value="${action.component.name}"/>
                    </dd>
                    <dt>Corrective Action:</dt>
                    <dd>
                        <div class="markdown-widget">
                            <div class="markdown-text"><c:out value="${action.correctiveAction}"/></div>
                            <div class="markdown-html"></div>
                        </div>
                    </dd>
                    <dt>Rationale:</dt>
                    <dd>
                        <div class="markdown-widget">
                            <div class="markdown-text"><c:out value="${action.rationale}"/></div>
                            <div class="markdown-html"></div>
                        </div>
                    </dd>
                    <dt>Filterable:</dt>
                    <dd><c:out value="${action.filterable ? 'Yes' : 'No'}"/></dd>
                    <dt>Latchable:</dt>
                    <dd><c:out value="${action.latchable ? 'Yes' : 'No'}"/></dd>
                    <dt>On Delay (seconds):</dt>
                    <dd><c:out value="${action.onDelaySeconds eq null ? 'None' : action.onDelaySeconds}"/></dd>
                    <dt>Off Delay (seconds):</dt>
                    <dd><c:out value="${action.offDelaySeconds eq null ? 'None' : action.offDelaySeconds}"/></dd>
                </dl>
            </div>
        </section>
    </jsp:body>         
</t:inventory-page>