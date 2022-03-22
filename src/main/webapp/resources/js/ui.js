import remote from './remote.js';
import db from './db.js';
import TableUI from './table-ui.js';
import page from '../libs/page-1.11.6/page.js';
import Editor from '../libs/toastui-3.1.3/toastui-all.min.js';

const meta = document.querySelector('meta');
const contextPath = meta && meta.dataset.contextPath || '';

class UserInterface {
    constructor() {
        this.tabs = {
            init: function() {
                page('/registrations');
            },
            registration: async function(ctx, next) {
                $("#tabs").tabs({ active: 0 });

                let data = await db.effectives.get(ctx.params.name);

                $("#view-effective-name").text(data.name);
                $("#view-effective-class").text(data.class || 'None');
                $("#view-effective-epicspv").text(data.epicspv || 'None');
                $("#view-effective-priority").text(data.priority || 'None');
                $("#view-effective-location").text(data.location || 'None');
                $("#view-effective-category").text(data.category || 'None');
                $("#view-effective-contact").text(data.pointofcontactusername || 'None');
                $("#view-effective-filterable").text(data.filterable || 'None');
                $("#view-effective-latching").text(data.latching || 'None');
                $("#view-effective-on-delay").text(data.ondelayseconds || 'None');
                $("#view-effective-off-delay").text(data.offdelayseconds || 'None');
                $("#view-effective-masked-by").text(data.maskedby || 'None');
                $("#view-effective-screen-command").text(data.screencommand || 'None');

                ui.effectiverationaleviewer.setMarkdown(data.rationale || 'None');
                ui.effectivecorrectiveactionviewer.setMarkdown(data.correctiveaction || 'None');

                $("#view-effective-dialog").dialog("open");
            },
            class: async function(ctx, next) {
                $("#tabs").tabs({ active: 1 });

                let data = await db.classes.get(ctx.params.name);

                $("#view-class-name").text(data.name);
                $("#view-class-priority").text(data.priority || 'None');
                $("#view-class-category").text(data.category || 'None');
                $("#view-class-contact").text(data.pointofcontactusername || 'None');
                $("#view-class-filterable").text(data.filterable || 'None');
                $("#view-class-latching").text(data.latching || 'None');
                $("#view-class-on-delay").text(data.ondelayseconds || 'None');
                $("#view-class-off-delay").text(data.offdelayseconds || 'None');

                ui.classrationaleviewer.setMarkdown(data.rationale || 'None');
                ui.classcorrectiveactionviewer.setMarkdown(data.correctiveaction || 'None');

                $("#view-class-dialog").dialog("open");
            },
            instance: async function(ctx, next) {
                $("#tabs").tabs({ active: 2 });

                let data = await db.instances.get(ctx.params.name);

                $("#view-registration-name").text(data.name);
                $("#view-registration-class").text(data.class || 'None');
                $("#view-registration-epicspv").text(data.epicspv || 'None');
                $("#view-registration-location").text(data.location || 'None');
                $("#view-registration-masked-by").text(data.maskedby || 'None');
                $("#view-registration-screen-command").text(data.screencommand || 'None');

                $("#view-instance-dialog").dialog("open");
            },
            location: async function(ctx, next) {
                $("#tabs").tabs({ active: 3 });

                let data = await db.locations.get(ctx.params.name);

                $("#view-location-name").text(data.name);
                $("#view-location-parent").text(data.parent || 'None');

                $("#view-location-dialog").dialog("open");
            },
            category: async function(ctx, next) {
                $("#tabs").tabs({ active: 4 });

                let data = await db.catgories.get(ctx.params.name);

                $("#view-category-name").text(data.name);

                $("#view-category-dialog").dialog("open");
            },
            registrations: function() {
                $("#tabs").tabs({ active: 0 });
            },
            classes: function() {
                $("#tabs").tabs({ active: 1 });
            },
            instances: function() {
                $("#tabs").tabs({ active: 2 });
            },
            locations: function() {
                $("#tabs").tabs({ active: 3 });
            },
            categories: function() {
                $("#tabs").tabs({ active: 4 });
            }
        };

        page.base(contextPath + '/view');

        page('/', this.tabs.init);
        page('/registrations', this.tabs.registrations);
        page('/classes', this.tabs.classes);
        page('/instances', this.tabs.instances);
        page('/locations', this.tabs.locations);
        page('/categories', this.tabs.categories);
        page('/registrations/:name', this.tabs.registration);
        page('/classes/:name', this.tabs.class);
        page('/instances/:name', this.tabs.instance);
        page('/categories/:name', this.tabs.category);
        page('/locations/:name', this.tabs.location);
        page();

        let panelElement = "#categories-panel",
            tableElement = "#categories-table",
            options = {
                data: [],
                headerSort: false,
                reactiveData: false,
                height: "100%", // enables the Virtual DOM
                layout: "fitColumns",
                responsiveLayout: "collapse",
                index: "name",
                selectable: 1,
                initialSort: [
                    {column: "name", dir: "asc"}
                ],
                columns: [
                    {title: "name", field: "name"}
                ]
            };

        this.categories = new TableUI(panelElement, tableElement, options);

        panelElement = "#classes-panel",
        tableElement = "#classes-table",
        options = {
                data: [],
                headerSort: false,
                reactiveData: false,
                height: "100%", // enables the Virtual DOM
                layout: "fitColumns",
                responsiveLayout: "collapse",
                index: "name",
                selectable: 1,
                initialSort: [
                    {column: "name", dir: "asc"}
                ],
                columns: [
                    {title: "name", field: "name"},
                    {title: "category", field: "category"},
                    {title: "priority", field: "priority"},
                    {title: "contact", field: "pointofcontactusername"}
                ]
            };

       this.classes = new TableUI(panelElement, tableElement, options);

        panelElement = "#instances-panel",
            tableElement = "#instances-table",
            options = {
            data: [],
            headerSort: false,
            reactiveData: false,
            height: "100%", // enables the Virtual DOM
            layout: "fitColumns",
            responsiveLayout: "collapse",
            index: "name",
            selectable: 1,
            initialSort: [
                {column: "name", dir: "asc"}
            ],
            columns: [
                {title: "name", field: "name"},
                {title: "class", field: "class"},
                {title: "location", field: "location"},
                {title: "epicspv", field: "epicspv"}
            ]
        };

        this.instances = new TableUI(panelElement, tableElement, options);

        panelElement = "#locations-panel",
            tableElement = "#locations-table",
            options = {
                data: [],
                headerSort: false,
                reactiveData: false,
                height: "100%", // enables the Virtual DOM
                layout: "fitColumns",
                responsiveLayout: "collapse",
                index: "name",
                selectable: 1,
                initialSort: [
                    {column: "name", dir: "asc"}
                ],
                columns: [
                    {title: "name", field: "name"},
                    {title: "parent", field: "parent"}
                ]
            };

        this.locations = new TableUI(panelElement, tableElement, options);

        panelElement = "#effective-panel",
            tableElement = "#effective-table",
            options = {
                data: [],
                headerSort: false,
                reactiveData: false,
                height: "100%", // enables the Virtual DOM
                layout: "fitColumns",
                responsiveLayout: "collapse",
                index: "name",
                selectable: 1,
                initialSort: [
                    {column: "name", dir: "asc"}
                ],
                columns: [
                    {title: "name", field: "name"},
                    {title: "category", field: "category"},
                    {title: "class", field: "class"},
                    {title: "location", field: "location"},
                    {title: "priority", field: "priority"},
                    {title: "contact", field: "pointofcontactusername"}
                ]
            };

        this.effectives = new TableUI(panelElement, tableElement, options);

        // bind this on properties
        const props = Object.getOwnPropertyNames(Object.getPrototypeOf(this));

        props
            .filter(prop => (prop !== 'constructor'))
            .forEach((prop) => { this[prop] = this[prop].bind(this);});


        let correctiveactionOptions = {
            toolbarItems: [
                ['heading', 'bold', 'italic'],
                ['ul', 'ol', 'task', 'indent', 'outdent', 'link']
            ],
            hideModeSwitch: true,
            usageStatistics: false,
            el: document.querySelector('#registered-correctiveaction-editor'),
            height: '300px',
            initialEditType: 'markdown',
            previewStyle: 'tab',
            autofocus: false
        };


        let classcorrectiveactionOptions = JSON.parse(JSON.stringify(correctiveactionOptions));
        classcorrectiveactionOptions.el = document.querySelector("#class-correctiveaction-editor");

        let classrationaleOptions = JSON.parse(JSON.stringify(correctiveactionOptions));
        classrationaleOptions.el = document.querySelector("#class-rationale-editor");

        this.classcorrectiveactioneditor = new Editor(classcorrectiveactionOptions);
        this.classrationaleeditor = new Editor(classrationaleOptions);

        this.effectiverationaleviewer = Editor.factory({
            viewer: true,
            usageStatistics: false,
            autofocus: false,
            el: document.querySelector('#view-effective-rationale')
        });

        this.effectivecorrectiveactionviewer = Editor.factory({
            viewer: true,
            usageStatistics: false,
            autofocus: false,
            el: document.querySelector('#view-effective-action')
        });

        this.classrationaleviewer = Editor.factory({
            viewer: true,
            usageStatistics: false,
            autofocus: false,
            el: document.querySelector('#view-class-rationale')
        });

        this.classcorrectiveactionviewer = Editor.factory({
            viewer: true,
            usageStatistics: false,
            autofocus: false,
            el: document.querySelector('#view-class-action')
        });
    }

