import db from './resources/modules/jaws-admin-gui-@VERSION@/db.mjs';
import {AlarmActivation, AlarmCategory, AlarmClass, AlarmInstance, AlarmLocation, AlarmOverride, EffectiveAlarm, EffectiveNotification, EffectiveRegistration, KafkaLogPosition} from "./resources/modules/jaws-admin-gui-@VERSION@/entities.mjs";

const urlObject = new URL(self.location);
const contextPath = '/' + urlObject.pathname.split('/')[1];

class BackgroundWorker {
    constructor(eventType, toEntityFunc, store) {
        let me = this;

        me.eventType = eventType;
        me.toEntityFunc = toEntityFunc;
        me.store = store;
        me.eventQueue = [];
        me.processing = false;

        /* We must serialize via a queue to ensure highwater event is ordered with respect to batch events;
           batch events processing is async due to db access, so we must await those and block (by queuing) highwater
            messages during db write.   This also ensures db writes are serialized */
        me.processEvents = async function() {
            if(!me.processing) {
                me.processing = true;
                while (me.eventQueue.length > 0) {
                    let event = me.eventQueue.shift();

                    await event();
                }
                me.processing = false;
            }
        }

        me.start = function(evtSource) {
            evtSource.addEventListener(me.eventType + "-highwatermark", function (e) {
                me.eventQueue.push(async function() {
                    postMessage({type: me.eventType + "-highwatermark"});
                });

                me.processEvents();
            });

            evtSource.addEventListener(me.eventType, (e) => {
                me.eventQueue.push(async function() {
                    let records = JSON.parse(e.data);

                    let remove = [];
                    let updateOrAdd = [];
                    let compacted = new Map();

                    /* Compact, in-order */
                    for (const record of records) {
                        compacted.set(record.key, record.value);
                    }

                    for (const [key, value] of compacted.entries()) {

                        if (value === null) {
                            remove.push(key);
                        } else {
                            updateOrAdd.push(me.toEntityFunc(key, value));
                        }
                    }

                    if (remove.length > 0) {
                        await me.store.bulkDelete(remove);
                    }

                    if (updateOrAdd.length > 0) {
                        await me.store.bulkPut(updateOrAdd);
                    }

                    if (records.length > 0) {
                        let resumeIndex = records[records.length - 1].offset;
                        await db.positions.put(new KafkaLogPosition(me.eventType, resumeIndex));
                    }

                    postMessage({type: me.eventType, detail: records.length});
                });

                me.processEvents();
            });
        }
    }
}

let toAlarm = function(key, value) {

    value.registration = value.registration || {};
    value.activation = value.activation || {};
    value.activation.overrides = value.activation.overrides || {};
    value.activation.actual = value.activation.actual || {};

    return new EffectiveAlarm(
        key,
        value.registration.priority,
        value.registration.category,
        value.registration.rationale,
        value.registration.action,
        value.registration.contact,
        value.registration.filterable,
        value.registration.latching,
        value.registration.ondelay,
        value.registration.offdelay,
        value.registration.class,
        value.registration.location,
        value.registration.maskedby,
        value.registration.screencommand,
        value.registration.producer ? value.registration.producer.pv : undefined,
        value.activation.state,
        value.activation.overrides.disabled ? value.activation.overrides.disabled.comments : undefined,
        value.activation.overrides.ondelayed ? value.activation.overrides.ondelayed.expiration : undefined,
        value.activation.overrides.offdelayed ? value.activation.overrides.offdelayed.expiration: undefined,
        value.activation.overrides.filtered ? value.activation.overrides.filtered.filtername : undefined,
        value.activation.overrides.shelved ? value.activation.overrides.shelved.oneshot : undefined,
        value.activation.overrides.shelved ? value.activation.overrides.shelved.reason : undefined,
        value.activation.overrides.shelved ? value.activation.overrides.shelved.comments : undefined,
        value.activation.overrides.latched ? true : false,
        value.activation.overrides.masked ? true : false,
        value.activation.actual.error,
        value.activation.actual.note,
        value.activation.actual.sevr,
        value.activation.actual.stat
    );
}

