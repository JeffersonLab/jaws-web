

$( function() {
    $( "#tabs" ).tabs();
} );

var tabledata = [
];

var rowSelected = function(row) {
    $(".no-selection-row-action").prop("disabled", true);
    $(".selected-row-action").prop("disabled", false);
};

var rowDeselected = function(row) {
    $(".no-selection-row-action").prop("disabled", false);
    $(".selected-row-action").prop("disabled", true);
};

var table = new Tabulator("#registered-table", {
    data: tabledata,
    reactiveData: true,
    height: 200, // enables the Virtual DOM
    layout: "fitColumns",
    responsiveLayout: "collapse",
    index: "name",
    selectable: 1,
    rowSelected: rowSelected,
    rowDeselected: rowDeselected,
    columns: [
        {title:"Alarm Name", field:"name"},
        {title:"Class", field:"class"},
        {title:"Priority", field:"priority"},
        {title:"Producer", field:"producer"},
        {title:"Location", field:"location"},
        {title:"Category", field:"category"},
        {title:"Rationale", field:"rationale"},
        {title:"Corrective Action", field:"correctiveaction"},
        {title:"Point of Contact Username", field:"pointofcontactusername"},
        {title:"Filterable", field:"filterable"},
        {title:"Latching", field:"latching"},
        {title:"On Delay Seconds", field:"ondelayseconds"},
        {title:"Off Delay Seconds", field:"offdelayseconds"},
        {title:"Masked By", field:"maskedby"},
        {title:"Screen Path", field:"screenpath"},
    ]
});


$(document).on("click", "#delete-registration-button", function() {
    console.log('attempting to delete');

    let selectedData = table.getSelectedData();

    console.log("selectedData:", selectedData);

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
            console.log("attempting to deselect");
            rowDeselected();
        })
        .catch(error => {
            console.error('Delete failed: ', error)
        });
});


let classButton = document.getElementById('class-submit');

classButton.addEventListener("click", function(e) {
    let form = document.getElementById("class-form"),
        formData = new FormData(form);

    /*Treat empty string as no-field*/
    for(var pair of Array.from(formData.entries())) {
        if(pair[1] === "") {
            console.log('deleting: ', pair);
            formData.delete(pair[0]);
        } else {
            console.log('keeping: ', pair)
        }
    }

    let promise = fetch("proxy/rest/class", {
        method: "PUT",
        body: new URLSearchParams(formData),
        headers: {
            "Content-type": 'application/x-www-form-urlencoded;charset=UTF-8'
        }
    });

    form.reset();
});




let button = document.getElementById('registered-submit');

button.addEventListener("click", function(e) {
    let form = document.getElementById("registered-form"),
        formData = new FormData(form);

    /*Treat empty string as no-field*/
    for(var pair of Array.from(formData.entries())) {
        if(pair[1] === "") {
            console.log('deleting: ', pair);
            formData.delete(pair[0]);
        } else {
            console.log('keeping: ', pair)
        }
    }

    let promise = fetch("proxy/rest/registered", {
        method: "PUT",
        body: new URLSearchParams(formData),
        headers: {
            "Content-type": 'application/x-www-form-urlencoded;charset=UTF-8'
        }
    });

    form.reset();
});




let evtSource = new EventSource('proxy/sse'),
    registeredTable = document.getElementById('registered-table'),
    registeredTableBody = registeredTable.getElementsByTagName("tbody")[0],
    classTable = document.getElementById('class-table');
classTableBody = classTable.getElementsByTagName("tbody")[0];

console.log('attempting sse...')


let unwrapNullableUnionText = function(text) {
    if(text != null) {
        text = Object.values(text)[0];
    }
    return text;
};

let insertText = function(text, row, index) {
    let cell = row.insertCell(index++);
    cell.appendChild(document.createTextNode(text));
}

evtSource.addEventListener("registration", function(e) {
    console.log('registration type message')
    let json = JSON.parse(e.data),
        key = json.key,
        value = json.value;

    const i = tabledata.findIndex(element => element.name === key);

    if(i !== -1) {
        tabledata.splice(i, 1);
    }

    if(value === null) {
        console.log("tombstone encountered");
        return;
    }

    tabledata.push({name: key,
        class: value.class,
        priority: unwrapNullableUnionText(value.priority),
        producer: JSON.stringify(value.producer),
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
        screenpath: unwrapNullableUnionText(value.screenpath)});

    console.log(tabledata);

    let deleteBtn = document.createElement('input');
    deleteBtn.type = "button";
    deleteBtn.value = "X";
    deleteBtn.onclick = function() {

        console.log('attempting to delete: ', key);

        let params = "name=" + key;

        let promise = fetch("proxy/rest/registered", {
            method: "DELETE",
            body: new URLSearchParams(params),
            headers: {
                "Content-type": 'application/x-www-form-urlencoded;charset=UTF-8'
            }
        });
    };
});

evtSource.addEventListener("class", function(e) {
    console.log('class type message: ');

    let json = JSON.parse(e.data),
        key = json.key,
        value = json.value;

    console.log(json);

    let keys = document.querySelectorAll("#class-table tbody tr td:nth-child(2)");

    keys.forEach(
        function(td, index, list) {
            if(key === td.textContent) {
                console.log('match: ', key, td.textContent);
                classTableBody.deleteRow(index);
            } else {
                console.log('no match: ', key, td.textContent, index, list);
            }
        }
    );

    if(value === null) {
        console.log("tombstone encountered");
        return;
    }

    let index = 0;

    let row = classTableBody.insertRow(-1),
        cell = row.insertCell(index++);

    let deleteBtn = document.createElement('input');
    deleteBtn.type = "button";
    deleteBtn.value = "X";
    deleteBtn.onclick = function() {

        console.log('attempting to delete: ', key);

        let params = "name=" + key;

        let promise = fetch("proxy/rest/class", {
            method: "DELETE",
            body: new URLSearchParams(params),
            headers: {
                "Content-type": 'application/x-www-form-urlencoded;charset=UTF-8'
            }
        });
    };
    cell.append(deleteBtn)

    cell = row.insertCell(index++);
    cell.appendChild(document.createTextNode(key));

    insertText(value.priority, row, index++);

    insertText(value.location, row, index++);
    insertText(value.category, row, index++);
    insertText(value.rationale, row, index++);
    insertText(value.correctiveaction, row, index++);
    insertText(value.pointofcontactusername, row, index++);
    insertText(value.filterable, row, index++);
    insertText(value.latching, row, index++);
    insertText(value.ondelayseconds, row, index++);
    insertText(value.offdelayseconds, row, index++);
    insertText(value.maskedby, row, index++);
    insertText(value.screenpath, row, index++);
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
                other.id = 'cloned-priority-select';

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
                other.id = 'cloned-location-select';

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
                other.id = 'cloned-category-select';

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