    search(filterText, uitab, dbtab) {
        let filterArray = filterText.split(",");

        uitab.filters = [];

        for (let filter of filterArray) {
            if(filter.indexOf('=') > -1) { // exact match equals search
                let keyValue = filter.split("=");
                uitab.filters.push(record => record[keyValue[0]] === keyValue[1]);
            } else if(filter.indexOf('~') > -1) { // case-insensitive contains search
                let keyValue = filter.split("~");
                uitab.filters.push(record => {
                    let haystack = record[keyValue[0]] || "";
                    let needle = keyValue[1] || "";
                    return haystack.toLowerCase().includes(needle.toLowerCase());
                });
            }
        }

        uitab.refresh(dbtab);
    }

    classSearch() {
        let filterText = $("#class-search-input").val();

        ui.search(filterText, ui.classes, db.classes);
    }

    instanceSearch() {
        let filterText = $("#registration-search-input").val();

        ui.search(filterText, ui.instances, db.instances);
    }

    effectiveSearch() {
        let filterText = $("#effective-search-input").val();

        ui.search(filterText, ui.effectives, db.effectives);
    }

    setRegistrationBatch() {
        let property = $("#batch-update-select").val();
        let value = $("#batch-update-input").val();

        if(value === '') {
            value = null;
        }

        let promises = [];

        for(const r of ui.instances.data) {
            let record = JSON.parse(JSON.stringify(r));
            record[property] = value;

            ui.fillRegistrationForm(record);
            promises.push(ui.setRegistration());
        }

        Promise.all(promises).then(() => {
            $("#batch-registration-dialog").dialog("close");
        });
    }

