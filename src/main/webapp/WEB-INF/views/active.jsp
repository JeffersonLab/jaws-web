<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="Active Alarms"/>
<t:page title="${title}">  
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/active.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/active.js"></script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <h2 id="page-header-title"><c:out value="${title}"/><span id="alarm-count">0</span></h2>
            <div id="liveness-heartbeat">Liveness Heartbeat: <span id="liveness-ts">None</span></div>
            <img draggable="false" alt="machine" width="1100" height="600" src="${pageContext.request.contextPath}/resources/img/accelerator.png"/>
            <button type="button" id="show-all">Show All</button>
        </section>
        <div id="all-dialog" class="dialog" title="Active Alarms">
            <table id="alarm-table" class="data-table">
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
        </div>
    </jsp:body>
</t:page>
