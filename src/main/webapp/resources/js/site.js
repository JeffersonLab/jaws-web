let maxUpdateMillis = 500;
let maxRecordsPerUpdate = 100;

const classesEvents = new Map();
const registeredEvents = new Map();
const effectiveEvents = new Map();

var classestable = null;
var registeredtable = null;
var effectivetable = null;

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

evtSource.addEventListener("registration", function (e) {
    let json = JSON.parse(e.data),
        key = json.key,
        value = json.value;

    registeredEvents.set(key, value);
});

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
