import {AlarmClass, AlarmRegistration, EffectiveRegistration, KafkaLogPosition} from "./entities.js";
import db from './db.js';
import ui from './ui.js';
import remote from './remote.js';

ui.start();
remote.start();

remote.addEventListener("class-highwatermark", async () => {
    await db.classes.toArray().then((data) => ui.classes.setData(data));
});

remote.addEventListener("registration-highwatermark", async () => {
    await db.registrations.toArray().then((data) => ui.registrations.setData(data));
});

remote.addEventListener("effective-highwatermark", async () => {
    await db.effective.toArray().then((data) => ui.effective.setData(data));
});