<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="title" value="Alarm Instance"/>
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
                    <span>Instances</span>
                </li>
                <li>
                    <h2 id="page-header-title"><c:out value="${instance.name}"/></h2>
                </li>
            </ul>
        </div>
        <section>
            <div class="dialog-content">
                <div class="dialog-links">
                    <a class="dialog-only-link" href="${pageContext.request.contextPath}/inventory/instances/detail?instanceId=${instance.instanceId}">Link</a>
                    <c:if test="${editable}">
                        <c:url var="url" value="/inventory/instances">
                            <c:param name="instanceName" value="${instance.name}"/>
                        </c:url>
                        <a href="${url}">Modify</a>
                    </c:if>
                </div>
                <dl>
                    <dt>Class:</dt>
                    <c:url var="url" value="/inventory/classes/detail">
                        <c:param name="classId" value="${instance.alarmClass.classId}"/>
                    </c:url>
                    <dd><a href="${url}"><c:out value="${instance.alarmClass.name}"/></a></dd>
                    <dt>Location:</dt>
                    <dd><c:out value="${instance.locationNameCsv}"/></dd>
                    <dt>Device:</dt>
                    <dd><c:out value="${instance.device eq null ? 'None' : instance.device}"/></dd>
                    <dt>Screen Command:</dt>
                    <dd><c:out value="${instance.screenCommand eq null ? 'None' : instance.screenCommand}"/></dd>
                    <dt>Masked By:</dt>
                    <dd><c:out value="${instance.maskedBy eq null ? 'None' : instance.maskedBy}"/></dd>
                    <dt>Source:</dt>
                    <dd><c:out value="${instance.epicsSource eq null ? 'None' : 'EPICS PV: '.concat(instance.epicsSource.pv)}"/></dd>
                </dl>
            </div>
        </section>
    </jsp:body>         
</t:inventory-page>