let maxUpdateMillis = 500;
let maxRecordsPerUpdate = 100;

const classesEvents = new Map();
const registeredEvents = new Map();
const effectiveEvents = new Map();

var classestable = null;
var registeredtable = null;
var effectivetable = null;

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
        registeredRowDeselected();
    })
        .catch(error => {
            console.error('Delete failed: ', error)
        });
});

let registrationSearch = function () {
    registeredtable.clearFilter();

    let filterText = $("#registration-search-input").val();

    let filterArray = filterText.split(",");

    for (let filter of filterArray) {
        let keyValue = filter.split("=");

        registeredtable.addFilter(keyValue[0], "=", keyValue[1]);
    }

    let count = registeredtable.getDataCount("active");
    $("#registered-record-count").text(count.toLocaleString());
};

$(document).on("submit", "#registered-search-form", function (event) {
    event.preventDefault();
    registrationSearch();
});

$(document).on("click", "#search-registration-button", function () {
    registrationSearch();
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
        classRowDeselected();
    })
        .catch(error => {
            console.error('Delete failed: ', error)
        });
});


let classSearch = function () {
    classestable.clearFilter();

    let filterText = $("#class-search-input").val();

    let filterArray = filterText.split(",");

    for (let filter of filterArray) {
        let keyValue = filter.split("=");

        classestable.addFilter(keyValue[0], "=", keyValue[1]);
    }

    let count = classestable.getDataCount("active");
    $("#class-record-count").text(count.toLocaleString());
};

$(document).on("submit", "#class-search-form", function (event) {
    event.preventDefault();
    classSearch();
});

$(document).on("click", "#search-class-button", function () {
    classSearch();
});


$(document).on("click", "#search-effective-button", function () {
    effectiveSearch();
});

let effectiveSearch = function () {
    effectivetable.clearFilter();

    let filterText = $("#effective-search-input").val();

    let filterArray = filterText.split(",");

    for (let filter of filterArray) {
        let keyValue = filter.split("=");

        effectivetable.addFilter(keyValue[0], "=", keyValue[1]);
    }

    let count = effectivetable.getDataCount("active");
    $("#effective-record-count").text(count.toLocaleString());
};

$(document).on("submit", "#effective-search-form", function (event) {
    event.preventDefault();
    effectiveSearch();
});


let evtSource = new EventSource('proxy/sse');

let unwrapNullableUnionText = function (text) {
    if (text != null) {
        text = Object.values(text)[0];
    }
    return text;
};

evtSource.addEventListener("class-highwatermark", function (e) {
    const {updateOrAdd, remove} = processClassEvents(75000);

    classestable = new Tabulator("#classes-table", {
        data: updateOrAdd,
        reactiveData: false,
        height: "100%", // enables the Virtual DOM
        layout: "fitColumns",
        responsiveLayout: "collapse",
        index: "name",
        selectable: 1,
        initialSort: [
            {column: "name", dir: "asc"}
        ],
        rowSelected: classRowSelected,
        rowDeselected: classRowDeselected,
        columns: [
            {title: "name", field: "name"},
            {title: "priority", field: "priority"},
            {title: "location", field: "location"},
            {title: "category", field: "category"},
            {title: "rationale", field: "rationale"},
            {title: "correctiveaction", field: "correctiveaction"},
            {title: "pointofcontactusername", field: "pointofcontactusername"},
            {title: "filterable", field: "filterable"},
            {title: "latching", field: "latching"},
            {title: "ondelayseconds", field: "ondelayseconds"},
            {title: "offdelayseconds", field: "offdelayseconds"},
            {title: "maskedby", field: "maskedby"},
            {title: "screenpath", field: "screenpath"}
        ]
    });

    updateClassCountLabel();

    setTimeout(batchClassesTableUpdate, maxUpdateMillis);
});

evtSource.addEventListener("registration-highwatermark", function (e) {
    console.time('registered-init');
    const {updateOrAdd, remove} = processRegistrationEvents(75000);
    console.timeLog('registered-init');
    registeredtable = new Tabulator("#registered-table", {
        data: updateOrAdd,
        reactiveData: false,
        height: "100%", // enables the Virtual DOM
        layout: "fitColumns",
        responsiveLayout: "collapse",
        index: "name",
        selectable: 1,
        initialSort: [
            {column: "name", dir: "asc"}
        ],
        rowSelected: registeredRowSelected,
        rowDeselected: registeredRowDeselected,
        columns: [
            {title: "name", field: "name"},
            {title: "class", field: "class"},
            {title: "priority", field: "priority"},
            {title: "location", field: "location"},
            {title: "category", field: "category"},
            {title: "rationale", field: "rationale"},
            {title: "correctiveaction", field: "correctiveaction"},
            {title: "pointofcontactusername", field: "pointofcontactusername"},
            {title: "filterable", field: "filterable"},
            {title: "latching", field: "latching"},
            {title: "ondelayseconds", field: "ondelayseconds"},
            {title: "offdelayseconds", field: "offdelayseconds"},
            {title: "maskedby", field: "maskedby"},
            {title: "screenpath", field: "screenpath"},
            {title: "epicspv", field: "epicspv"}
        ]
    });

    updateRegistrationCountLabel();

    console.timeEnd('registered-init');
    setTimeout(batchRegisteredTableUpdate, maxUpdateMillis);
});

