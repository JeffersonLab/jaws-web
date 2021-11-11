import remote from './remote.js';

class UserInterface {
    constructor() {
        this.self = this;
    }

    registrationSearch() {
        registeredtable.clearFilter();

        let filterText = $("#registration-search-input").val();

        let filterArray = filterText.split(",");

        for (let filter of filterArray) {
            let keyValue = filter.split("=");

            registeredtable.addFilter(keyValue[0], "=", keyValue[1]);
        }

        let count = registeredtable.getDataCount("active");
        $("#registered-record-count").text(count.toLocaleString());
    }

    classSearch() {
        classestable.clearFilter();

        let filterText = $("#class-search-input").val();

        let filterArray = filterText.split(",");

        for (let filter of filterArray) {
            let keyValue = filter.split("=");

            classestable.addFilter(keyValue[0], "=", keyValue[1]);
        }

        let count = classestable.getDataCount("active");
        $("#class-record-count").text(count.toLocaleString());
    }

    effectiveSearch() {
        effectivetable.clearFilter();

        let filterText = $("#effective-search-input").val();

        let filterArray = filterText.split(",");

        for (let filter of filterArray) {
            let keyValue = filter.split("=");

            effectivetable.addFilter(keyValue[0], "=", keyValue[1]);
        }

        let count = effectivetable.getDataCount("active");
        $("#effective-record-count").text(count.toLocaleString());
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

                        let other = prioritySelect.cloneNode(true);
                        other.id = 'registered-priority-select';

                        let emptyOption = document.createElement('option');
                        emptyOption.selected = "selected";
                        other.insertBefore(emptyOption, other.options[0]);

                        document.getElementById("registered-priority-select-span").appendChild(other);
                    });
                }
            )
            .catch(function (err) {
                console.error('Fetch Error -', err);
            });
    }

    initLocations() {
        let locationSelect = document.getElementById('location-select');
        remote.getLocations()
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
                            locationSelect.add(option);
                        }

                        let other = locationSelect.cloneNode(true);
                        other.id = 'registered-location-select';

                        let emptyOption = document.createElement('option');
                        emptyOption.selected = "selected";
                        other.insertBefore(emptyOption, other.options[0]);

                        document.getElementById("registered-location-select-span").appendChild(other);
                    });
                }
            )
            .catch(function (err) {
                console.error('Fetch Error -', err);
            });
    }

    initCategories() {
        let categorySelect = document.getElementById('category-select');
        remote.getCategories()
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
                            categorySelect.add(option);
                        }

                        let other = categorySelect.cloneNode(true);
                        other.id = 'registered-category-select';

                        let emptyOption = document.createElement('option');
                        emptyOption.selected = "selected";
                        other.insertBefore(emptyOption, other.options[0]);

                        document.getElementById("registered-category-select-span").appendChild(other);
                    });
                }
            )
            .catch(function (err) {
                console.error('Fetch Error -', err);
            });
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

            //self.initPriorities();
            //self.initLocations();
            //self.initCategories();
        });

        $(document).on("click", "#new-registration-button", function () {

            document.getElementById("registered-form").reset();

            $("#registration-dialog").dialog("option", "title", "New Registration")
            $("#registration-dialog").dialog("open");
        });

        $(document).on("click", "#edit-registration-button", function () {
            let selectedData = registeredtable.getSelectedData(),
                data = selectedData[0];

            $("#alarm-name-input").val(data.name);
            $("#registered-class-input").val(data.class);
            $("#registered-priority-select").val(data.priority);
            $("#registered-location-select").val(data.location);
            $("#registered-category-select").val(data.category);
            $("#registered-rationale-textarea").val(data.rationale);
            $("#registered-correctiveaction-textarea").val(data.correctiveaction);
            $("#registered-pocusername-input").val(data.pointofcontactusername);
            $("#registered-form [name=filterable]").val([data.filterable]);
            $("#registered-form [name=latching]").val([data.latching]);
            $("#registered-ondelay-input").val(data.ondelayseconds);
            $("#registered-offdelay-input").val(data.offdelayseconds);
            $("#registered-maskedby-input").val(data.maskedby);
            $("#registered-screenpath-input").val(data.screenpath);
            $("#epicspv-input").val(data.epicspv);

            $("#registration-dialog").dialog("option", "title", "Edit Registration")
            $("#registration-dialog").dialog("open");
        });

        $(document).on("click", "#delete-registration-button", function () {
            let selectedData = registeredtable.getSelectedData();

            let params = "name=" + selectedData[0].name;

            let promise = fetch("proxy/rest/registered", {
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
                self.registeredRowDeselected();
            })
                .catch(error => {
                    console.error('Delete failed: ', error)
                });
        });

        $(document).on("submit", "#registered-search-form", function (event) {
            event.preventDefault();
            self.registrationSearch();
        });

        $(document).on("click", "#search-registration-button", function () {
            self.registrationSearch();
        });

        $(document).on("click", "#new-class-button", function () {

            document.getElementById("class-form").reset();

            $("#class-dialog").dialog("option", "title", "New Class")
            $("#class-dialog").dialog("open");
        });

        $(document).on("click", "#edit-class-button", function () {
            let selectedData = classestable.getSelectedData(),
                data = selectedData[0];

            $("#class-name-input").val(data.name);
            $("#priority-select").val(data.priority);
            $("#location-select").val(data.location);
            $("#category-select").val(data.category);
            $("#class-rationale-textarea").val(data.rationale);
            $("#class-correctiveaction-textarea").val(data.correctiveaction);
            $("#class-pocusername-input").val(data.pointofcontactusername);
            $("#class-form [name=filterable]").val([data.filterable]);
            $("#class-form [name=latching]").val([data.latching]);
            $("#class-ondelay-input").val(data.ondelayseconds);
            $("#class-offdelay-input").val(data.offdelayseconds);
            $("#class-maskedby-input").val(data.maskedby);
            $("#class-screenpath-input").val(data.screenpath);

            $("#class-dialog").dialog("option", "title", "Edit Class")
            $("#class-dialog").dialog("open");
        });

        $(document).on("click", "#delete-class-button", function () {
            let selectedData = classestable.getSelectedData();

            let params = "name=" + selectedData[0].name;

            let promise = fetch("proxy/rest/class", {
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
                self.classRowDeselected();
            })
                .catch(error => {
                    console.error('Delete failed: ', error)
                });
        });

        $(document).on("submit", "#class-search-form", function (event) {
            event.preventDefault();
            self.classSearch();
        });

        $(document).on("click", "#search-class-button", function () {
            self.classSearch();
        });


        $(document).on("click", "#search-effective-button", function () {
            self.effectiveSearch();
        });

        $(document).on("submit", "#effective-search-form", function (event) {
            event.preventDefault();
            self.effectiveSearch();
        });
    }
}

const ui = new UserInterface();

export default ui;