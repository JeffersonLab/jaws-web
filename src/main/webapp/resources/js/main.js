import {AlarmClass, AlarmRegistration, EffectiveRegistration, KafkaLogPosition} from "./entities.js";
import db from './db.js';
import ui from './ui.js';
import remote from './remote.js';

ui.start();
remote.start();

remote.addEventListener("class-highwatermark", async () => {
    await db.classes.toArray().then((data) => ui.classes.setData(data));
});