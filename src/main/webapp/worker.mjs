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

        me.start = function(evtSource) {
            evtSource.addEventListener(me.eventType + "-highwatermark", function (e) {
                postMessage(me.eventType + "-highwatermark");
            });

            evtSource.addEventListener(me.eventType, async (e) => {
                let records = JSON.parse(e.data);

                let remove = [];
                let updateOrAdd = [];
                let compacted = new Map();

                /* Compact, in-order */
                for(const record of records) {
                    compacted.set(record.key, record.value);
                }

                for (const [key, value] of compacted.entries()) {

                    if(value === null) {
                        remove.push(key);
                    } else {
                        updateOrAdd.push(me.toEntityFunc(key, value));
                    }
                }

                if(remove.length > 0) {
                    await me.store.bulkDelete(remove);
                }

                if(updateOrAdd.length > 0) {
                    await me.store.bulkPut(updateOrAdd);
                }

                if(records.length > 0) {
                    let resumeIndex = records[records.length - 1].offset;
                    await db.positions.put(new KafkaLogPosition(me.eventType, resumeIndex));
                }

                postMessage(me.eventType);
            });
        }
    }
}

let toAlarm = function(key, value) {
    return new EffectiveAlarm(
        key,
        value.state
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
    return new EffectiveNotification(
        key,
        value.state
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

async function init() {
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

    const eventOffsets = await db.positions.bulkGet(eventNames);

    let queryParams = '';

    for(let [index, value] of eventOffsets.entries()) {
        let offset = value === undefined ? -1 : value.position + 1;

        queryParams = queryParams + '&' + eventNames[index] + 'Index=' + offset;
    }

    // Replace first & with ?
    queryParams = '?' + queryParams.substring(1);

    const evtSource = new EventSource(contextPath + '/proxy/sse' + queryParams);

    for(const worker of workers) {
        worker.start(evtSource);
    }
}

init().then(()=>{
}).catch(error => {
    console.error(error);
});

onmessage = function(e) {
    console.log('Worker: Message received from main script: ', e);
}