evtSource.addEventListener("effective-highwatermark", function (e) {
    const {updateOrAdd, remove} = processEffectiveEvents(75000);

    effectivetable = new Tabulator("#effective-table", {
        data: updateOrAdd,
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
            {title: "priority", field: "priority"},
            {title: "location", field: "location"},
            {title: "category", field: "category"},
            {title: "rationale", field: "rationale"},
            {title: "correctiveaction", field: "correctiveaction"},
            {title: "pointofcontactusername", field: "pointofcontactusername"},
            {title: "filterable", field: "filterable"},
            {title: "latching", field: "latching"},
            {title: "ondelayseconds", field: "ondelayseconds"},
            {title: "offdelayseconds", field: "offdelayseconds"},
            {title: "maskedby", field: "maskedby"},
            {title: "screenpath", field: "screenpath"},
            {title: "epicspv", field: "epicspv"}
        ]
    });

    updateEffectiveCountLabel();

    setTimeout(batchEffectiveTableUpdate, maxUpdateMillis);
});


evtSource.addEventListener("class", function (e) {
    let json = JSON.parse(e.data),
        key = json.key,
        value = json.value;

    classesEvents.set(key, value);
});

let processClassEvents = function (maxRecords = maxRecordsPerUpdate) {
    const updateOrAdd = [];
    const remove = [];

    let keys = classesEvents.keys();

    let recordsToProcess = Math.min(classesEvents.size, maxRecords);

    for(let i = 0; i < recordsToProcess; i++) {
        const key = keys.next().value;
        let value = classesEvents.get(key);

        if (value == null) { /*null means tombstone*/
            remove.push(key);
        } else {
            updateOrAdd.push({
                name: key,
                priority: value.priority,
                location: value.location,
                category: value.category,
                rationale: value.rationale,
                correctiveaction: value.correctiveaction,
                pointofcontactusername: value.pointofcontactusername,
                filterable: value.filterable,
                latching: value.latching,
                ondelayseconds: unwrapNullableUnionText(value.ondelayseconds),
                offdelayseconds: unwrapNullableUnionText(value.offdelayseconds),
                maskedby: unwrapNullableUnionText(value.maskedby),
                screenpath: value.screenpath
            });
        }

        classesEvents.delete(key);
    }

    return {updateOrAdd, remove};
};

evtSource.addEventListener("registration", function (e) {
    let json = JSON.parse(e.data),
        key = json.key,
        value = json.value;

    registeredEvents.set(key, value);
});

let processRegistrationEvents = function (maxRecords = maxRecordsPerUpdate) {
    const updateOrAdd = [];
    const remove = [];

    let keys = registeredEvents.keys();

    let recordsToProcess = Math.min(registeredEvents.size, maxRecords);

    for(let i = 0; i < recordsToProcess; i++) {
        const key = keys.next().value;
        let value = registeredEvents.get(key);

        if (value == null) { /*null means tombstone*/
            remove.push(key);
        } else {
            let epicspv = null;

            if ("org.jlab.jaws.entity.EPICSProducer" in value.producer) {
                epicspv = value.producer["org.jlab.jaws.entity.EPICSProducer"].pv;
            }

            updateOrAdd.push({
                name: key,
                class: value.class,
                priority: unwrapNullableUnionText(value.priority),
                location: unwrapNullableUnionText(value.location),
                category: unwrapNullableUnionText(value.category),
                rationale: unwrapNullableUnionText(value.rationale),
                correctiveaction: unwrapNullableUnionText(value.correctiveaction),
                pointofcontactusername: unwrapNullableUnionText(value.pointofcontactusername),
                filterable: unwrapNullableUnionText(value.filterable),
                latching: unwrapNullableUnionText(value.latching),
                ondelayseconds: unwrapNullableUnionText(value.ondelayseconds),
                offdelayseconds: unwrapNullableUnionText(value.offdelayseconds),
                maskedby: unwrapNullableUnionText(value.maskedby),
                screenpath: unwrapNullableUnionText(value.screenpath),
                epicspv: epicspv
            });
        }

        registeredEvents.delete(key);
    }

    return {updateOrAdd, remove};
};

evtSource.addEventListener("effective", function (e) {
    let json = JSON.parse(e.data),
        key = json.key,
        value = json.value;

    effectiveEvents.set(key, value);
});

