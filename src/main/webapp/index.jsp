<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" data-context-path="${pageContext.request.contextPath}"/>
    <title>JAWS Admin GUI</title>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/site.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/jquery-ui-1.12.1.smoothness/jquery-ui.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/tabulator-4.9.3/css/tabulator.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/toastui-3.1.1.min.css"/>
</head>
<body>
<header>
    <img src="${pageContext.request.contextPath}/resources/img/logo128x128.png"/>
    <h1>JAWS Admin GUI</h1>
</header>
<div id="tabs">
    <ul>
        <li><a href="#effective-panel">Registrations</a></li>
        <li><a href="#classes-panel">Classes</a></li>
        <li><a href="#instances-panel">Instances</a></li>
    </ul>
    <div id="classes-panel">
        <h2>Alarm Registration Classes</h2>
        <div class="toolbar">
            <form id="class-search-form" class="search-form" onsubmit="return false;">
                <input type="text" value="" id="class-search-input" class="search-input" placeholder="field=value,field~value"/>
                <button type="button" id="search-class-button">Search</button> |
            </form>
            <button type="button" id="new-class-button">New</button> |
            <button type="button" id="view-class-button" class="selected-row-action" disabled="disabled">View</button>
            <button type="button" id="edit-class-button" class="selected-row-action" disabled="disabled">Edit</button>
            <button type="button" id="delete-class-button" class="selected-row-action" disabled="disabled">Delete</button> |
            <button type="button" class="prev-button" id="previous-class-button" disabled="disabled">Previous</button>
            <button type="button" class="next-button" id="next-class-button" disabled="disabled">Next</button> |
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
    <div id="instances-panel">
        <h2>Alarm Registration Instances</h2>
        <div class="toolbar">
            <form id="registered-search-form" class="search-form" onsubmit="return false;">
                <input type="text" value="" id="registration-search-input" class="search-input" placeholder="field=value,field~value"/>
                <button type="button" id="search-registration-button">Search</button> |
            </form>
            <button type="button" id="new-registration-button">New</button>
            <button type="button" id="batch-edit-button">Edit All</button> |
            <button type="button" id="view-registration-button" class="selected-row-action" disabled="disabled">View</button>
            <button type="button" id="edit-registration-button" class="selected-row-action" disabled="disabled">Edit</button>
            <button type="button" id="delete-registration-button" class="selected-row-action" disabled="disabled">Delete</button> |
            <button type="button" class="prev-button" id="previous-registration-button" disabled="disabled">Previous</button>
            <button type="button" class="next-button" id="next-registration-button" disabled="disabled">Next</button> |
            <span>Records: </span><span class="record-count"></span>
        </div>
        <div class="table-wrap">
            <div id="instances-table"></div>
        </div>
        <div id="view-registration-dialog" class="dialog" title="Registration (Instance)">
            <dl>
                <dt>Name</dt>
                <dd id="view-registration-name"></dd>
                <dt>Class</dt>
                <dd id="view-registration-class"></dd>
                <dt>EPICS PV</dt>
                <dd id="view-registration-epicspv"></dd>
                <dt>Priority</dt>
                <dd id="view-registration-priority"></dd>
                <dt>Location</dt>
                <dd id="view-registration-location"></dd>
                <dt>Category</dt>
                <dd id="view-registration-category"></dd>
                <dt>Rationale</dt>
                <dd id="view-registration-rationale"></dd>
                <dt>Corrective Action</dt>
                <dd id="view-registration-action"></dd>
                <dt>Point of Contact Username</dt>
                <dd id="view-registration-contact"></dd>
                <dt>Filterable</dt>
                <dd id="view-registration-filterable"></dd>
                <dt>Latching</dt>
                <dd id="view-registration-latching"></dd>
                <dt>On-Delay Seconds</dt>
                <dd id="view-registration-on-delay"></dd>
                <dt>Off-Delay Seconds</dt>
                <dd id="view-registration-off-delay"></dd>
                <dt>Masked By</dt>
                <dd id="view-registration-masked-by"></dd>
                <dt>Screen Path</dt>
                <dd id="view-registration-screen-path"></dd>
            </dl>
        </div>
        <div id="batch-registration-dialog" class="dialog" title="Edit All On Current Page">
            <form id="batch-form" onsubmit="return false;">
                <p>Edit entire page of registration records</p>
                <label>Field to modify</label>
                <select id="batch-update-select">
                    <option value="class" selected="selected">Class</option>
                    <option value="category">Category</option>
                    <option value="priority">Priority</option>
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
                    <label>Rationale (Markdown format)</label>
                    <textarea name="rationale" id="registered-rationale-textarea" style="display: none;"></textarea>
                    <div id="registered-rationale-editor"></div>
                    <label>Corrective Action (Markdown format)</label>
                    <textarea name="correctiveaction" id="registered-correctiveaction-textarea" style="display: none;"></textarea>
                    <div id="registered-correctiveaction-editor"></div>
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
        <h2>Effective Alarm Registrations <span class="header-subtext">(Class + Instance)</span></h2>
        <div class="toolbar">
            <form id="effective-search-form" class="search-form" onsubmit="return false;">
                <input type="text" value="" id="effective-search-input" class="search-input" placeholder="field=value,field~value"/>
                <button type="button" id="search-effective-button">Search</button>
            </form> |
            <button type="button" id="view-effective-button" class="selected-row-action" disabled="disabled">View</button> |
            <button type="button" class="prev-button" id="previous-effective-button" disabled="disabled">Previous</button>
            <button type="button" class="next-button" id="next-effective-button" disabled="disabled">Next</button> |
            <span>Records: </span><span class="record-count"></span>
        </div>
        <div class="table-wrap">
            <div id="effective-table"></div>
        </div>
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
                <dt>Rationale</dt>
                <dd id="view-effective-rationale"></dd>
                <dt>Corrective Action</dt>
                <dd id="view-effective-action"></dd>
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
                <dt>Screen Path</dt>
                <dd id="view-effective-screen-path"></dd>
            </dl>
        </div>
    </div>
</div>
<script type="module" src="${pageContext.request.contextPath}/resources/js/toastui-3.1.1-all.min.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/page-1.11.6.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/dexie-3.0.3.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/tabulator-4.9.3/js/tabulator.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/jquery-ui-1.12.1.smoothness/external/jquery/jquery.js"></script>
<script src="${pageContext.request.contextPath}/resources/jquery-ui-1.12.1.smoothness/jquery-ui.min.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/main.js"></script>
</body>
</html>