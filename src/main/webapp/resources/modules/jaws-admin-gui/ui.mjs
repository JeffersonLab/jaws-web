import remote from './remote.mjs';
import db from './db.mjs';
import PanelUI from './panel-ui.mjs';
import page from '../page-1.11.6/page.min.mjs';

const meta = document.querySelector('meta');
const contextPath = meta && meta.dataset.contextPath || '';

class UserInterface {
    constructor() {
        let me = this;

        this.registrations = new PanelUI("#registrations", "Registration", "Registrations", db.registrations, '/registrations');
        this.classes = new PanelUI("#classes", "Class", "Classes", db.classes, '/classes');
        this.instances = new PanelUI("#instances", "Instance", "Instances", db.instances, '/instances');
        this.locations = new PanelUI("#locations", "Location", "Locations", db.locations, '/locations');
        this.categories = new PanelUI("#categories", "Category", "Categories", db.categories, '/categories');

        this.tabs = {
            init: function() {
                page('/registrations');
            },
            registration: async function(ctx, next) {
                $("#tabs").tabs({ active: 0 });

                await me.registrations.showViewDialog(ctx.params.name);
            },
            class: async function(ctx, next) {
                $("#tabs").tabs({ active: 1 });

                await me.classes.showViewDialog(ctx.params.name);
            },
            instance: async function(ctx, next) {
                $("#tabs").tabs({ active: 2 });

                await me.instances.showViewDialog(ctx.params.name);
            },
            location: async function(ctx, next) {
                $("#tabs").tabs({ active: 3 });

                await me.locations.showViewDialog(ctx.params.name);
            },
            category: async function(ctx, next) {
                $("#tabs").tabs({ active: 4 });

                await me.categories.showViewDialog(ctx.params.name);
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

/*
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
        });*/
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

            //ui.initPriorities();
            //ui.initLocations();
            //ui.initCategories();
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

        $(document).on("click", "#new-class-button", function () {

            document.getElementById("class-form").reset();

            $("#class-dialog").dialog("option", "title", "New Class")
            $("#class-dialog").dialog("open");
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
    }
}

const ui = new UserInterface();

export default ui;