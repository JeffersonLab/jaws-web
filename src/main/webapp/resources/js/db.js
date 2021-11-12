import Dexie from './dexie-3.0.3.js';
import {AlarmClass, AlarmRegistration, EffectiveRegistration, KafkaLogPosition} from "./entities.js";

const db = new Dexie("jaws");

db.version(1).stores({
    classes: "name",
    registrations: "name",
    effective: "name",
    positions: "name, position"
});

db.classes.clear();
db.registrations.clear();
db.effective.clear();
db.positions.clear();

db.classes.mapToClass(AlarmClass);
db.registrations.mapToClass(AlarmRegistration);
db.effective.mapToClass(EffectiveRegistration);
db.positions.mapToClass(KafkaLogPosition);

export default db;