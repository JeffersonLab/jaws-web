<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="jaws" uri="http://jlab.org/jaws/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="title" value="Sync Rules"/>
<t:setup-page title="${title}">
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/syncs.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/syncs.js"></script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <s:filter-flyout-widget clearButton="true">
                <form id="filter-form" method="get" action="syncs">
                    <div id="filter-form-panel">
                        <fieldset>
                            <legend>Filter</legend>
                            <ul class="key-value-list">
                                <li>
                                    <div class="li-key">
                                        <label for="sync-id">Sync ID</label>
                                    </div>
                                    <div class="li-value">
                                        <input id="sync-id"
                                               name="syncId" value="${fn:escapeXml(param.syncId)}"/>
                                    </div>
                                </li>
                                <li>
                                    <div class="li-key">
                                        <label for="action-name">Action Name</label>
                                    </div>
                                    <div class="li-value">
                                        <input id="action-name"
                                               name="actionName" value="${fn:escapeXml(param.actionName)}"
                                               placeholder="action name"/>
                                        <div>(use % as wildcard)</div>
                                    </div>
                                </li>
                            </ul>
                        </fieldset>
                    </div>
                    <input type="hidden" id="offset-input" name="offset" value="0"/>
                    <input id="filter-form-submit-button" type="submit" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <h2 id="page-header-title"><c:out value="${title}"/></h2>
            <div class="message-box"><c:out value="${selectionMessage}"/></div>
            <div id="chart-wrap" class="chart-wrap-backdrop">
                <c:set var="readonly" value="${!pageContext.request.isUserInRole('jaws-admin')}"/>
                <c:if test="${not readonly}">
                    <s:editable-row-table-controls>
                    </s:editable-row-table-controls>
                </c:if>
                <table class="data-table outer-table">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Action</th>
                        <th>Deployment</th>
                        <th>Query</th>
                        <th></th>
                        <th class="scrollbar-header"><span class="expand-icon" title="Expand Table"></span></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td class="inner-table-cell" colspan="7">
                            <div class="pane-decorator">
                                <div class="table-scroll-pane">
                                    <table class="data-table inner-table stripped-table ${readonly ? '' : 'uniselect-table editable-row-table'}">
                                        <tbody>
                                        <c:forEach items="${syncList}" var="sync">
                                            <tr data-id="${sync.syncRuleId}" data-action-id="${sync.action.actionId}" data-screencommand="${fn:escapeXml(sync.screenCommand)}" data-pv="${fn:escapeXml(sync.pv)}">
                                                <td><c:out value="${sync.syncRuleId}"/></td>
                                                <td>
                                                    <c:url value="/inventory/actions/${jaws:urlEncodePath(sync.action.name)}" var="url">
                                                    </c:url>
                                                    <a title="Action Information" class="dialog-ready"
                                                       data-dialog-title="Action Information: ${fn:escapeXml(sync.action.name)}"
                                                       href="${url}"><c:out
                                                        value="${sync.action.name}"/></a>
                                                </td>
                                                <td><c:out value="${sync.deployment}"/></td>
                                                <td><c:out value="${sync.query}"/></td>
                                                <td>
                                                    <!-- Use onclick to avoid https://bugs.webkit.org/show_bug.cgi?id=30103 -->
                                                    <c:url value="/setup/syncs/${jaws:urlEncodePath(sync.syncRuleId)}" var="url">
                                                    </c:url>
                                                    <form method="get"
                                                          action="${pageContext.request.contextPath}/setup/syncs/${fn:escapeXml(sync.syncRuleId)}">
                                                        <button class="single-char-button" type="button" onclick="window.location.href = '${url}';  return false;">&rarr;</button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
                <button id="previous-button" type="button" data-offset="${paginator.previousOffset}"
                        value="Previous"${paginator.previous ? '' : ' disabled="disabled"'}>Previous
                </button>
                <button id="next-button" type="button" data-offset="${paginator.nextOffset}"
                        value="Next"${paginator.next ? '' : ' disabled="disabled"'}>Next
                </button>
            </div>
        </section>
        <s:editable-row-table-dialog>
            <form id="row-form">
                <ul class="key-value-list">
                    <li>
                        <div class="li-key">
                            <label for="row-action">Action</label>
                        </div>
                        <div class="li-value">
                            <select id="row-action" required="required">
                                <option value="">&nbsp;</option>
                                <c:forEach items="${actionList}" var="action">
                                    <option value="${action.actionId}">
                                        <c:out value="${action.name}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                    </li>
                    <li>
                        <div class="li-key">
                            <label for="row-deployment">Deployment</label>
                        </div>
                        <div class="li-value">
                            <select id="row-deployment" required="required">
                                <option value="">&nbsp;</option>
                                <option value="CED">CED</option>
                                <option value="LED">LED</option>
                                <option value="UED">UED</option>
                            </select>
                        </div>
                    </li>
                    <li>
                        <div class="li-key">
                            <label for="row-query">Query</label>
                        </div>
                        <div class="li-value">
                            <input type="text" required="required" id="row-query"/>
                        </div>
                    </li>
                </ul>
                <div>Template</div>
                <ul class="key-value-list">
                    <li>
                        <div class="li-key">
                            <label for="row-name">Name</label>
                        </div>
                        <div class="li-value">
                            <input type="text" id="row-name" value="{ElementName} {Action}" disabled="disabled"/>
                        </div>
                    </li>
                    <li>
                        <div class="li-key">
                            <label for="row-location">Location</label>
                        </div>
                        <div class="li-value">
                            <input type="text" id="row-location" value="{function:locationFromSegMask(SegMask)}" disabled="disabled"/>
                        </div>
                    </li>
                    <li>
                        <div class="li-key">
                            <label for="row-screencommand">Screen Command</label>
                        </div>
                        <div class="li-value">
                            <input type="text" id="row-screencommand"/>
                        </div>
                    </li>
                    <li>
                        <div class="li-key">
                            <label for="row-pv">PV</label>
                        </div>
                        <div class="li-value">
                            <input type="text" id="row-pv"/>
                        </div>
                    </li>
                </ul>
                <div>Expression variables: {ElementName}, {EPICSName}, {Deployment}</div>
            </form>
        </s:editable-row-table-dialog>
    </jsp:body>         
</t:setup-page>