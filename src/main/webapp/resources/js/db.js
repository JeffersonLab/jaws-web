import Dexie from './dexie-3.0.3.js';
import {AlarmClass, AlarmRegistration, EffectiveRegistration, KafkaLogPosition} from "./entities.js";

const db = new Dexie("jaws");

db.version(1).stores({
    classes: "name",
    registrations: "name",
    effective: "name",
    kafkaLogPositions: "name, position"
});

db.classes.mapToClass(AlarmClass);
db.registrations.mapToClass(AlarmRegistration);
db.effective.mapToClass(EffectiveRegistration);
db.kafkaLogPositions.mapToClass(KafkaLogPosition);

export default db;