let toActivation = function(key, value) {
    return new AlarmActivation(
        key,
        value.msg.error,
        value.msg.note,
        value.msg.sevr,
        value.msg.stat
    );
}

let toCategory = function(key, value) {
    return new AlarmCategory(key);
}

let toClass = function(key, value) {
    return new AlarmClass(
        key,
        value.priority,
        value.category,
        value.rationale,
        value.correctiveaction,
        value.pointofcontactusername,
        value.filterable,
        value.latching,
        value.ondelayseconds,
        value.offdelayseconds
    );
}

let toInstance = function(key, value) {
    return new AlarmInstance(
        key,
        value.class,
        value.location,
        value.maskedby,
        value.screencommand,
        value.producer.pv
    )
}

let toLocation = function(key, value) {
    return new AlarmLocation(
        key,
        value.parent
    )
}

let toNotification = function(key, value) {

    value.overrides = value.overrides || {};
    value.actual = value.actual || {};

    return new EffectiveNotification(
        key,
        value.state,
        value.overrides.disabled ? value.overrides.disabled.comments : undefined,
        value.overrides.ondelayed ? value.overrides.ondelayed.expiration : undefined,
        value.overrides.offdelayed ? value.overrides.offdelayed.expiration : undefined,
        value.overrides.filtered ? value.overrides.filtered.filtername : undefined,
        value.overrides.shelved ? value.overrides.shelved.oneshot : undefined,
        value.overrides.shelved ? value.overrides.shelved.reason : undefined,
        value.overrides.shelved ? value.overrides.shelved.comments : undefined,
        value.overrides.latched ? true : false,
        value.overrides.masked ? true : false,
        value.actual.error,
        value.actual.note,
        value.actual.sevr,
        value.actual.stat
    );
}

let toOverride = function(key, value) {
    return new AlarmOverride(
        key,
        value.msg.comments,
        value.msg.expiration,
        value.msg.filtername,
        value.msg.oneshot,
        value.msg.reason
    )
}

let toRegistration = function(key, value) {
    return new EffectiveRegistration(
        key,
        value.class,
        value.priority,
        value.location,
        value.category,
        value.rationale,
        value.correctiveaction,
        value.pointofcontactusername,
        value.filterable,
        value.latching,
        value.ondelayseconds,
        value.offdelayseconds,
        value.maskedby,
        value.screencommand,
        value.producer.pv
    )
}

const workers = [new BackgroundWorker('alarm', toAlarm, db.alarms),
    new BackgroundWorker('activation', toActivation, db.activations),
    new BackgroundWorker('category', toCategory, db.categories),
    new BackgroundWorker('class', toClass, db.classes),
    new BackgroundWorker('instance', toInstance, db.instances),
    new BackgroundWorker('location', toLocation, db.locations),
    new BackgroundWorker('notification', toNotification, db.notifications),
    new BackgroundWorker('override', toOverride, db.overrides),
    new BackgroundWorker('registration', toRegistration, db.registrations)]

let eventNames = [];

for(const worker of workers) {
    eventNames.push(worker.eventType);
}

let evtSource = null;

async function init() {
    const eventOffsets = await db.positions.bulkGet(eventNames);

    let queryParams = '';

    for(let [index, value] of eventOffsets.entries()) {
        let offset = value === undefined ? -1 : value.position + 1;

        queryParams = queryParams + '&' + eventNames[index] + 'Index=' + offset;
    }

    // Replace first & with ?
    queryParams = '?' + queryParams.substring(1);

    evtSource = new EventSource(contextPath + '/proxy/sse' + queryParams);

    for(const worker of workers) {
        worker.start(evtSource);
    }
}

init().then(()=>{
}).catch(error => {
    console.error(error);
});

onmessage = async function(e) {
    if("clear" === e.data) {
        evtSource.close();

        await db.clear();

        postMessage({type: "cleared"});
    }
}