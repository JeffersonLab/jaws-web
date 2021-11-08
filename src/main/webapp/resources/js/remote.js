import {AlarmClass, AlarmRegistration, EffectiveRegistration} from "./entities.js";

const worker = new Worker('worker.js', {"type": "module"});

worker.onmessage = function(e) {
    console.log('remote onmessage', e.data);
}

class Remote {
    start() {
        console.log('starting remote!');
    }
}

const remote = new Remote();

export default remote;