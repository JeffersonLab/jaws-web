import db from './resources/js/db.js';
import {AlarmClass, AlarmRegistration, EffectiveRegistration, KafkaLogPosition} from "./resources/js/entities.js";

async function init() {
    const [classPos, instancePos, effectivePos] = await db.positions.bulkGet(["class", "instance", "effective"]);

    let classIndex = classPos === undefined ? -1 : classPos.position + 1;
    let instanceIndex = instancePos === undefined ? -1 : instancePos.position + 1;
    let effectiveIndex = effectivePos === undefined ? -1 : effectivePos.position + 1;

    //console.log('classIndex: ', classIndex, ', instanceIndex: ', instanceIndex, ', effectiveIndex: ', effectiveIndex);

    const evtSource = new EventSource('proxy/sse?classIndex=' + classIndex +
        '&instanceIndex=' + instanceIndex +
        '&effectiveIndex=' + effectiveIndex);

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
                    value.screenpath
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
                updateOrAdd.push(new AlarmRegistration(
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
                    value.screenpath,
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

    evtSource.addEventListener("effective", async (e) => {
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
                    value.screenpath,
                    value.producer.pv
                ));
            }
        }

        if(remove.length > 0) {
            await db.effectives.bulkDelete(remove);
        }

        if(updateOrAdd.length > 0) {
            await db.effectives.bulkPut(updateOrAdd);
        }

        if(records.length > 0) {
            let resumeIndex = records[records.length - 1].offset;
            await db.positions.put(new KafkaLogPosition('effective', resumeIndex));
            //console.log('Saved effective resume index: ', resumeIndex);
        }

        postMessage("effective");
    });

    evtSource.addEventListener("class-highwatermark", function (e) {
        postMessage("class-highwatermark");
    });

    evtSource.addEventListener("instance-highwatermark", function (e) {
        postMessage("instance-highwatermark");
    });

    evtSource.addEventListener("effective-highwatermark", function (e) {
        postMessage("effective-highwatermark");
    });
}

init().then(()=>{
}).catch(error => {
    console.error(error);
});

onmessage = function(e) {
    console.log('Worker: Message received from main script: ', e);
}