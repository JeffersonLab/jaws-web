const urlObject = new URL(self.location);
const contextPath = '/' + urlObject.pathname.split('/')[1];

const activeByName = new Map();

class EffectiveAlarm {
    constructor(name, priority, category, rationale, action, contact, filterable, latchable,
                ondelay, offdelay, alarmclass, device, location, maskedby, screencommand, epicspv, state,
                disabled, ondelayed, offdelayed, filtered, oneshot, reason, comments, latched, masked, type, error, note, sevr, stat) {
        this.name = name;

        this.priority = priority;
        this.category = category;
        this.rationale = rationale;
        this.action = action;
        this.contact = contact;
        this.filterable = filterable;
        this.latchable = latchable;
        this.ondelay = ondelay;
        this.offdelay = offdelay;

        this.class = alarmclass;
        this.device = device;
        this.location = location;
        this.maskedby = maskedby;
        this.screencommand = screencommand;
        this.epicspv = epicspv;

        this.state = state;
        this.disabled = disabled;
        this.ondelayed = ondelayed;
        this.offdelayed = offdelayed;
        this.filtered = filtered;
        this.oneshot = oneshot;
        this.reason = reason;
        this.comments = comments;
        this.latched = latched;
        this.masked = masked;
        this.type = type;
        this.error = error;
        this.note = note;
        this.sevr = sevr;
        this.stat = stat;
    }
}

let toAlarm = function(key, value) {

    value.registration = value.registration || {};
    value.notification = value.notification || {};
    value.notification.overrides = value.notification.overrides || {};
    value.notification.activation = value.notification.activation || {};

    return new EffectiveAlarm(
        key,
        value.registration.priority,
        value.registration.category,
        value.registration.rationale,
        value.registration.action,
        value.registration.contact,
        value.registration.filterable,
        value.registration.latchable,
        value.registration.ondelay,
        value.registration.offdelay,
        value.registration.class,
        value.registration.device,
        value.registration.location ? value.registration.location.join(',') : undefined,
        value.registration.maskedby,
        value.registration.screencommand,
        value.registration.source ? value.registration.source.pv : undefined,
        value.notification.state,
        value.notification.overrides.disabled ? value.notification.overrides.disabled.comments : undefined,
        value.notification.overrides.ondelayed ? value.notification.overrides.ondelayed.expiration : undefined,
        value.notification.overrides.offdelayed ? value.notification.overrides.offdelayed.expiration: undefined,
        value.notification.overrides.filtered ? value.notification.overrides.filtered.filtername : undefined,
        value.notification.overrides.shelved ? value.notification.overrides.shelved.oneshot : undefined,
        value.notification.overrides.shelved ? value.notification.overrides.shelved.reason : undefined,
        value.notification.overrides.shelved ? value.notification.overrides.shelved.comments : undefined,
        value.notification.overrides.latched ? true : false,
        value.notification.overrides.masked ? true : false,
        value.notification.activation.union.type,
        value.notification.activation.union.error,
        value.notification.activation.union.note,
        value.notification.activation.union.sevr,
        value.notification.activation.union.stat
    );
}


let evtSource = new EventSource(contextPath + '/proxy/sse' + '?entitiesCsv=alarm&initiallyActiveOnly=true');

evtSource.onerror = (err) => {
    console.error("EventSource failed:", err);
};

let table = document.getElementById("alarm-table"),
    tbody = table.querySelector("tbody"),
    thList = table.querySelectorAll("thead th"),
    columnStrList = [...thList.values()].map(th => th.textContent),
    alarmCountSpan = document.getElementById("alarm-count"),
    diagramContainer = document.getElementById("diagram-container");

function addToTable(data) {
    for(const record of data) {
        let tr = document.createElement("tr");

        const map = new Map(Object.entries(record));

        for (const column of columnStrList) {
            let value = map.get(column);
            let td = document.createElement("td");
            td.innerText = value === undefined ? '' : value;
            tr.appendChild(td);
        }

        tbody.appendChild(tr);
    }
}

function removeFromTable(keys) {
    for(const record of keys) {

        let nameList = tbody.querySelectorAll("tr td:first-child");

        for(let i = 0; i < nameList.length; i++) {
            let name = nameList[i].textContent;

            if(name === record) {
                // deleteRow method may only exist on table, not tbody?  +1 for thead row?
                tbody.deleteRow(i);
                break;
            }
        }
    }
}

function addAlarms(data) {

    let keys = data.map(item => item.name);

    for(const record of data) {
        activeByName.set(record.name, record);

        addToDiagram(record);
    }

    //removeFromTable(keys);

    //addToTable(data);
}

function removeAlarms(keys) {

    for(const name of keys) {
        activeByName.delete(name);

        removeFromDiagram(name);
    }

    //removeFromTable(keys);
}

const livenessEl = document.getElementById("liveness-ts");

evtSource.addEventListener('ping', (e) =>{
    let ts = e.data,
        formatted = new Date(ts);

    livenessEl.innerHTML = formatted.toLocaleString();
});

const loading = document.getElementById('loading');

evtSource.addEventListener('alarm-highwatermark', (e) =>{
    loading.style.display = "none";
    alarmCountSpan.style.display = "inline";
});

evtSource.addEventListener('alarm', (e) => {

    let records = JSON.parse(e.data);

    let remove = [];
    let updateOrAdd = [];
    let compacted = new Map();

    /* Compact, in-order */
    for (const record of records) {
        compacted.set(record.key, record.value);
    }

    for (const [key, value] of compacted.entries()) {

        if (value === null || value.notification.state.startsWith('Normal')) {
            remove.push(key);
        } else {
            updateOrAdd.push(toAlarm(key, value));
        }
    }

    if (remove.length > 0) {
        removeAlarms(remove);
    }

    if (updateOrAdd.length > 0) {
        addAlarms(updateOrAdd);
    }

    updateCount();
});
function updateCount() {
    let count = activeByName.size;

    alarmCountSpan.innerText = jlab.integerWithCommas(count);

    if(count > 0) {
        alarmCountSpan.classList.add("alarming");
    } else {
        alarmCountSpan.classList.remove("alarming");
    }
}
function addToDiagram(alarm) {
    const element = document.createElement("span"),
          id = "alarm-" + alarm.name.replaceAll(' ', ''),
          locationCsv = alarm.location;

    element.setAttribute("id", id);

    let locationArray = locationCsv.split(",");

    for(let l of locationArray) {
        const locElement = document.createElement("span");
        let locationClass = 'location-' + l.replaceAll(' ', '');
        locElement.classList.add(locationClass);
        element.append(locElement);
    }

    diagramContainer.appendChild(element);
}
function removeFromDiagram(name) {
    let id = "alarm-" + name.replaceAll(' ', ''),
        element = document.getElementById(id);

    if(element) {
        element.remove();
    }
}
$(function() {
    $("#all-dialog").dialog({
        autoOpen: false,
        width: 800,
        height: 600
    });
});

let tempSort = {},
    offset = 0,
    max = 1000;
$(document).on("click", "#show-all", function() {
    $("#all-dialog").dialog('open');

    tbody.innerHTML = '';

    tempSort = Array.from(activeByName.values()).sort();

    let subset = tempSort.slice(offset, max);

    addToTable(subset);
});