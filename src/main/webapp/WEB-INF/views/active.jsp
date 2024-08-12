<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="title" value="Active Alarms"/>
<t:page title="${title}">  
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/active.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script>
            jlab.materializedLocations = ${materializedLocationsArrayStr};
        </script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/active.js"></script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <s:filter-flyout-widget ribbon="true" clearButton="true">
                <form id="filter-form" method="get" action="active">
                    <div id="filter-form-panel">
                        <fieldset>
                            <legend>Filter</legend>
                            <ul class="key-value-list">
                                <li>
                                    <div class="li-key">
                                        <label for="location-select">Location</label>
                                    </div>
                                    <div class="li-value">
                                        <select id="location-select" name="locationId" multiple="multiple">
                                            <c:forEach items="${locationRoot.children}" var="child">
                                                <t:hierarchical-select-option node="${child}" level="0"
                                                                              parameterName="locationId"/>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </li>
                            </ul>
                        </fieldset>
                    </div>
                    <input id="filter-form-submit-button" type="submit" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <h2 id="page-header-title"><c:out value="${title}"/><span class="status" id="alarm-count">0</span><span class="status" id="loading"><span class="button-indicator"></span> Loading...</span></h2>
            <div id="liveness-heartbeat">Liveness Heartbeat: <span id="liveness-ts">None</span></div>
            <div class="message-box"><c:out value="${selectionMessage}"/></div>
            <div id="diagram-container">
                <img draggable="false" alt="machine" src="${pageContext.request.contextPath}/resources/img/accelerator.png"/>
            </div>
            <span id="link-bar">
                List:
                <a id="list-active-link" href="${pageContext.request.contextPath}/notifications${listActiveParams}">Active</a>
                <span id="unregistered" class="initially-none"> | Unregistered <a href="${pageContext.request.contextPath}/notifications?state=Active&registered=N">(<span id="unregistered-count"></span>)</a></span>
                <span id="unfilterable" class="initially-none"> | Unfilterable <a href="${pageContext.request.contextPath}/notifications?state=Active&filterable=N">(<span id="unfilterable-count"></span>)</a></span>
            </span>
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
