import db from './resources/js/db.js';
import {AlarmClass} from "./resources/js/entities.js";

let unwrapNullableUnionText = function (text) {
    if (text != null) {
        text = Object.values(text)[0];
    }
    return text;
};

async function init() {
    const [classPos, regPos, effPos] = await db.positions.bulkGet(["class", "registration", "effective"]);

    let classIndex = classPos === undefined ? -1 : classPos.position;
    let regIndex = regPos === undefined ? -1 : regPos.position;
    let effIndex = effPos === undefined ? -1 : effPos.position;

    const evtSource = new EventSource('proxy/sse?classIndex=' + classIndex +
        '&registrationIndex=' + regIndex +
        '&effectiveIndex=' + effIndex);

    evtSource.addEventListener("class", function (e) {
        console.log('class (worker)!', e.data);

        let records = JSON.parse(e.data);

        let classset = new Map();

        // TODO: update EventSourceTable to resolve duplicates AND provide separate add/remove arrays
        // Also, would be nice if union encoding (unwrapNullableUnionText) was done server-side...

        // Resolve duplicate keys;
        for(const record of records) {
            classset.set(record.key, record.value);
        }

        let remove = [];
        let updateOrAdd = [];

        let keys = classset.keys();

        for (const key of keys) {
            let value = classset.get(key);

            console.log('found: ', key, value);

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
                    unwrapNullableUnionText(value.ondelayseconds),
                    unwrapNullableUnionText(value.offdelayseconds),
                    unwrapNullableUnionText(value.maskedby),
                    value.screenpath
                ));
            }
        }

        if(remove.length > 0) {
            db.classes.bulkDelete(remove);
        }

        if(updateOrAdd.length > 0) {
            db.classes.bulkPut(updateOrAdd);
        }

        postMessage("class");
    });

    evtSource.addEventListener("class-highwatermark", function (e) {
        console.log('class-highwatermark (worker)!');
        postMessage("class-highwatermark");
    });
}

init().then(()=>{
    console.log("success");
}).catch(error => {
    console.error(error);
});

onmessage = function(e) {
    console.log('Worker: Message received from main script: ', e);
}