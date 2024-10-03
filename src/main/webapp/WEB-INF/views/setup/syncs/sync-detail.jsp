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
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/sync-detail.js"></script>
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
                            <dt>Server</dt>
                            <dd><c:out value="${rule.syncServer.name}"/></dd>
                            <dt>Query</dt>
                            <dd>
                                <a href="${rule.searchURL}"><c:out value="${rule.query}"/></a>
                            </dd>
                        </dl>
                        <h3>Template</h3>
                        <dl>
                            <dt>Name</dt>
                            <dd>{ElementName} {Action}</dd>
                            <dt>Location</dt>
                            <dd>{function:locationFromSegMask(SegMask)}</dd>
                            <dt>Screen Command</dt>
                            <dd><c:out value="${rule.screenCommand}"/></dd>
                            <dt>PV</dt>
                            <dd><c:out value="${rule.pv}"/></dd>
                        </dl>
                        <h3>Results</h3>
                        <c:choose>
                            <c:when test="${error ne null}">
                                Error: <c:out value="${error}"/>
                            </c:when>
                            <c:when test="${fn:length(remoteList) > 0}">
                                <div>Found ${fn:length(remoteList)} remote alarms (vs ${fn:length(localList)} local alarms)</div>
                                <table class="data-table">
                                    <thead>
                                        <tr>
                                            <th>Name</th>
                                            <th>Location</th>
                                            <th>Screen Command</th>
                                            <th>PV</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${remoteList.values()}" var="alarm">
                                            <tr>
                                                <td><c:out value="${alarm.name}"/></td>
                                                <td><c:out value="${alarm.locationNameCsv}"/></td>
                                                <td><c:out value="${alarm.screenCommand}"/></td>
                                                <td><c:out value="${alarm.pv}"/></td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <div>No results found.</div>
                            </c:otherwise>
                        </c:choose>
                        <h3>Diff</h3>
                        <h4>Add</h4>
                        <c:choose>
                            <c:when test="${fn:length(diff.addList) > 0}">
                                <table id="add-table" class="data-table">
                                    <tbody>
                                        <c:forEach items="${diff.addList}" var="alarm">
                                            <tr data-action-id="${alarm.action.actionId}" data-location-id-csv="${alarm.locationIdCsv}" data-device="${alarm.device}" data-screen-command="${alarm.screenCommand}" data-managed-by="${alarm.managedBy}" data-masked-by="${alarm.maskedBy}" data-pv="${alarm.pv}" data-rule-id="${alarm.syncRule.syncRuleId}" data-element-id="${alarm.syncElementId}">
                                                <td><c:out value="${alarm.name}"/></td>
                                                <td><button type="button">Add</button></td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <span>None</span>
                            </c:otherwise>
                        </c:choose>
                        <h4>Remove</h4>
                        <c:choose>
                            <c:when test="${fn:length(diff.removeList) > 0}">
                                <table id="remove-table" class="data-table">
                                    <tbody>
                                    <c:forEach items="${diff.removeList}" var="alarm">
                                        <tr data-id="${alarm.alarmId}">
                                            <td><c:out value="${alarm.name}"/></td>
                                            <td><button type="button">Remove</button></td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <span>None</span>
                            </c:otherwise>
                        </c:choose>
                        <h4>Update</h4>
                        <c:choose>
                            <c:when test="${fn:length(diff.updateList) > 0}">
                                <table id="update-table" class="data-table">
                                    <tbody>
                                    <c:forEach items="${diff.updateList}" var="alarm">
                                        <tr data-id="${alarm.alarmId}" data-action-id="${alarm.action.actionId}" data-location-id-csv="${alarm.locationIdCsv}" data-device="${alarm.device}" data-screen-command="${alarm.screenCommand}" data-managed-by="${alarm.managedBy}" data-masked-by="${alarm.maskedBy}" data-pv="${alarm.pv}">
                                            <td><c:out value="${alarm.name}"/></td>
                                            <td><button type="button">Update</button></td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <span>None</span>
                            </c:otherwise>
                        </c:choose>
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