<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" data-context-path="${pageContext.request.contextPath}" data-app-version="${initParam['releaseNumber']}"/>
    <title>JAWS Admin GUI</title>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico"/>
    <c:url value="/resources/css/site.css" var="siteCssUrl">
        <c:param name="v" value="${initParam['releaseNumber']}"/>
    </c:url>
    <link rel="stylesheet" href="${siteCssUrl}"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/libs/jquery-ui-1.12.1.smoothness/jquery-ui.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/libs/tabulator-4.9.3/css/tabulator.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/toast-ui-3.1.3/toastui-editor.css"/>
</head>
<body>
<header>
    <img src="${pageContext.request.contextPath}/resources/img/logo128x128.png"/>
    <h1>JAWS Admin GUI</h1>
</header>
<div id="tabs">
    <ul>
        <li><a href="#registrations-panel">Registrations</a></li>
        <li><a href="#classes-panel">Classes</a></li>
        <li><a href="#instances-panel">Instances</a></li>
        <li><a href="#locations-panel">Locations</a></li>
        <li><a href="#categories-panel">Categories</a></li>
    </ul>
    <t:panel id="registrations" title="Registrations" editable="false">
        <jsp:body>
            <div id="view-effective-dialog" class="dialog" title="Registration (Effective)">
                <dl>
                    <dt>Name</dt>
                    <dd id="view-effective-name"></dd>
                    <dt>Class</dt>
                    <dd id="view-effective-class"></dd>
                    <dt>EPICS PV</dt>
                    <dd id="view-effective-epicspv"></dd>
                    <dt>Priority</dt>
                    <dd id="view-effective-priority"></dd>
                    <dt>Location</dt>
                    <dd id="view-effective-location"></dd>
                    <dt>Category</dt>
                    <dd id="view-effective-category"></dd>
                    <dt>Corrective Action</dt>
                    <dd id="view-effective-action"></dd>
                    <dt>Rationale</dt>
                    <dd id="view-effective-rationale"></dd>
                    <dt>Point of Contact Username</dt>
                    <dd id="view-effective-contact"></dd>
                    <dt>Filterable</dt>
                    <dd id="view-effective-filterable"></dd>
                    <dt>Latching</dt>
                    <dd id="view-effective-latching"></dd>
                    <dt>On-Delay Seconds</dt>
                    <dd id="view-effective-on-delay"></dd>
                    <dt>Off-Delay Seconds</dt>
                    <dd id="view-effective-off-delay"></dd>
                    <dt>Masked By</dt>
                    <dd id="view-effective-masked-by"></dd>
                    <dt>Screen Command</dt>
                    <dd id="view-effective-screen-command"></dd>
                </dl>
            </div>
        </jsp:body>
    </t:panel>
    <t:panel id="classes" title="Classes">
        <jsp:body>
            <div id="view-class-dialog" class="dialog" title="Class">
                <dl>
                    <dt>Name</dt>
                    <dd id="view-class-name"></dd>
                    <dt>Priority</dt>
                    <dd id="view-class-priority"></dd>
                    <dt>Category</dt>
                    <dd id="view-class-category"></dd>
                    <dt>Corrective Action</dt>
                    <dd id="view-class-action"></dd>
                    <dt>Rationale</dt>
                    <dd id="view-class-rationale"></dd>
                    <dt>Point of Contact Username</dt>
                    <dd id="view-class-contact"></dd>
                    <dt>Filterable</dt>
                    <dd id="view-class-filterable"></dd>
                    <dt>Latching</dt>
                    <dd id="view-class-latching"></dd>
                    <dt>On-Delay Seconds</dt>
                    <dd id="view-class-on-delay"></dd>
                    <dt>Off-Delay Seconds</dt>
                    <dd id="view-class-off-delay"></dd>
                </dl>
            </div>
            <div id="class-dialog" class="dialog" title="New Class">
                <form id="class-form" onsubmit="return false;">
                    <fieldset>
                        <label>Class Name</label>
                        <input type="text" name="name" id="class-name-input" value=""/>
                    </fieldset>
                    <fieldset>
                        <label>Priority</label>
                        <select id="priority-select" name="priority"></select>
                        <label>Category</label>
                        <select id="category-select" name="category"></select>
                        <label>Corrective Action (Markdown format)</label>
                        <textarea name="correctiveaction" id="class-correctiveaction-textarea" style="display: none;"></textarea>
                        <div id="class-correctiveaction-editor"></div>
                        <label>Rationale (Markdown format)</label>
                        <textarea name="rationale" id="class-rationale-textarea" style="display: none;"></textarea>
                        <div id="class-rationale-editor"></div>
                        <label>Point of Contact Username</label>
                        <input type="text" name="pocusername" id="class-pocusername-input" value=""/>
                        <fieldset>
                            <legend>Filterable</legend>
                            <label class="radio-label">True</label>
                            <input type="radio" name="filterable" checked="checked" value="true"/>
                            <label class="radio-label">False</label>
                            <input type="radio" name="filterable" value="false"/>
                        </fieldset>
                        <fieldset>
                            <legend>Latching</legend>
                            <label class="radio-label">True</label>
                            <input type="radio" name="latching" checked="checked" value="true"/>
                            <label class="radio-label">False</label>
                            <input type="radio" name="latching" value="false"/>
                        </fieldset><label>On Delay Seconds</label>
                        <input type="number" name="ondelayseconds" id="class-ondelay-input" value=""/>
                        <label>Off Delay Seconds</label>
                        <input type="number" name="offdelayseconds" id="class-offdelay-input" value=""/>
                    </fieldset>
                    <input id="class-submit" type="submit" tabindex="-1" style="position:absolute; top:-1000px"/>
                </form>
            </div>
        </jsp:body>
    </t:panel>
    <t:panel id="instances" title="Instances">
        <jsp:body>
            <div id="view-instance-dialog" class="dialog" title="Registration (Instance)">
                <dl>
                    <dt>Name</dt>
                    <dd id="view-registration-name"></dd>
                    <dt>Class</dt>
                    <dd id="view-registration-class"></dd>
                    <dt>EPICS PV</dt>
                    <dd id="view-registration-epicspv"></dd>
                    <dt>Location</dt>
                    <dd id="view-registration-location"></dd>
                    <dt>Masked By</dt>
                    <dd id="view-registration-masked-by"></dd>
                    <dt>Screen Command</dt>
                    <dd id="view-registration-screen-command"></dd>
                </dl>
            </div>
            <div id="batch-registration-dialog" class="dialog" title="Edit All On Current Page">
                <form id="batch-form" onsubmit="return false;">
                    <p>Edit entire page of registration records</p>
                    <label>Field to modify</label>
                    <select id="batch-update-select">
                        <option value="class" selected="selected">Class</option>
                        <option value="location">Location</option>
                    </select>
                    <label>Value</label>
                    <input id="batch-update-input" type="text" value=""/>
                    <input id="batch-submit" type="submit" tabindex="-1" style="position:absolute; top:-1000px"/>
                </form>
            </div>
            <div id="registration-dialog" class="dialog" title="New Registration (Instance)">
                <form id="registered-form" onsubmit="return false;">
                    <fieldset>
                        <label>Alarm Name</label>
                        <input type="text" name="name" id="alarm-name-input" value=""/>
                        <label>Class</label>
                        <input type="text" name="class" id="registered-class-input" value=""/>
                        <label>EPICS PV</label>
                        <input type="text" name="epicspv" id="epicspv-input" value=""/>
                        <label>Location</label>
                        <span id="registered-location-select-span"></span>
                        <label>Masked By</label>
                        <input type="text" name="maskedby" id="registered-maskedby-input" value=""/>
                        <label>Screen Command</label>
                        <input type="text" name="screencommand" id="registered-screencommand-input" value=""/>
                    </fieldset>
                    <input id="registered-submit" type="submit" tabindex="-1" style="position:absolute; top:-1000px"/>
                </form>
            </div>
        </jsp:body>
    </t:panel>
    <t:panel id="locations" title="Locations">
        <jsp:body>
            <div id="view-location-dialog" class="dialog" title="Location">
                <dl>
                    <dt>Name</dt>
                    <dd id="view-location-name"></dd>
                    <dt>Parent</dt>
                    <dd id="view-location-parent"></dd>
                </dl>
            </div>
            <div id="location-dialog" class="dialog" title="New Location">
                <form id="location-form" onsubmit="return false;">
                    <fieldset>
                        <label>Location Name</label>
                        <input type="text" name="name" id="location-name-input" value=""/>
                    </fieldset>
                    <fieldset>
                        <label>Parent</label>
                        <input type="text" name="parent" id="location-parent-input" value=""/>
                    </fieldset>
                    <input id="location-submit" type="submit" tabindex="-1" style="position:absolute; top:-1000px"/>
                </form>
            </div>
        </jsp:body>
    </t:panel>
    <t:panel id="categories" title="Categories">
        <jsp:body>
            <div id="view-category-dialog" class="dialog" title="Category">
                <dl>
                    <dt>Name</dt>
                    <dd id="view-category-name"></dd>
                </dl>
            </div>
            <div id="category-dialog" class="dialog" title="New Category">
                <form id="category-form" onsubmit="return false;">
                    <fieldset>
                        <label>Category Name</label>
                        <input type="text" name="name" id="category-name-input" value=""/>
                    </fieldset>
                    <input id="category-submit" type="submit" tabindex="-1" style="position:absolute; top:-1000px"/>
                </form>
            </div>
        </jsp:body>
    </t:panel>
</div>
<div id="version-div">v<c:out value="${initParam['releaseNumber']}"/></div>
<script src="${pageContext.request.contextPath}/resources/libs/tabulator-4.9.3/js/tabulator.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/libs/jquery-ui-1.12.1.smoothness/external/jquery/jquery.js"></script>
<script src="${pageContext.request.contextPath}/resources/libs/jquery-ui-1.12.1.smoothness/jquery-ui.min.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/modules/toast-ui-3.1.3/toastui-all.min.mjs"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/modules/page-1.11.6/page.min.mjs"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/modules/dexie-3.2.1/dexie.min.mjs"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/modules/jaws-admin-gui-${initParam['releaseNumber']}/main.mjs"></script>
</body>
</html>