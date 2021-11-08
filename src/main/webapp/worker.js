import db from './resources/js/db.js';

const evtSource = new EventSource('proxy/sse?classIndex=2&registrationIndex=7&effectiveIndex=3');

evtSource.addEventListener("class", function (e) {
    console.log('class (worker)!');

    let records = JSON.parse(e.data);

    for(const record of records) {
        let key = record.key,
            value = record.value;

        console.log('found: ', key, value);
    }
});

evtSource.addEventListener("class-highwatermark", function (e) {
    console.log('class-highwatermark (worker)!');
    postMessage("class-highwatermark");
});

onmessage = function(e) {
    console.log('Worker: Message received from main script: ', e);
}