import db from './resources/modules/jaws-admin-gui-@VERSION@/db.mjs';
import {AlarmActivation, AlarmCategory, AlarmClass, AlarmInstance, AlarmLocation, EffectiveRegistration, KafkaLogPosition} from "./resources/modules/jaws-admin-gui-@VERSION@/entities.mjs";

const urlObject = new URL(self.location);
const contextPath = '/' + urlObject.pathname.split('/')[1];

class BackgroundWorker {
    constructor(evtSource, eventType, toEntityFunc, store) {
        let me = this;

        me.evtSource = evtSource;
        me.eventType = eventType;
        me.toEntityFunc = toEntityFunc;
        me.store = store;

        evtSource.addEventListener(me.eventType + "-highwatermark", function (e) {
            postMessage(me.eventType + "-highwatermark");
        });

        evtSource.addEventListener(me.eventType, async (e) => {
            let records = JSON.parse(e.data);

            let remove = [];
            let updateOrAdd = [];

            for (const record of records) {
                let key = record.key;
                let value = record.value;

                if(value == null) {
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

let toActivation = function(key, value) {
    console.log('toActivation', key, value);

    return new AlarmActivation(
        key,
        value.error,
        value.note,
        value.sevr,
        value.stat
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
    const [activationPos, categoryPos, classPos, instancePos, locationPos, registrationPos] = await db.positions.bulkGet(["activation", "category", "class", "instance", "location", "registration"]);

    let activationIndex = activationPos === undefined ? -1 : activationPos.position + 1;
    let categoryIndex = categoryPos === undefined ? -1 : categoryPos.position + 1;
    let classIndex = classPos === undefined ? -1 : classPos.position + 1;
    let instanceIndex = instancePos === undefined ? -1 : instancePos.position + 1;
    let locationIndex = locationPos === undefined ? -1 : locationPos.position + 1;
    let registrationIndex = registrationPos === undefined ? -1 : registrationPos.position + 1;

    //console.log('classIndex: ', classIndex, ', instanceIndex: ', instanceIndex, ', registrationIndex: ', registrationIndex);

    const evtSource = new EventSource(contextPath + '/proxy/sse?activationIndex=' + activationIndex +
        '&categoryIndex=' + categoryIndex +
        '&classIndex=' + classIndex +
        '&instanceIndex=' + instanceIndex +
        '&locationIndex=' + locationIndex +
        '&registrationIndex=' + registrationIndex);

    const workers = [new BackgroundWorker(evtSource,'activation', toActivation, db.activations),
                     new BackgroundWorker(evtSource,'category', toCategory, db.categories),
                     new BackgroundWorker(evtSource, 'class', toClass, db.classes),
                     new BackgroundWorker(evtSource, 'instance', toInstance, db.instances),
                     new BackgroundWorker(evtSource, 'location', toLocation, db.locations),
                     new BackgroundWorker(evtSource, 'registration', toRegistration, db.registrations)]
}

init().then(()=>{
}).catch(error => {
    console.error(error);
});

onmessage = function(e) {
    console.log('Worker: Message received from main script: ', e);
}