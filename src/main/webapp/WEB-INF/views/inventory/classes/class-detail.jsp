<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="title" value="Alarm Class"/>
<t:inventory-page title="${title}">
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/class.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>        
    <jsp:body>
        <div class="banner-breadbox">
            <ul>
                <li>
                    <span>Classes</span>
                </li>
                <li>
                    <h2 id="page-header-title"><c:out value="${entity.name}"/></h2>
                </li>
            </ul>
        </div>
        <section>
            <div class="dialog-content">
                <div class="dialog-links">
                    <a class="dialog-only-link" href="${pageContext.request.contextPath}/inventory/classes/detail?classId=${entity.classId}">Link</a>
                    <c:if test="${editable}">
                        <c:url var="url" value="/inventory/classes">
                            <c:param name="className" value="${entity.name}"/>
                        </c:url>
                        <a href="${url}">Modify</a>
                    </c:if>
                </div>
                <dl>
                    <dt>Category:</dt>
                    <dd><c:out value="${entity.category.name}"/></dd>
                    <dt>Team:</dt>
                    <dd><c:out value="${entity.team.name}"/></dd>
                    <dt>Priority:</dt>
                    <dd><c:out value="${entity.priority.name}"/></dd>
                    <dt>Corrective Action:</dt>
                    <dd><c:out value="${entity.correctiveAction}"/></dd>
                    <dt>Rationale:</dt>
                    <dd><c:out value="${entity.rationale}"/></dd>
                    <dt>Filterable:</dt>
                    <dd><c:out value="${entity.filterable ? 'Yes' : 'No'}"/></dd>
                    <dt>Latchable:</dt>
                    <dd><c:out value="${entity.latchable ? 'Yes' : 'No'}"/></dd>
                    <dt>On Delay (seconds):</dt>
                    <dd><c:out value="${entity.onDelaySeconds eq null ? 'None' : entity.onDelaySeconds}"/></dd>
                    <dt>Off Delay (seconds):</dt>
                    <dd><c:out value="${entity.offDelaySeconds eq null ? 'None' : entity.offDelaySeconds}"/></dd>
                </dl>
            </div>
        </section>
    </jsp:body>         
</t:inventory-page>