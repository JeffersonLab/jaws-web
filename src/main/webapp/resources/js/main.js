import db from './db.js';
import ui from './ui.js';
import remote from './remote.js';

ui.start();
remote.start();

let classHighOffsetReached = false,
    effectiveHighOffsetReached = false,
    instanceHighOffsetReached = false;

remote.addEventListener("class-highwatermark", async () => {
        await ui.classes.refresh(db.classes);
        classHighOffsetReached = true;
});

remote.addEventListener("class", async () => {
    if(classHighOffsetReached) {
        await ui.classes.refresh(db.classes);
    }
});

remote.addEventListener("instance-highwatermark", async () => {
    await ui.instances.refresh(db.instances);
    instanceHighOffsetReached = true;
});

remote.addEventListener("instance", async () => {
    if(instanceHighOffsetReached) {
        await ui.instances.refresh(db.instances);
    }
});

remote.addEventListener("effective-highwatermark", async () => {
    await ui.effectives.refresh(db.effectives);
    effectiveHighOffsetReached = true;
});

remote.addEventListener("effective", async () => {
    if(effectiveHighOffsetReached) {
        await ui.effectives.refresh(db.effectives);
    }
});