    fillRegistrationForm(data) {
        $("#alarm-name-input").val(data.name);
        $("#registered-class-input").val(data.class);
        $("#registered-priority-select").val(data.priority);
        $("#registered-location-select").val(data.location);
        $("#registered-category-select").val(data.category);
        $("#registered-rationale-textarea").val(data.rationale);
        $("#registered-correctiveaction-textarea").val(data.correctiveaction);
        $("#registered-pocusername-input").val(data.pointofcontactusername);
        $("#registered-form [name=filterable]").val(data.filterable);
        $("#registered-form [name=latching]").val(data.latching);
        $("#registered-ondelay-input").val(data.ondelayseconds);
        $("#registered-offdelay-input").val(data.offdelayseconds);
        $("#registered-maskedby-input").val(data.maskedby);
        $("#registered-screencommand-input").val(data.screencommand);
        $("#epicspv-input").val(data.epicspv);

        this.instancerationaleeditor.setMarkdown(data.rationale || '');
        this.instancecorrectiveactioneditor.setMarkdown(data.correctiveaction || '');
    }

    setRegistration() {
        $("#registered-rationale-textarea").val(this.instancerationaleeditor.getMarkdown());
        $("#registered-correctiveaction-textarea").val(this.instancecorrectiveactioneditor.getMarkdown());

        let form = document.getElementById("registered-form"),
            formData = new FormData(form);

        let promise = remote.setInstance(formData);

        promise.then(response => {
            if (response.ok) {
                ui.registeredRowDeselected();
                $("#registration-dialog").dialog("close");
            } else {
                response.json().then(function (data) {
                    if ('parameterViolations' in data && data.parameterViolations.length > 0) {
                        let violations = [];
                        for (let v of data.parameterViolations) {
                            violations.push(v.message);
                        }
                        alert("Unable to edit: " + violations);
                    } else {
                        alert("Network response not ok: " + data);
                    }
                });
            }
        })
            .catch(error => {
                alert("Unable to edit: " + error);
            });
        return promise;
    }

