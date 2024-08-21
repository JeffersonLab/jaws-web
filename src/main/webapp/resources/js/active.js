const urlObject = new URL(self.location);
const contextPath = '/' + urlObject.pathname.split('/')[1];

const activeByLocation = new Map();
const activeByName = new Map();
const activeUnregistered = new Map();
const activeUnfilterable = new Map();

const cebafTree = ['CEBAF', 'MCC', 'ESR', 'HallA', 'HallB', 'HallC', 'HallD'],
      injTree = ['Injector', '1D', '2D', '3D', '4D', '5D'],
      nlTree = ['North Linac', 'Linac1', 'Linac3', 'Linac5', 'Linac7', 'Linac9'],
      slTree = ['South Linac', 'Linac2', 'Linac4', 'Linac6', 'Linac8'],
      eaTree = ['East Arc', 'ARC1', 'ARC3', 'ARC5', 'ARC7', 'ARC9'],
      waTree = ['West Arc', 'ARC2', 'ARC4', 'ARC6', 'ARC8', 'ARCA'],
      bsyTree = ['BSY', 'BSY Dump', 'BSY2', 'BSY4', 'BSY6', 'BSY8', 'BSYA'];

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
    cebafCountSpan = document.getElementById("cebaf-count"),
    injectorCountSpan = document.getElementById("injector-count"),
    southlinacCountSpan = document.getElementById("southlinac-count"),
    diagramContainer = document.getElementById("diagram-container"),
    unregisteredCountSpan = document.getElementById("unregistered-count"),
    unregisteredSpan = document.getElementById("unregistered"),
    unfilterableCountSpan = document.getElementById("unfilterable-count"),
    unfilterableSpan = document.getElementById("unfilterable");

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

function updateOrAddAlarms(data) {

    let keys = data.map(item => item.name);

    for(const record of data) {
        activeByName.set(record.name, record);

        updateOrAddToDiagram(record);
    }

    //removeFromTable(keys);

    //addToTable(data);
}

function removeAlarms(keys) {

    for(const name of keys) {
        let alarm = activeByName.get(name);


        if(alarm !== undefined) {
            let locationCsv = alarm.location;
            let locationArray = [];

            if (locationCsv === undefined) {
                locationArray = ['JLAB']
            } else {
                locationArray = locationCsv.split(",");
            }

            for (let l of locationArray) {
                let alarmSet = activeByLocation.get(l);
                if (alarmSet !== undefined) {
                    alarmSet.delete(name);
                }
            }
        }

        activeByName.delete(name);
        activeUnregistered.delete(name);
        activeUnfilterable.delete(name);

        removeFromDiagram(name);
    }

    //removeFromTable(keys);
}

const livenessEl = document.getElementById("liveness-ts");

