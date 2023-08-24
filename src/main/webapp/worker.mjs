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
        value.registration.location,
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
        value.notification.activation.error,
        value.notification.activation.note,
        value.notification.activation.sevr,
        value.notification.activation.stat
    );
}

let toActivation = function(key, value) {
    return new AlarmActivation(
        key,
        value.union.type,
        value.union.error,
        value.union.note,
        value.union.sevr,
        value.union.stat
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
        value.latchable,
        value.ondelayseconds,
        value.offdelayseconds
    );
}

let toInstance = function(key, value) {
    return new AlarmInstance(
        key,
        value.alarmclass,
        value.device,
        value.location,
        value.maskedby,
        value.screencommand,
        value.source.pv
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
    value.activation = value.activation || {};

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
        value.activation.type,
        value.activation.error,
        value.activation.note,
        value.activation.sevr,
        value.activation.stat
    );
}

let toOverride = function(key, value) {
    return new AlarmOverride(
        key,
        value.union.comments,
        value.union.expiration,
        value.union.filtername,
        value.union.oneshot,
        value.union.reason
    )
}

let toRegistration = function(key, value) {
    return new EffectiveRegistration(
        key,
        value.alarmclass,
        value.device,
        value.priority,
        value.location,
        value.category,
        value.rationale,
        value.correctiveaction,
        value.pointofcontactusername,
        value.filterable,
        value.latchable,
        value.ondelayseconds,
        value.offdelayseconds,
        value.maskedby,
        value.screencommand,
        value.source.pv
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

    // + '&entitiesCsv=alarm&initiallyActiveOnly=true'

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