import remote from './remote.js';

class UserInterface {
    constructor() {
        this.self = this;
    }

    start() {

        $(function () {
            $(".toolbar button").button();

            $("#tabs").tabs({
                activate: function (event, ui) {
                    ui.newPanel.css("display", "flex");
                }
            }).show();

            let registrationDialog = $("#registration-dialog").dialog({
                autoOpen: false,
                height: 400,
                width: 400,
                modal: true,
                buttons: {
                    Set: self.setRegistration,
                    Cancel: function () {
                        registrationDialog.dialog("close");
                    }
                }
            });

            registrationDialog.find("form").on("submit", function (event) {
                event.preventDefault();
                self.setRegistration();
            });

            let classDialog = $("#class-dialog").dialog({
                autoOpen: false,
                height: 400,
                width: 400,
                modal: true,
                buttons: {
                    Set: self.setClass,
                    Cancel: function () {
                        classDialog.dialog("close");
                    }
                }
            });

            classDialog.find("form").on("submit", function (event) {
                event.preventDefault();
                self.setClass();
            });
        });
    }

    setRegistration() {
        let form = document.getElementById("registered-form"),
            formData = new FormData(form);

        let promise = remote.setRegistration(formData);

        promise.then(response => {
            if (response.ok) {
                self.registeredRowDeselected();
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
    }

    setClass() {
        let form = document.getElementById("class-form"),
            formData = new FormData(form);

        let promise = remote.setClass(formData);

        promise.then(response => {
            if (response.ok) {
                self.classRowDeselected();
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

    classRowSelected(row) {
        $("#class-toolbar .no-selection-row-action").button("option", "disabled", true);
        $("#class-toolbar .selected-row-action").button("option", "disabled", false);
    }

    registeredRowDeselected(row) {
        $("#registered-toolbar .no-selection-row-action").button("option", "disabled", false);
        $("#registered-toolbar .selected-row-action").button("option", "disabled", true);
    }

    classRowDeselected(row) {
        $("#class-toolbar .no-selection-row-action").button("option", "disabled", false);
        $("#class-toolbar .selected-row-action").button("option", "disabled", true);
    }
}

const ui = new UserInterface();

export default ui;