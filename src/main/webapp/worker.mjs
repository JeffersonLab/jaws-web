import db from './resources/modules/jaws-admin-gui-@VERSION@/db.mjs';
import {AlarmCategory, AlarmClass, AlarmInstance, AlarmLocation, EffectiveRegistration, KafkaLogPosition} from "./resources/modules/jaws-admin-gui-@VERSION@/entities.mjs";

const urlObject = new URL(self.location);
const contextPath = '/' + urlObject.pathname.split('/')[1];

async function init() {
    const [categoryPos, classPos, instancePos, locationPos, registrationPos] = await db.positions.bulkGet(["category", "class", "instance", "location", "registration"]);

    let categoryIndex = categoryPos === undefined ? -1 : categoryPos.position + 1;
    let classIndex = classPos === undefined ? -1 : classPos.position + 1;
    let instanceIndex = instancePos === undefined ? -1 : instancePos.position + 1;
    let locationIndex = locationPos === undefined ? -1 : locationPos.position + 1;
    let registrationIndex = registrationPos === undefined ? -1 : registrationPos.position + 1;

    //console.log('classIndex: ', classIndex, ', instanceIndex: ', instanceIndex, ', registrationIndex: ', registrationIndex);

    const evtSource = new EventSource(contextPath + '/proxy/sse?categoryIndex=' + categoryIndex +
        '&classIndex=' + classIndex +
        '&instanceIndex=' + instanceIndex +
        '&locationIndex=' + locationIndex +
        '&registrationIndex=' + registrationIndex);

    evtSource.addEventListener("category", async (e) => {
        let records = JSON.parse(e.data);

        let remove = [];
        let updateOrAdd = [];

        for (const record of records) {
            let key = record.key;
            let value = record.value;

            if(value == null) {
                remove.push(key);
            } else {
                updateOrAdd.push(new AlarmCategory(key));
            }
        }

        if(remove.length > 0) {
            await db.categories.bulkDelete(remove);
        }

        if(updateOrAdd.length > 0) {
            await db.categories.bulkPut(updateOrAdd);
        }

        if(records.length > 0) {
            let resumeIndex = records[records.length - 1].offset;
            await db.positions.put(new KafkaLogPosition('category', resumeIndex));
            //console.log('Saved category resume index: ', resumeIndex);
        }

        postMessage("category");
    });

    evtSource.addEventListener("class", async (e) => {
        let records = JSON.parse(e.data);

        let remove = [];
        let updateOrAdd = [];

        for (const record of records) {
            let key = record.key;
            let value = record.value;

            if(value == null) {
                remove.push(key);
            } else {
                updateOrAdd.push(new AlarmClass(
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
                ));
            }
        }

        if(remove.length > 0) {
            await db.classes.bulkDelete(remove);
        }

        if(updateOrAdd.length > 0) {
            await db.classes.bulkPut(updateOrAdd);
        }

        if(records.length > 0) {
            let resumeIndex = records[records.length - 1].offset;
            await db.positions.put(new KafkaLogPosition('class', resumeIndex));
            //console.log('Saved class resume index: ', resumeIndex);
        }

        postMessage("class");
    });

    evtSource.addEventListener("instance", async (e) => {
        let records = JSON.parse(e.data);

        let remove = [];
        let updateOrAdd = [];

        for (const record of records) {
            let key = record.key,
                value = record.value;

            if(value == null) {
                remove.push(key);
            } else {
                updateOrAdd.push(new AlarmInstance(
                    key,
                    value.class,
                    value.location,
                    value.maskedby,
                    value.screencommand,
                    value.producer.pv
                ));
            }
        }

        if(remove.length > 0) {
            await db.instances.bulkDelete(remove);
        }

        if(updateOrAdd.length > 0) {
            await db.instances.bulkPut(updateOrAdd);
        }

        if(records.length > 0) {
            let resumeIndex = records[records.length - 1].offset;
            await db.positions.put(new KafkaLogPosition('instance', resumeIndex));
            //console.log('Saved instance resume index: ', resumeIndex);
        }

        postMessage("instance");
    });

    evtSource.addEventListener("location", async (e) => {
        let records = JSON.parse(e.data);

        let remove = [];
        let updateOrAdd = [];

        for (const record of records) {
            let key = record.key;
            let value = record.value;

            if(value == null) {
                remove.push(key);
            } else {
                updateOrAdd.push(new AlarmLocation(
                    key,
                    value.parent
                ));
            }
        }

        if(remove.length > 0) {
            await db.locations.bulkDelete(remove);
        }

        if(updateOrAdd.length > 0) {
            await db.locations.bulkPut(updateOrAdd);
        }

        if(records.length > 0) {
            let resumeIndex = records[records.length - 1].offset;
            await db.positions.put(new KafkaLogPosition('location', resumeIndex));
            //console.log('Saved location resume index: ', resumeIndex);
        }

        postMessage("location");
    });

    evtSource.addEventListener("registration", async (e) => {
        let records = JSON.parse(e.data);

        let remove = [];
        let updateOrAdd = [];

        for (const record of records) {
            let key = record.key,
                value = record.value;

            if(value == null) {
                remove.push(key);
            } else {
                updateOrAdd.push(new EffectiveRegistration(
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
                ));
            }
        }

        if(remove.length > 0) {
            await db.registrations.bulkDelete(remove);
        }

        if(updateOrAdd.length > 0) {
            await db.registrations.bulkPut(updateOrAdd);
        }

        if(records.length > 0) {
            let resumeIndex = records[records.length - 1].offset;
            await db.positions.put(new KafkaLogPosition('registration', resumeIndex));
            //console.log('Saved registration resume index: ', resumeIndex);
        }

        postMessage("registration");
    });

    evtSource.addEventListener("category-highwatermark", function (e) {
        postMessage("category-highwatermark");
    });

    evtSource.addEventListener("class-highwatermark", function (e) {
        postMessage("class-highwatermark");
    });

    evtSource.addEventListener("instance-highwatermark", function (e) {
        postMessage("instance-highwatermark");
    });

    evtSource.addEventListener("location-highwatermark", function (e) {
        postMessage("location-highwatermark");
    });

    evtSource.addEventListener("registration-highwatermark", function (e) {
        postMessage("registration-highwatermark");
    });
}

init().then(()=>{
}).catch(error => {
    console.error(error);
});

onmessage = function(e) {
    console.log('Worker: Message received from main script: ', e);
}