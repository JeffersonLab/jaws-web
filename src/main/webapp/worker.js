import db from './resources/js/db.js';
import {AlarmClass} from "./resources/js/entities.js";

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

        let classes = [];

        for (const record of records) {
            let key = record.key,
                value = record.value;

            console.log('found: ', key, value);
            classes.push(new AlarmClass(key));
        }

        db.classes.bulkPut(classes);
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