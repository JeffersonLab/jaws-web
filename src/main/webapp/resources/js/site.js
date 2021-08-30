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


let insertUnionText = function(text, row, index) {
    if(text != null) {
        text = Object.values(text)[0];
    }
    let cell = row.insertCell(index++);
    cell.appendChild(document.createTextNode(text));
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

    console.log(json);

    let keys = document.querySelectorAll("#registered-table tbody tr td:nth-child(2)");

    keys.forEach(
        function(td, index, list) {
            if(key === td.textContent) {
                console.log('match: ', key, td.textContent);
                registeredTableBody.deleteRow(index);
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

    let row = registeredTableBody.insertRow(-1),
        cell = row.insertCell(index++);

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
    cell.append(deleteBtn)

    cell = row.insertCell(index++);
    cell.appendChild(document.createTextNode(key));

    insertUnionText(value.priority, row, index++);

    cell = row.insertCell(index++);
    cell.appendChild(document.createTextNode(JSON.stringify(value.producer)));

    cell = row.insertCell(index++);
    cell.appendChild(document.createTextNode(value.class));

    insertUnionText(value.location, row, index++);
    insertUnionText(value.category, row, index++);
    insertUnionText(value.rationale, row, index++);
    insertUnionText(value.correctiveaction, row, index++);
    insertUnionText(value.pointofcontactusername, row, index++);
    insertUnionText(value.filterable, row, index++);
    insertUnionText(value.latching, row, index++);
    insertUnionText(value.ondelayseconds, row, index++);
    insertUnionText(value.offdelayseconds, row, index++);
    insertUnionText(value.maskedby, row, index++);
    insertUnionText(value.screenpath, row, index++);
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