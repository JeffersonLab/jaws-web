<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" data-context-path="${pageContext.request.contextPath}"/>
    <title>JAWS Admin GUI</title>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/site.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/libs/jquery-ui-1.12.1.smoothness/jquery-ui.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/libs/tabulator-4.9.3/css/tabulator.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/libs/toast-ui-3.1.3/toastui-editor.css"/>
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
        <li><a href="#locations-panel">Locations</a></li>
        <li><a href="#categories-panel">Categories</a></li>
    </ul>
    <div id="categories-panel">
        <h2>Alarm Registration Categories</h2>
        <div class="toolbar">
            <form id="category-search-form" class="search-form" onsubmit="return false;">
                <input type="text" value="" id="category-search-input" class="search-input" placeholder="field=value,field~value"/>
                <button type="button" id="search-category-button">Search</button> |
            </form>
            <button type="button" id="new-category-button">New</button> |
            <button type="button" id="view-category-button" class="selected-row-action" disabled="disabled">View</button>
            <button type="button" id="edit-category-button" class="selected-row-action" disabled="disabled">Edit</button>
            <button type="button" id="delete-category-button" class="selected-row-action" disabled="disabled">Delete</button> |
            <button type="button" class="prev-button" id="previous-category-button" disabled="disabled">Previous</button>
            <button type="button" class="next-button" id="next-category-button" disabled="disabled">Next</button> |
            <span>Records: </span><span class="record-count"></span>
        </div>
        <div class="table-wrap">
            <div id="categories-table"></div>
        </div>
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
    </div>
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
    </div>
    <div id="locations-panel">
        <h2>Alarm Registration Locations</h2>
        <div class="toolbar">
            <form id="location-search-form" class="search-form" onsubmit="return false;">
                <input type="text" value="" id="location-search-input" class="search-input" placeholder="field=value,field~value"/>
                <button type="button" id="search-location-button">Search</button> |
            </form>
            <button type="button" id="new-location-button">New</button> |
            <button type="button" id="view-location-button" class="selected-row-action" disabled="disabled">View</button>
            <button type="button" id="edit-location-button" class="selected-row-action" disabled="disabled">Edit</button>
            <button type="button" id="delete-location-button" class="selected-row-action" disabled="disabled">Delete</button> |
            <button type="button" class="prev-button" id="previous-location-button" disabled="disabled">Previous</button>
            <button type="button" class="next-button" id="next-location-button" disabled="disabled">Next</button> |
            <span>Records: </span><span class="record-count"></span>
        </div>
        <div class="table-wrap">
            <div id="locations-table"></div>
        </div>
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
    </div>
</div>
<div id="version-div">v${initParam['releaseNumber']}</div>
<script type="module" src="${pageContext.request.contextPath}/resources/libs/toast-ui-3.1.3/toastui-all.min.mjs"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/libs/page-1.11.6/page.mjs"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/libs/dexie-3.2.1/dexie.min.mjs"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/libs/tabulator-4.9.3/js/tabulator.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/libs/jquery-ui-1.12.1.smoothness/external/jquery/jquery.js"></script>
<script src="${pageContext.request.contextPath}/resources/libs/jquery-ui-1.12.1.smoothness/jquery-ui.min.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/main.js"></script>
</body>
</html>