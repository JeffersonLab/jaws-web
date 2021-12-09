import db from './resources/js/db.js';
import {AlarmClass, AlarmRegistration, EffectiveRegistration, KafkaLogPosition} from "./resources/js/entities.js";

async function init() {
    const [classPos, regPos, effPos] = await db.positions.bulkGet(["class", "registration", "effective"]);

    let classIndex = classPos === undefined ? -1 : classPos.position + 1;
    let regIndex = regPos === undefined ? -1 : regPos.position + 1;
    let effIndex = effPos === undefined ? -1 : effPos.position + 1;

    //console.log('classIndex: ', classIndex, ', regIndex: ', regIndex, ', effIndex: ', effIndex);

    const evtSource = new EventSource('proxy/sse?classIndex=' + classIndex +
        '&registrationIndex=' + regIndex +
        '&effectiveIndex=' + effIndex);

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
            await db.effective.bulkDelete(remove);
        }

        if(updateOrAdd.length > 0) {
            await db.effective.bulkPut(updateOrAdd);
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

    evtSource.addEventListener("registration-highwatermark", function (e) {
        postMessage("registration-highwatermark");
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