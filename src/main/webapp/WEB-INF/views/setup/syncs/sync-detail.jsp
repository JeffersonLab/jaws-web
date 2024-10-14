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
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/sync-detail.js"></script>
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
                            <c:param name="edit" value="Y"/>
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
                            <dt>Description</dt>
                            <dd>
                                <c:out value="${rule.description}"/>
                            </dd>
                            <dt>Base Query</dt>
                            <dd class="query">
                                <c:out value="${rule.query}"/>
                            </dd>
                            <dt>Property Expression</dt>
                            <dd class="query">
                                <ul>
                                <c:forEach items="${rule.expressionArray}" var="operand">
                                    <li><c:out value="${operand}"/></li>
                                </c:forEach>
                                </ul>
                            </dd>
                            <dt>Full URL</dt>
                            <dd class="query">
                                <div><a href="${rule.getHTMLURL()}">HTML</a> | <a href="${rule.searchURL}">JSON</a></div>
                            </dd>
                        </dl>
                        <h3>Template</h3>
                        <dl>
                            <dt>Name</dt>
                            <dd>{ElementName} {Action}</dd>
                            <dt>Location</dt>
                            <dd>{function:locationFromSegMask(SegMask)}</dd>
                            <dt>Screen Command</dt>
                            <dd><c:out value="${empty rule.screenCommand ? 'None' : rule.screenCommand}"/></dd>
                            <dt>PV</dt>
                            <dd><c:out value="${empty rule.pv ? 'None' : rule.pv}"/></dd>
                        </dl>
                        <h3>Results</h3>
                        <c:choose>
                            <c:when test="${error ne null}">
                                Error: <c:out value="${error}"/>
                            </c:when>
                            <c:when test="${fn:length(remoteList) > 0}">
                                <div>Found ${fn:length(remoteList)} remote alarms, ${fn:length(localList)} local
                                    alarms, and ${collisionCount} name/pv collisions (${matchCount} Matched, ${addCount} Add, ${removeCount} Remove, ${updateCount} Update, ${linkCount} Link)
                                </div>
                                <table class="data-table">
                                    <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Name</th>
                                        <th>Location</th>
                                        <th>Screen Command</th>
                                        <th>PV</th>
                                        <th><button ${diff.hasChanges() ? '' : 'disabled="disabled"'} id="apply-all-button" type="button">Apply All</button></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${diff.addList}" var="alarm">
                                        <tr data-action-id="${alarm.action.actionId}"
                                            data-location-id-csv="${alarm.locationIdCsv}" data-device="${alarm.device}"
                                            data-screen-command="${alarm.screenCommand}"
                                            data-managed-by="${alarm.managedBy}" data-masked-by="${alarm.maskedBy}"
                                            data-pv="${alarm.pv}" data-rule-id="${alarm.syncRule.syncRuleId}"
                                            data-element-id="${alarm.syncElementId}"
                                            class="add-row">
                                            <td><c:out value="${alarm.syncElementId}"/></td>
                                            <td><c:out value="${alarm.name}"/></td>
                                            <td><c:out value="${alarm.locationNameCsv}"/></td>
                                            <td><c:out value="${alarm.screenCommand}"/></td>
                                            <td><c:out value="${alarm.pv}"/></td>
                                            <td>
                                                <c:set value="${false}" var="linkCreated"/>
                                                <c:set value="${danglingByNameList[alarm.name]}" var="danglingNameAlarm"/>
                                                <c:set value="${danglingByPvList[alarm.pv]}" var="danglingPvAlarm"/>
                                                <c:if test="${danglingNameAlarm eq null && danglingPvAlarm eq null}">
                                                    <button class="add" type="button">Add</button>
                                                </c:if>
                                                <c:if test="${danglingNameAlarm ne null}">
                                                    <c:set value="${true}" var="linkCreated"/>
                                                    <c:url value="/inventory/alarms/${jaws:urlEncodePath(alarm.name)}"
                                                           var="url">
                                                    </c:url>
                                                    <div>
                                                        <button class="link autolink" type="button" data-alarm-id="${danglingNameAlarm.alarmId}">Link</button>
                                                        <a title="Alarm Information" class="dialog-ready"
                                                           data-dialog-title="Alarm Information: ${fn:escapeXml(alarm.name)}"
                                                           href="${url}">Existing Name</a>
                                                    </div>
                                                </c:if>
                                                <c:if test="${danglingPvAlarm ne null}">
                                                    <c:url value="/inventory/alarms/${jaws:urlEncodePath(danglingPvAlarm.name)}"
                                                           var="url">
                                                    </c:url>
                                                    <div>
                                                        <button class="link ${linkCreated ? '' : 'autolink'}" type="button" data-alarm-id="${danglingPvAlarm.alarmId}">Link</button>
                                                        <a title="Alarm Information" class="dialog-ready"
                                                           data-dialog-title="Alarm Information: ${fn:escapeXml(danglingPvAlarm.name)}"
                                                           href="${url}">Existing PV</a>
                                                    </div>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:forEach items="${diff.removeList}" var="alarm">
                                        <tr data-id="${alarm.alarmId}"
                                            class="remove-row">
                                            <td><c:out value="${alarm.syncElementId}"/></td>
                                            <td><c:out value="${alarm.name}"/></td>
                                            <td><c:out value="${alarm.locationNameCsv}"/></td>
                                            <td><c:out value="${alarm.screenCommand}"/></td>
                                            <td><c:out value="${alarm.pv}"/></td>
                                            <td><button class="remove" type="button">Remove</button></td>
                                        </tr>
                                    </c:forEach>
                                    <c:forEach items="${diff.updateList}" var="alarm">
                                        <c:set value="${not empty remoteList[alarm.syncElementId].screenCommand}" var="screenCommandSync"/>
                                        <c:set value="${not empty remoteList[alarm.syncElementId].pv}" var="pvSync"/>
                                        <tr data-id="${alarm.alarmId}"
                                            data-name="${remoteList[alarm.syncElementId].name}"
                                            data-action-id="${alarm.action.actionId}"
                                            data-location-id-csv="${remoteList[alarm.syncElementId].locationIdCsv}"
                                            data-device="${alarm.device}"
                                            data-screen-command="${screenCommandSync ? remoteList[alarm.syncElementId].screenCommand : alarm.screenCommand}"
                                            data-managed-by="${alarm.managedBy}"
                                            data-masked-by="${alarm.maskedBy}"
                                            data-pv="${pvSync ? remoteList[alarm.syncElementId].pv : alarm.pv}"
                                            data-rule-id="${alarm.syncRule.syncRuleId}"
                                            data-element-id="${alarm.syncElementId}">
                                            <td><c:out value="${alarm.syncElementId}"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${alarm.name eq remoteList[alarm.syncElementId].name}">
                                                        <c:out value="${alarm.name}"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="remote"><c:out value="${remoteList[alarm.syncElementId].name}"/></div>
                                                        <div class="local"><c:out value="${alarm.name}"/></div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${alarm.locationNameCsv eq remoteList[alarm.syncElementId].locationNameCsv}">
                                                        <c:out value="${alarm.locationNameCsv}"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="remote"><c:out value="${remoteList[alarm.syncElementId].locationNameCsv}"/></div>
                                                        <div class="local"><c:out value="${alarm.locationNameCsv}"/></div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not screenCommandSync || alarm.screenCommand eq remoteList[alarm.syncElementId].screenCommand}">
                                                        <c:out value="${alarm.screenCommand}"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="remote"><c:out value="${remoteList[alarm.syncElementId].screenCommand}"/></div>
                                                        <div class="local"><c:out value="${alarm.screenCommand}"/></div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not pvSync || alarm.pv eq remoteList[alarm.syncElementId].pv}">
                                                        <c:out value="${alarm.pv}"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="remote"><c:out value="${remoteList[alarm.syncElementId].pv}"/></div>
                                                        <div class="local"><c:out value="${alarm.pv}"/></div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td><button class="update" type="button">Update</button></td>
                                        </tr>
                                    </c:forEach>
                                    <c:forEach items="${diff.matchList}" var="alarm">
                                        <tr>
                                            <td><c:out value="${alarm.syncElementId}"/></td>
                                            <td>
                                                <c:url value="/inventory/alarms/${jaws:urlEncodePath(alarm.name)}"
                                                       var="url">
                                                </c:url>
                                                <a title="Alarm Information" class="dialog-ready"
                                                   data-dialog-title="Alarm Information: ${fn:escapeXml(alarm.name)}"
                                                   href="${url}"><c:out value="${alarm.name}"/></a>
                                            </td>
                                            <td><c:out value="${alarm.locationNameCsv}"/></td>
                                            <td><c:out value="${alarm.screenCommand}"/></td>
                                            <td><c:out value="${alarm.pv}"/></td>
                                            <td></td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <div>No results found.</div>
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