let processEffectiveEvents = function (maxRecords = maxRecordsPerUpdate) {
    const updateOrAdd = [];
    const remove = [];

    let keys = effectiveEvents.keys();

    let recordsToProcess = Math.min(effectiveEvents.size, maxRecords);

    for(let i = 0; i < recordsToProcess; i++) {
        const key = keys.next().value;
        let value = effectiveEvents.get(key);

        if (value == null || value.calculated == null) { /*null means tombstone*/
            remove.push(key);
        } else {
            value = value.calculated["org.jlab.jaws.entity.AlarmRegistration"];

            let epicspv = null;

            if ("org.jlab.jaws.entity.EPICSProducer" in value.producer) {
                epicspv = value.producer["org.jlab.jaws.entity.EPICSProducer"].pv;
            }

            updateOrAdd.push({
                name: key,
                class: value.class,
                priority: unwrapNullableUnionText(value.priority),
                location: unwrapNullableUnionText(value.location),
                category: unwrapNullableUnionText(value.category),
                rationale: unwrapNullableUnionText(value.rationale),
                correctiveaction: unwrapNullableUnionText(value.correctiveaction),
                pointofcontactusername: unwrapNullableUnionText(value.pointofcontactusername),
                filterable: unwrapNullableUnionText(value.filterable),
                latching: unwrapNullableUnionText(value.latching),
                ondelayseconds: unwrapNullableUnionText(value.ondelayseconds),
                offdelayseconds: unwrapNullableUnionText(value.offdelayseconds),
                maskedby: unwrapNullableUnionText(value.maskedby),
                screenpath: unwrapNullableUnionText(value.screenpath),
                epicspv: epicspv
            });
        }

        effectiveEvents.delete(key);
    }

    return {updateOrAdd, remove};
};

evtSource.onerror = function (e) {
    console.log('error')
    console.log(e)
}

let updateClassCountLabel = function() {
    let count = classestable.getDataCount("active");

    let sorters = classestable.getSorters();
    classestable.setSort(sorters);

    $("#class-record-count").text(count.toLocaleString());
};

let batchClassesTableUpdate = function () {
    const {updateOrAdd, remove} = processClassEvents();

    if (updateOrAdd.length > 0 || remove.length > 0) {
        console.time('batchClassesTableUpdate');

        let promises = [];

        if(remove.length > 0) {
            //promises.push(classestable.deleteRow(remove));
        }

        if(updateOrAdd.length > 0) {
            promises.push(classestable.updateOrAddData(updateOrAdd));
        }

        Promise.all(promises).then(function(){
            updateClassCountLabel();

            console.timeEnd('batchClassesTableUpdate');
            setTimeout(batchClassesTableUpdate, maxUpdateMillis);
        });
    } else {
        setTimeout(batchClassesTableUpdate, maxUpdateMillis);
    }
}

let updateRegistrationCountLabel = function() {
    let count = registeredtable.getDataCount("active");

    let sorters = registeredtable.getSorters();
    registeredtable.setSort(sorters);

    $("#registered-record-count").text(count.toLocaleString());
};

let batchRegisteredTableUpdate = function () {
    const {updateOrAdd, remove} = processRegistrationEvents();

    if (updateOrAdd.length > 0 || remove.length > 0) {
        console.time('batchRegisteredTableUpdate');

        let promises = [];

        if(remove.length > 0) {
            //promises.push(registeredtable.deleteRow(remove));
        }

        if(updateOrAdd.length > 0) {
            promises.push(registeredtable.updateOrAddData(updateOrAdd));
        }

        Promise.all(promises).then(function(){
            updateRegistrationCountLabel();

            console.timeEnd('batchRegisteredTableUpdate');
            setTimeout(batchRegisteredTableUpdate, maxUpdateMillis);
        });
    } else {
        setTimeout(batchRegisteredTableUpdate, maxUpdateMillis);
    }
}

let updateEffectiveCountLabel = function() {
    let count = effectivetable.getDataCount("active");

    let sorters = effectivetable.getSorters();
    effectivetable.setSort(sorters);

    $("#effective-record-count").text(count.toLocaleString());
};

let batchEffectiveTableUpdate = function () {
    const {updateOrAdd, remove} = processEffectiveEvents();

    if (updateOrAdd.length > 0 || remove.length > 0) {
        console.time('batchEffectiveTableUpdate');
        let promises = [];

        if(remove.length > 0) {
            //promises.push(effectivetable.deleteRow(remove));
        }

        if(updateOrAdd.length > 0) {
            promises.push(effectivetable.updateOrAddData(updateOrAdd));
        }

        Promise.all(promises).then(function(){
            updateEffectiveCountLabel();

            console.timeEnd('batchEffectiveTableUpdate');
            setTimeout(batchEffectiveTableUpdate, maxUpdateMillis);
        });
    } else {
        setTimeout(batchEffectiveTableUpdate, maxUpdateMillis);
    }
}


let prioritySelect = document.getElementById('priority-select');
fetch('proxy/rest/priorities')
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


let locationSelect = document.getElementById('location-select');
fetch('proxy/rest/locations')
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


let categorySelect = document.getElementById('category-select');
fetch('proxy/rest/categories')
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