evtSource.addEventListener('ping', (e) =>{
    let ts = e.data,
        formatted = new Date(ts);

    livenessEl.innerHTML = formatted.toLocaleTimeString();
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
            let inLocationSet = false;

            if(value.registration.location === undefined) {
                // Unregistered
                activeUnregistered.set(key, value);
                inLocationSet = true;
            } else if(value.registration.filterable === false) {
                // Unfilterable
                activeUnfilterable.set(key, value);
                inLocationSet = true;
            } else if(jlab.materializedLocations.length === 0) {
                    // No filter applied
                    inLocationSet = true;
            } else {
                //console.log('before', JSON.stringify(value.registration.location));
                let effectiveLocation = [];
                for(const l of value.registration.location) {
                    if(jlab.materializedLocations.includes(l)) {
                        inLocationSet = true;
                        effectiveLocation.push(l);
                    }
                }
                value.registration.location = effectiveLocation;
                //console.log('after', JSON.stringify(value.registration.location));
            }

            if(inLocationSet) {
                updateOrAdd.push(toAlarm(key, value));
            }
        }
    }

    if (remove.length > 0) {
        removeAlarms(remove);
    }

    if (updateOrAdd.length > 0) {
        updateOrAddAlarms(updateOrAdd);
    }

    updateCount();
});
function updateCount() {
    let count = activeByName.size;

    alarmCountSpan.firstElementChild.innerText = jlab.integerWithCommas(count);

    if (count > 0) {
        alarmCountSpan.classList.add("alarming");
    } else {
        alarmCountSpan.classList.remove("alarming");
    }

    unregisteredCountSpan.textContent = jlab.integerWithCommas(activeUnregistered.size);

    if (activeUnregistered.size > 0) {
        unregisteredSpan.classList.add("shown");
    } else {
        unregisteredSpan.classList.remove("shown");
    }

    unfilterableCountSpan.textContent = jlab.integerWithCommas(activeUnfilterable.size);

    if (activeUnfilterable.size > 0) {
        unfilterableSpan.classList.add("shown");
    } else {
        unfilterableSpan.classList.remove("shown");
    }

    //console.log('activeByLocation', activeByLocation);

    let injUnion = unionOfLocationTree(injTree);
    let nlUnion = unionOfLocationTree(nlTree);
    let slUnion = unionOfLocationTree(slTree);
    let eaUnion = unionOfLocationTree(eaTree);
    let waUnion = unionOfLocationTree(waTree);
    let bsyUnion = unionOfLocationTree(bsyTree);

    let cebafUnion = unionOfLocationTree(cebafTree);
    cebafUnion = cebafUnion.union(injUnion);
    cebafUnion = cebafUnion.union(nlUnion);
    cebafUnion = cebafUnion.union(slUnion);
    cebafUnion = cebafUnion.union(eaUnion);
    cebafUnion = cebafUnion.union(waUnion);
    cebafUnion = cebafUnion.union(bsyUnion);

    //console.log('cebafUnion', cebafUnion);

    cebafCountSpan.firstElementChild.innerText = jlab.integerWithCommas(cebafUnion.size);
    injectorCountSpan.firstElementChild.innerText = jlab.integerWithCommas(injUnion.size);
    southlinacCountSpan.firstElementChild.innerText = jlab.integerWithCommas(slUnion.size);

    updateShown(cebafUnion, cebafCountSpan);
    updateShown(injUnion, injectorCountSpan);
    updateShown(slUnion, southlinacCountSpan);
}
function updateShown(locationUnion, span) {
    if (locationUnion.size > 0) {
        span.classList.add("location-active");
    } else {
        span.classList.remove("location-active");
    }
}
function unionOfLocationTree(locationTree) {
    let unionSet = new Set([]);
    for (let l of locationTree) {
        let locationSet = activeByLocation.get(l);
        if(locationSet !== undefined) {
            unionSet = unionSet.union(locationSet);
        }
    }

    return unionSet;
}

function updateOrAddToDiagram(alarm) {
    const element = document.createElement("span"),
          id = "alarm-" + alarm.name.replaceAll(' ', ''),
          existing = document.getElementById(id),
          locationCsv = alarm.location;

    element.setAttribute("id", id);

    let locationArray = [];

    if(locationCsv === undefined) {
        locationArray = ['JLAB']
    } else {
        locationArray = locationCsv.split(",");
    }

    for(let l of locationArray) {
        let alarmSet = activeByLocation.get(l);
        if(alarmSet === undefined) {
            alarmSet = new Set([]);
            activeByLocation.set(l, alarmSet);
        }
        alarmSet.add(alarm.name);

        const locElement = document.createElement("span");
        let locationClass = 'location-' + l.replaceAll(' ', '');
        locElement.classList.add(locationClass);
        element.append(locElement);
    }

    if(existing !== null) {
        existing.remove();
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
$(document).on("click", ".default-clear-panel", function () {
    $("#location-select").val(null).trigger('change');
    return false;
});
function formatLocation(location) {
    return location.text.trim();
}
$(function() {
    $("#all-dialog").dialog({
        autoOpen: false,
        width: 800,
        height: 600
    });

    $("#location-select").select2({
        width: 390,
        templateSelection: formatLocation
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