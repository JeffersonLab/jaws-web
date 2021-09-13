

let setRegistration = function() {
    let form = document.getElementById("registered-form"),
        formData = new FormData(form);

    /*Treat empty string as no-field*/
    for(var pair of Array.from(formData.entries())) {
        if(pair[1] === "") {
            formData.delete(pair[0]);
        }
    }

    let promise = fetch("proxy/rest/registered", {
        method: "PUT",
        body: new URLSearchParams(formData),
        headers: {
            Accept: "application/json",
            "Content-type": 'application/x-www-form-urlencoded;charset=UTF-8'
        }
    });

    promise.then(response => {
        if(response.ok) {
            registeredRowDeselected();
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
};


let setClass = function() {
    let form = document.getElementById("class-form"),
        formData = new FormData(form);

    /*Treat empty string as no-field*/
    for(var pair of Array.from(formData.entries())) {
        if(pair[1] === "") {
            formData.delete(pair[0]);
        }
    }

    let promise = fetch("proxy/rest/class", {
        method: "PUT",
        body: new URLSearchParams(formData),
        headers: {
            Accept: "application/json",
            "Content-type": 'application/x-www-form-urlencoded;charset=UTF-8'
        }
    });

    promise.then(response => {
        if(response.ok) {
            classRowDeselected();
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
};

$( function() {
    $( ".toolbar button" ).button();

    $( "#tabs" ).tabs({
        activate: function( event, ui ) {
            ui.newPanel.css("display","flex");
        }
    }).show();

    var registrationDialog = $("#registration-dialog").dialog({
        autoOpen: false,
        height: 400,
        width: 400,
        modal: true,
        buttons: {
            Set: setRegistration,
            Cancel: function() {
                registrationDialog.dialog( "close" );
            }
        }
    });

    registrationDialog.find( "form" ).on( "submit", function( event ) {
        event.preventDefault();
        setRegistration();
    });

    var classDialog = $("#class-dialog").dialog({
        autoOpen: false,
        height: 400,
        width: 400,
        modal: true,
        buttons: {
            Set: setClass,
            Cancel: function() {
                classDialog.dialog( "close" );
            }
        }
    });

    classDialog.find( "form" ).on( "submit", function( event ) {
        event.preventDefault();
        setClass();
    });
} );

var registeredtabledata = [
];

var classestabledata = [
];

var registeredRowSelected = function(row) {
    $("#registered-toolbar .no-selection-row-action").button( "option", "disabled", true );
    $("#registered-toolbar .selected-row-action").button( "option", "disabled", false );
};

var classRowSelected = function(row) {
    $("#class-toolbar .no-selection-row-action").button( "option", "disabled", true );
    $("#class-toolbar .selected-row-action").button( "option", "disabled", false );
};

var registeredRowDeselected = function(row) {
    $("#registered-toolbar .no-selection-row-action").button( "option", "disabled", false );
    $("#registered-toolbar .selected-row-action").button( "option", "disabled", true );
};

var classRowDeselected = function(row) {
    $("#class-toolbar .no-selection-row-action").button( "option", "disabled", false );
    $("#class-toolbar .selected-row-action").button( "option", "disabled", true );
};

var registeredtable = null;

var classestable = new Tabulator("#classes-table", {
    data: classestabledata,
    reactiveData: true,
    height: "100%", // enables the Virtual DOM
    layout: "fitColumns",
    responsiveLayout: "collapse",
    index: "name",
    selectable: 1,
    initialSort:[
        {column:"name", dir:"asc"}
    ],
    rowSelected: classRowSelected,
    rowDeselected: classRowDeselected,
    columns: [
        {title:"name", field:"name"},
        {title:"priority", field:"priority"},
        {title:"location", field:"location"},
        {title:"category", field:"category"},
        {title:"rationale", field:"rationale"},
        {title:"correctiveaction", field:"correctiveaction"},
        {title:"pointofcontactusername", field:"pointofcontactusername"},
        {title:"filterable", field:"filterable"},
        {title:"latching", field:"latching"},
        {title:"ondelayseconds", field:"ondelayseconds"},
        {title:"offdelayseconds", field:"offdelayseconds"},
        {title:"maskedby", field:"maskedby"},
        {title:"screenpath", field:"screenpath"}
    ]
});


$(document).on("click", "#new-registration-button", function() {

    document.getElementById("registered-form").reset();

    $("#registration-dialog").dialog("option", "title", "New Registration")
    $("#registration-dialog").dialog("open");
});

$(document).on("click", "#edit-registration-button", function() {
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

$(document).on("click", "#delete-registration-button", function() {
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
            if(!response.ok) {
                throw new Error("Network response not ok");
            }
            registeredRowDeselected();
        })
        .catch(error => {
            console.error('Delete failed: ', error)
        });
});

let registrationSearch = function() {
    registeredtable.clearFilter();

    let filterText = $("#registration-search-input").val();

    let filterArray = filterText.split(",");

    for(let filter of filterArray) {
        let keyValue = filter.split("=");

        registeredtable.addFilter(keyValue[0], "=", keyValue[1]);
    }

    let count = registeredtable.getDataCount("active");
    $("#registered-record-count").text(count.toLocaleString());
};

$(document).on( "submit", "#registered-search-form", function( event ) {
    event.preventDefault();
    registrationSearch();
});

$(document).on("click", "#search-registration-button", function() {
    registrationSearch();
});





$(document).on("click", "#new-class-button", function() {

    document.getElementById("class-form").reset();

    $("#class-dialog").dialog("option", "title", "New Class")
    $("#class-dialog").dialog("open");
});

$(document).on("click", "#edit-class-button", function() {
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

$(document).on("click", "#delete-class-button", function() {
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
        if(!response.ok) {
            throw new Error("Network response not ok");
        }
        classRowDeselected();
    })
        .catch(error => {
            console.error('Delete failed: ', error)
        });
});

$(document).on("click", "#search-class-button", function() {
    classestable.clearFilter();

    let filterText = $("#class-search-input").val();

    let filterArray = filterText.split(",");

    for(let filter of filterArray) {
        let keyValue = filter.split("=");

        classestable.addFilter(keyValue[0], "=", keyValue[1]);
    }
});




let evtSource = new EventSource('proxy/sse');

let unwrapNullableUnionText = function(text) {
    if(text != null) {
        text = Object.values(text)[0];
    }
    return text;
};

evtSource.addEventListener("registration-highwatermark", function(e){
    registeredtable = new Tabulator("#registered-table", {
        data: registeredtabledata,
        reactiveData: true,
        height: "100%", // enables the Virtual DOM
        layout: "fitColumns",
        responsiveLayout: "collapse",
        index: "name",
        selectable: 1,
        initialSort:[
            {column:"name", dir:"asc"}
        ],
        rowSelected: registeredRowSelected,
        rowDeselected: registeredRowDeselected,
        columns: [
            {title:"name", field:"name"},
            {title:"class", field:"class"},
            {title:"priority", field:"priority"},
            {title:"location", field:"location"},
            {title:"category", field:"category"},
            {title:"rationale", field:"rationale"},
            {title:"correctiveaction", field:"correctiveaction"},
            {title:"pointofcontactusername", field:"pointofcontactusername"},
            {title:"filterable", field:"filterable"},
            {title:"latching", field:"latching"},
            {title:"ondelayseconds", field:"ondelayseconds"},
            {title:"offdelayseconds", field:"offdelayseconds"},
            {title:"maskedby", field:"maskedby"},
            {title:"screenpath", field:"screenpath"},
            {title:"epicspv", field:"epicspv"}
        ]
    });
});

evtSource.addEventListener("registration", function(e) {
    let json = JSON.parse(e.data),
        key = json.key,
        value = json.value;

    const i = registeredtabledata.findIndex(element => element.name === key);

    if(i !== -1) {
        registeredtabledata.splice(i, 1);
    }

    if(value !== null) { /*null means tombstone*/
        let epicspv = null;

        if ("org.jlab.jaws.entity.EPICSProducer" in value.producer) {
            epicspv = value.producer["org.jlab.jaws.entity.EPICSProducer"].pv;
        }

        registeredtabledata.push({
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

    let count = 0;

    if(registeredtable === null) {
        count = registeredtabledata.length;
    } else {
        count = registeredtable.getDataCount("active");

        /*On tombstone this could be skipped...*/
        let sorters = registeredtable.getSorters();
        registeredtable.setSort(sorters);
    }

    $("#registered-record-count").text(count.toLocaleString());
});

evtSource.addEventListener("class", function(e) {
    let json = JSON.parse(e.data),
        key = json.key,
        value = json.value;

    const i = classestabledata.findIndex(element => element.name === key);

    if(i !== -1) {
        classestabledata.splice(i, 1);
    }

    if(value === null) {
        return;
    }

    classestabledata.push({name: key,
        priority: value.priority,
        location: value.location,
        category: value.category,
        rationale: value.rationale,
        correctiveaction:value.correctiveaction,
        pointofcontactusername: value.pointofcontactusername,
        filterable: value.filterable,
        latching: value.latching,
        ondelayseconds: unwrapNullableUnionText(value.ondelayseconds),
        offdelayseconds: unwrapNullableUnionText(value.offdelayseconds),
        maskedby: unwrapNullableUnionText(value.maskedby),
        screenpath: unwrapNullableUnionText(value.screenpath)});

    let sorters = classestable.getSorters();

    classestable.setSort(sorters);
});

evtSource.onerror = function(e) {
    console.log('error')
    console.log(e)
}







let prioritySelect = document.getElementById('priority-select');
fetch('proxy/rest/priorities')
    .then(
        function(response) {
            if (response.status !== 200) {
                console.warn('Error. Status Code: ' +
                    response.status);
                return;
            }

            response.json().then(function(data) {
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
    .catch(function(err) {
        console.error('Fetch Error -', err);
    });



let locationSelect = document.getElementById('location-select');
fetch('proxy/rest/locations')
    .then(
        function(response) {
            if (response.status !== 200) {
                console.warn('Error. Status Code: ' +
                    response.status);
                return;
            }

            response.json().then(function(data) {
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
    .catch(function(err) {
        console.error('Fetch Error -', err);
    });


let categorySelect = document.getElementById('category-select');
fetch('proxy/rest/categories')
    .then(
        function(response) {
            if (response.status !== 200) {
                console.warn('Error. Status Code: ' +
                    response.status);
                return;
            }

            response.json().then(function(data) {
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
    .catch(function(err) {
        console.error('Fetch Error -', err);
    });