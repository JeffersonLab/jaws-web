import {AlarmClass, AlarmRegistration, EffectiveRegistration, KafkaLogPosition} from "./entities.js";
import db from './db.js';
import ui from './ui.js';
import remote from './remote.js';

db.classes.put(new AlarmClass("Testing")).then (function(){
    return db.classes.get('Testing');
}).then(function (classes) {
    alert ("Classes contains: " + classes.name);
}).catch(function(error) {
    alert ("Ooops: " + error);
});

ui.start();
remote.start();