    setClass() {
        $("#class-rationale-textarea").val(ui.classrationaleeditor.getMarkdown());
        $("#class-correctiveaction-textarea").val(ui.classcorrectiveactioneditor.getMarkdown());

        let form = document.getElementById("class-form"),
            formData = new FormData(form);

        let promise = remote.setClass(formData);

        promise.then(response => {
            if (response.ok) {
                ui.classes.rowDeselected();
                $("#class-dialog").dialog("close");
            } else {
                response.json().then(function (data) {
                    if ('parameterViolations' in data && data.parameterViolations.length > 0) {
                        let violations = [];
                        for (let v of data.parameterViolations) {
                            violations.push(v.message);
                        }
                        alert("Unable to edit: " + violations);
                    } else {
                        alert("Network response not ok: " + data);
                    }
                });
            }
        })
            .catch(error => {
                alert("Unable to edit: " + error);
            });
    }

    registeredRowSelected(row) {
        $("#registered-toolbar .no-selection-row-action").button("option", "disabled", true);
        $("#registered-toolbar .selected-row-action").button("option", "disabled", false);
    }

    registeredRowDeselected(row) {
        $("#registered-toolbar .no-selection-row-action").button("option", "disabled", false);
        $("#registered-toolbar .selected-row-action").button("option", "disabled", true);
    }

    initPriorities() {
        let prioritySelect = document.getElementById('priority-select');
        remote.getPriorities()
            .then(
                function (response) {
                    if (response.status !== 200) {
                        console.warn('Error. Status Code: ' +
                            response.status);
                        return;
                    }

                    response.json().then(function (data) {
                        let option;

                        for (let i = 0; i < data.length; i++) {
                            option = document.createElement('option');
                            option.text = data[i];
                            prioritySelect.add(option);
                        }
                    });
                }
            )
            .catch(function (err) {
                console.error('Fetch Error -', err);
            });
    }

    initLocations() {
        let locationSelect = document.getElementById('location-select');
    }

    initCategories() {
        let categorySelect = document.getElementById('category-select');
    }

