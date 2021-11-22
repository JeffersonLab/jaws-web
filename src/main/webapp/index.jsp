<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" data-context-path="${pageContext.request.contextPath}"/>
    <title>JAWS Admin GUI</title>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/site.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/jquery-ui-1.12.1.smoothness/jquery-ui.min.css">
    <link href="${pageContext.request.contextPath}/resources/tabulator-4.9.3/css/tabulator.min.css" rel="stylesheet">
</head>
<body>
<header>
    <img src="${pageContext.request.contextPath}/resources/img/logo128x128.png"/>
    <h1>JAWS Admin GUI</h1>
</header>
<div id="tabs">
    <ul>
        <li><a href="#classes-panel">Classes</a></li>
        <li><a href="#registrations-panel">Registrations</a></li>
        <li><a href="#effective-panel">Effective</a></li>
    </ul>
    <div id="classes-panel">
        <h2>Alarm Classes</h2>
        <div class="toolbar">
            <form id="class-search-form" class="search-form" onsubmit="return false;">
                <input type="text" value="" id="class-search-input" class="search-input" placeholder="field=value,field=value"/>
                <button type="button" id="search-class-button">Search</button> |
            </form>
            <button type="button" id="new-class-button">New</button> |
            <button type="button" id="view-class-button" class="selected-row-action">View</button>
            <button type="button" id="edit-class-button" class="selected-row-action" disabled="disabled">Edit</button>
            <button type="button" id="delete-class-button" class="selected-row-action" disabled="disabled">Delete</button> |
            <span>Records: </span><span class="record-count"></span>
        </div>
        <div class="table-wrap">
            <div id="classes-table"></div>
        </div>
        <div id="view-class-dialog" class="dialog" title="Class">
            <dl>
                <dt>Name</dt>
                <dd id="view-class-name"></dd>
                <dt>Priority</dt>
                <dd id="view-class-priority"></dd>
                <dt>Location</dt>
                <dd id="view-class-location"></dd>
                <dt>Category</dt>
                <dd id="view-class-category"></dd>
                <dt>Rationale</dt>
                <dd id="view-class-rationale"></dd>
                <dt>Corrective Action</dt>
                <dd id="view-class-action"></dd>
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
                <dt>Masked By</dt>
                <dd id="view-class-masked-by"></dd>
                <dt>Screen Path</dt>
                <dd id="view-class-screen-path"></dd>
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
                    <label>Location</label>
                    <select id="location-select" name="location"></select>
                    <label>Category</label>
                    <select id="category-select" name="category"></select>
                    <label>Rationale</label>
                    <textarea name="rationale" id="class-rationale-textarea"></textarea>
                    <label>Corrective Action</label>
                    <textarea name="correctiveaction" id="class-correctiveaction-textarea"></textarea>
                    <label>Point of Contact Username</label>
                    <input type="text" name="pocusername" id="class-pocusername-input" value=""/>
                    <fieldset>
                        <legend>Filterable</legend>
                        <label>True</label>
                        <input type="radio" name="filterable" checked="checked" value="true"/>
                        <label>False</label>
                        <input type="radio" name="filterable" value="false"/>
                    </fieldset>
                    <fieldset>
                        <legend>Latching</legend>
                        <label>True</label>
                        <input type="radio" name="latching" checked="checked" value="true"/>
                        <label>False</label>
                        <input type="radio" name="latching" value="false"/>
                    </fieldset><label>On Delay Seconds</label>
                    <input type="number" name="ondelayseconds" id="class-ondelay-input" value=""/>
                    <label>Off Delay Seconds</label>
                    <input type="number" name="offdelayseconds" id="class-offdelay-input" value=""/>
                    <label>Masked By</label>
                    <input type="text" name="maskedby" id="class-maskedby-input" value=""/>
                    <label>Screen Path</label>
                    <input type="text" name="screenpath" id="class-screenpath-input" value=""/>
                </fieldset>
                <input id="class-submit" type="submit" tabindex="-1" style="position:absolute; top:-1000px"/>
            </form>
        </div>
    </div>
    <div id="registrations-panel">
        <h2>Alarm Registrations</h2>
        <div class="toolbar">
            <form id="registered-search-form" class="search-form" onsubmit="return false;">
                <input type="text" value="" id="registration-search-input" class="search-input" placeholder="field=value,field=value"/>
                <button type="button" id="search-registration-button">Search</button> |
            </form>
            <button type="button" id="new-registration-button">New</button> |
            <button type="button" id="edit-registration-button" class="selected-row-action" disabled="disabled">Edit</button>
            <button type="button" id="delete-registration-button" class="selected-row-action" disabled="disabled">Delete</button> |
            <span>Records: </span><span class="record-count"></span>
        </div>
        <div class="table-wrap">
            <div id="registrations-table"></div>
        </div>
        <div id="registration-dialog" class="dialog" title="New Registration">
            <form id="registered-form" onsubmit="return false;">
                <fieldset>
                    <label>Alarm Name</label>
                    <input type="text" name="name" id="alarm-name-input" value=""/>
                    <label>Class</label>
                    <input type="text" name="class" id="registered-class-input" value=""/>
                    <label>EPICS PV</label>
                    <input type="text" name="epicspv" id="epicspv-input" value=""/>
                </fieldset>
                <h2>Class Overrides</h2>
                <div class="footnote">(Blank fields inherit from class)</div>
                <fieldset>
                    <label>Priority</label>
                    <span id="registered-priority-select-span"></span>
                    <label>Location</label>
                    <span id="registered-location-select-span"></span>
                    <label>Category</label>
                    <span id="registered-category-select-span"></span>
                    <label>Rationale</label>
                    <textarea name="rationale" id="registered-rationale-textarea"></textarea>
                    <label>Corrective Action</label>
                    <textarea name="correctiveaction" id="registered-correctiveaction-textarea"></textarea>
                    <label>Point of Contact Username</label>
                    <input type="text" name="pocusername" id="registered-pocusername-input" value=""/>
                    <fieldset>
                        <legend>Filterable</legend>
                        <label>True</label>
                        <input type="radio" name="filterable" value="true"/>
                        <label>False</label>
                        <input type="radio" name="filterable" value="false"/>
                        <label>Inherit</label>
                        <input type="radio" name="filterable" checked="checked" value=""/>
                    </fieldset>
                    <fieldset>
                        <legend>Latching</legend>
                        <label>True</label>
                        <input type="radio" name="latching" value="true"/>
                        <label>False</label>
                        <input type="radio" name="latching" value="false"/>
                        <label>Inherit</label>
                        <input type="radio" name="latching" checked="checked" value=""/>
                    </fieldset><label>On Delay Seconds</label>
                    <input type="number" name="ondelayseconds" id="registered-ondelay-input" value=""/>
                    <label>Off Delay Seconds</label>
                    <input type="number" name="offdelayseconds" id="registered-offdelay-input" value=""/>
                    <label>Masked By</label>
                    <input type="text" name="maskedby" id="registered-maskedby-input" value=""/>
                    <label>Screen Path</label>
                    <input type="text" name="screenpath" id="registered-screenpath-input" value=""/>
                </fieldset>
                <input id="registered-submit" type="submit" tabindex="-1" style="position:absolute; top:-1000px"/>
            </form>
        </div>
    </div>
    <div id="effective-panel">
        <h2>Effective Registrations</h2>
        <div class="toolbar">
            <form id="effective-search-form" class="search-form" onsubmit="return false;">
                <input type="text" value="" id="effective-search-input" class="search-input" placeholder="field=value,field=value"/>
                <button type="button" id="search-effective-button">Search</button>
            </form> |
            <span>Records: </span><span class="record-count"></span>
        </div>
        <div class="table-wrap">
            <div id="effective-table"></div>
        </div>
    </div>
</div>
<script type="module" src="${pageContext.request.contextPath}/resources/js/page-1.11.6.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/dexie-3.0.3.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/tabulator-4.9.3/js/tabulator.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/jquery-ui-1.12.1.smoothness/external/jquery/jquery.js"></script>
<script src="${pageContext.request.contextPath}/resources/jquery-ui-1.12.1.smoothness/jquery-ui.min.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/main.js"></script>
</body>
</html>