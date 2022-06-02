import db from './db.mjs';
import ui from './ui.mjs';
import remote from './remote.mjs';

ui.start();
remote.start();

let classHighOffsetReached = false,
    registrationHighOffsetReached = false,
    instanceHighOffsetReached = false,
    locationHighOffsetReached = false,
    categoryHighOffsetReached = false;

remote.addEventListener("registration-highwatermark", async () => {
    await ui.registrations.refresh(db.registrations);
    registrationHighOffsetReached = true;
});

remote.addEventListener("registration", async () => {
    if(registrationHighOffsetReached) {
        await ui.registrations.refresh(db.registrations);
    }
});

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

remote.addEventListener("location-highwatermark", async () => {
    await ui.locations.refresh(db.locations);
    locationHighOffsetReached = true;
});

remote.addEventListener("location", async () => {
    if(locationHighOffsetReached) {
        await ui.locations.refresh(db.locations);
    }
});

remote.addEventListener("category-highwatermark", async () => {
    await ui.categories.refresh(db.categories);
    categoryHighOffsetReached = true;
});

remote.addEventListener("category", async () => {
    if(categoryHighOffsetReached) {
        await ui.categories.refresh(db.categories);
    }
});