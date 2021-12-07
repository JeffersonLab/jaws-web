import db from './db.js';
import ui from './ui.js';
import remote from './remote.js';

ui.start();
remote.start();

let classHighOffsetReached = false,
    registrationHighOffsetReached = false,
    effectiveHighOffsetReached = false;

remote.addEventListener("class-highwatermark", async () => {
        await ui.classes.refresh(db.classes);
        classHighOffsetReached = true;
});

remote.addEventListener("class", async () => {
    if(classHighOffsetReached) {
        await ui.classes.refresh(db.classes);
    }
});

remote.addEventListener("registration-highwatermark", async () => {
    await ui.registrations.refresh(db.registrations);
    registrationHighOffsetReached = true;
});

remote.addEventListener("registration", async () => {
    if(registrationHighOffsetReached) {
        await ui.registrations.refresh(db.registrations);
    }
});

remote.addEventListener("effective-highwatermark", async () => {
    await ui.effective.refresh(db.effective);
    effectiveHighOffsetReached = true;
});

remote.addEventListener("effective", async () => {
    if(effectiveHighOffsetReached) {
        await ui.effective.refresh(db.effective);
    }
});