    start() {

        $(function () {
            $(".toolbar button").button();

            $("#tabs").tabs({
                activate: function (event, i) {
                    i.newPanel.css("display", "flex");

                    switch (i.newTab.context.innerText) {
                        case 'Registrations':
                            page('/registrations');
                            break;
                        case 'Classes':
                            page('/classes');
                            break;
                        case 'Instances':
                            page('/instances');
                            break;
                        case 'Locations':
                            page('/locations');
                            break;
                        case 'Categories':
                            page('/categories')
                            break;
                        default:
                            console.log('Unknown tab activation: ', event, i);
                    }
                }
            }).show();

            let registrationDialog = $("#registration-dialog").dialog({
                autoOpen: false,
                height: 550,
                width: 750,
                modal: true,
                buttons: {
                    Set: ui.setRegistration,
                    Cancel: function () {
                        registrationDialog.dialog("close");
                    }
                }
            });

            registrationDialog.find("form").on("submit", function (event) {
                event.preventDefault();
                ui.setRegistration();
            });

            let batchDialog = $("#batch-registration-dialog").dialog({
                autoOpen: false,
                height: 550,
                width: 750,
                modal: true,
                buttons: {
                    Set: ui.setRegistrationBatch,
                    Cancel: function () {
                        batchDialog.dialog("close");
                    }
                }
            });

            batchDialog.find("form").on("submit", function (event) {
                event.preventDefault();
                ui.setRegistrationBatch();
            });

            let viewClassDialog = $("#view-class-dialog").dialog({
                autoOpen: false,
                height: 550,
                width: 750,
                modal: true,
                buttons: {
                    OK: function () {
                        viewClassDialog.dialog("close");
                        page('/classes');
                    }
                }
            });

            let viewInstanceDialog = $("#view-instance-dialog").dialog({
                autoOpen: false,
                height: 550,
                width: 750,
                modal: true,
                buttons: {
                    OK: function () {
                        viewInstanceDialog.dialog("close");
                        page('/instances');
                    }
                }
            });

            let viewEffectiveDialog = $("#view-effective-dialog").dialog({
                autoOpen: false,
                height: 550,
                width: 750,
                modal: true,
                buttons: {
                    OK: function () {
                        viewEffectiveDialog.dialog("close");
                        page('/registrations');
                    }
                }
            });

            let classDialog = $("#class-dialog").dialog({
                autoOpen: false,
                height: 550,
                width: 750,
                modal: true,
                buttons: {
                    Set: ui.setClass,
                    Cancel: function () {
                        classDialog.dialog("close");
                    }
                }
            });

            classDialog.find("form").on("submit", function (event) {
                event.preventDefault();
                ui.setClass();
            });

            ui.initPriorities();
            ui.initLocations();
            ui.initCategories();
        });

        $(document).on("click", "#batch-edit-button", function(){
            document.getElementById("batch-form").reset();

            $("#batch-registration-dialog").dialog("open");
        });

        $(document).on("click", "#new-registration-button", function () {

            document.getElementById("registered-form").reset();

            $("#registration-dialog").dialog("option", "title", "New Registration")
            $("#registration-dialog").dialog("open");
        });

        $(document).on("click", "#edit-registration-button", function () {
            let selectedData = ui.instances.tabulator.getSelectedData(),
                data = selectedData[0];

            ui.fillRegistrationForm(data);

            $("#registration-dialog").dialog("option", "title", "Edit Registration")
            $("#registration-dialog").dialog("open");
        });

        $(document).on("click", "#delete-registration-button", function () {
            let selectedData = ui.instances.tabulator.getSelectedData();

            let params = "name=" + selectedData[0].name;

            let promise = fetch(contextPath + "/proxy/rest/instance", {
                method: "DELETE",
                body: new URLSearchParams(params),
                headers: {
                    "Content-type": 'application/x-www-form-urlencoded;charset=UTF-8'
                }
            });

            promise.then(response => {
                if (!response.ok) {
                    throw new Error("Network response not ok");
                }
                ui.registeredRowDeselected();
            })
                .catch(error => {
                    console.error('Delete failed: ', error)
                });
        });

        $(document).on("submit", "#registered-search-form", function (event) {
            event.preventDefault();
            ui.instanceSearch();
        });

        $(document).on("click", "#search-registration-button", function () {
            ui.instanceSearch();
        });

        $(document).on("click", "#new-class-button", function () {

            document.getElementById("class-form").reset();

            $("#class-dialog").dialog("option", "title", "New Class")
            $("#class-dialog").dialog("open");
        });

        $(document).on("click", "#view-class-button", function() {
            let selectedData = ui.classes.tabulator.getSelectedData(),
                data = selectedData[0];

            page('/classes/' + data.name);
        });

        $(document).on("click", "#view-registration-button", function() {
            let selectedData = ui.instances.tabulator.getSelectedData(),
                data = selectedData[0];

            page('/instances/' + data.name);
        });

        $(document).on("click", "#view-effective-button", function() {
            let selectedData = ui.effectives.tabulator.getSelectedData(),
                data = selectedData[0];

            page('/registrations/' + data.name);
        });

        $(document).on("click", "#edit-class-button", function () {
            let selectedData = ui.classes.tabulator.getSelectedData(),
                data = selectedData[0];

            $("#class-name-input").val(data.name);
            $("#priority-select").val(data.priority);
            $("#category-select").val(data.category);
            $("#class-rationale-textarea").val(data.rationale);
            $("#class-correctiveaction-textarea").val(data.correctiveaction);
            $("#class-pocusername-input").val(data.pointofcontactusername);
            $("#class-form [name=filterable]").val([data.filterable]);
            $("#class-form [name=latching]").val([data.latching]);
            $("#class-ondelay-input").val(data.ondelayseconds);
            $("#class-offdelay-input").val(data.offdelayseconds);

            ui.classrationaleeditor.setMarkdown(data.rationale || '');
            ui.classcorrectiveactioneditor.setMarkdown(data.correctiveaction || '');

            $("#class-dialog").dialog("option", "title", "Edit Class")
            $("#class-dialog").dialog("open");
        });

        $(document).on("click", "#delete-class-button", function () {
            let selectedData = ui.classes.tabulator.getSelectedData();

            let params = "name=" + selectedData[0].name;

            let promise = fetch(contextPath + "/proxy/rest/class", {
                method: "DELETE",
                body: new URLSearchParams(params),
                headers: {
                    "Content-type": 'application/x-www-form-urlencoded;charset=UTF-8'
                }
            });

            promise.then(response => {
                if (!response.ok) {
                    throw new Error("Network response not ok");
                }
                ui.classes.rowDeselected();
            })
                .catch(error => {
                    console.error('Delete failed: ', error)
                });
        });

        $(document).on("click", "#next-class-button", function(){
            ui.classes.next(db.classes);
        });

        $(document).on("click", "#next-registration-button", function(){
            ui.instances.next(db.instances);
        });

        $(document).on("click", "#next-effective-button", function() {
            ui.effectives.next(db.effectives);
        });

        $(document).on("click", "#previous-class-button", function() {
            ui.classes.previous(db.classes);
        });

        $(document).on("click", "#previous-registration-button", function() {
            ui.instances.previous(db.instances);
        });

        $(document).on("click", "#previous-effective-button", function() {
            ui.effectives.previous(db.effectives);
        });

        $(document).on("submit", "#class-search-form", function (event) {
            event.preventDefault();
            ui.classSearch();
        });

        $(document).on("click", "#search-class-button", function () {
            ui.classSearch();
        });


        $(document).on("click", "#search-effective-button", function () {
            ui.effectiveSearch();
        });

        $(document).on("submit", "#effective-search-form", function (event) {
            event.preventDefault();
            ui.effectiveSearch();
        });
    }
}

const ui = new UserInterface();

export default ui;