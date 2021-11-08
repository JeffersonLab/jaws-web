import {AlarmClass, AlarmRegistration, EffectiveRegistration} from "./entities.js";

class Remote {
    start() {
        console.log('starting remote!');
    }
}

const remote = new Remote();

export default remote;